# Multi-language database planning

## Goal

Support more than one target language (Castilian Spanish now, Catalan next, possibly more later) from a **single running instance** of the backend, while keeping the setup as simple and easy to maintain as possible.

## Current state

- Spring Boot + Spring Data JPA, three entities: `Subject`, `Vocab`, `Lesson`.
- `Vocab` and `Lesson` both hang off a `Subject` (`Vocab` many-to-one, `Lesson` one-to-one).
- Local dev uses **SQLite** (`language.db`), production uses **Postgres on Supabase**.
- Schema is managed by `spring.jpa.hibernate.ddl-auto=update`.
- The vocab table has Spanish-specific column naming: `esp` / `eng`.

## Recommendation: one set of tables with a `language` column

Rather than one schema per language, keep the existing tables and **tag each subject with the language it belongs to**. Because vocab and lessons already belong to a subject, they inherit the language through that relationship, so only one table needs a new column.

```
subject (id, subject_name, language)   <-- new column: 'ES' or 'CA'
  ├── vocab  (id, subject_id, term, eng)
  └── lesson (id, subject_id, lesson_title, body, created_at)
```

The client chooses the language through the URL, e.g. `/es/subject/getall` vs `/ca/subject/getall`, and every query filters on it.

### Why this over separate schemas

| Concern | `language` column (recommended) | Schema per language |
|---|---|---|
| Code changes | One enum, one field, a few repository methods | Hibernate multi-tenancy wiring (`CurrentTenantIdentifierResolver` + `MultiTenantConnectionProvider`), request-scoped tenant context |
| Local dev (SQLite) | Works unchanged | SQLite has no schemas, so local dev would behave differently from prod |
| `ddl-auto=update` | Keeps working | Only manages the default schema; you'd need Flyway/Liquibase applied to each schema |
| Adding a column later | Done once | Done once **per language** |
| Adding a new language | Add an enum value | Create a schema, run all migrations into it, register it |
| Supabase | No concerns | `search_path` switching can misbehave with Supabase's pooler in transaction mode |
| Cross-language features later (user progress, stats, "all my languages") | Simple queries | Queries across schemas |

The main thing separate schemas give you is hard isolation (separate backups, no chance of a query leaking across languages). For a single-owner learning app that isn't worth the extra moving parts right now. The column approach can still be migrated to schemas later if that need ever appears.

## Implementation steps

### 1. Add a `Language` enum

```java
package com.language.vocab.model;

public enum Language {
    ES, // Castilian
    CA  // Catalan
}
```

Adding another language later is just a new enum value.

### 2. Add the column to `Subject`

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Language language;
// + getter/setter
```

`EnumType.STRING` stores `'ES'` / `'CA'`, which is readable in the DB and safe if the enum is reordered.

`Vocab` and `Lesson` do **not** need the column; they get their language from their subject.

### 3. Rename the Spanish-specific vocab field (recommended)

`esp` doesn't make sense for Catalan words. Rename the field to something neutral, e.g. `term` (the word in the language being learned), and keep `eng` as the translation.

Note: `ddl-auto=update` won't rename columns. It would add a new empty `term` column and leave `esp` behind, so rename it manually (see step 6). This also changes the JSON field name in the API (`esp` → `term`), so the frontend needs updating at the same time. If you'd rather not touch the frontend yet, keep the Java/JSON name and skip this step. It's cosmetic, not required.

### 4. Put the language in the URL

Add a `{language}` prefix at the class level of each controller:

```java
@RestController
@RequestMapping("/{language}/vocab")
public class VocabController {

    @GetMapping("/getall")
    public List<Vocab> getVocabAll(@PathVariable Language language) {
        return vocabService.getAllVocab(language);
    }
    ...
}
```

Spring converts the path segment to the enum automatically, but it is case-sensitive (`/ES/...`). To allow lowercase `/es/...`, register one small converter:

```java
@Component
public class LanguageConverter implements Converter<String, Language> {
    @Override
    public Language convert(String source) {
        return Language.valueOf(source.toUpperCase());
    }
}
```

An unknown language (e.g. `/fr/...`) throws, which can be mapped to a 400/404 in `GlobalExceptionHandler`.

A path prefix is better than a header or query param here: it's visible, easy to test in a browser or Postman, and the frontend just builds URLs from the currently selected language.

### 5. Filter by language in repositories and services

Spring Data can filter through the subject relationship with derived query names, so no SQL is needed:

```java
public interface SubjectRespository extends JpaRepository<Subject, Long> {
    List<Subject> findByLanguage(Language language);
    Optional<Subject> findByIdAndLanguage(Long id, Language language);
}

public interface VocabRepository extends JpaRepository<Vocab, Long> {
    List<Vocab> findBySubject(Subject subject);
    List<Vocab> findBySubject_Language(Language language);
    Optional<Vocab> findByIdAndSubject_Language(Long id, Language language);
}

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Lesson findBySubject(Subject subject);
    List<Lesson> findBySubject_Language(Language language);
    Optional<Lesson> findByIdAndSubject_Language(Long id, Language language);
}
```

In the services:

- **List endpoints** (`getall`): use the `findBy..._Language` methods instead of `findAll()`.
- **Get/update/delete by id**: use `findByIdAnd..._Language` so `/ca/vocab/get/5` can't return a Spanish word. A miss throws `ResourceNotFoundException` as it does now.
- **Create subject**: set `subject.setLanguage(language)` from the path.
- **Create vocab/lesson**: look up the parent with `subjectRespository.findByIdAndLanguage(subjectId, language)`. This stops a Catalan word from being attached to a Spanish subject.

IDs stay globally unique across languages. That's fine, since nothing requires per-language numbering.

### 6. Migrate existing data

Run this once against **both** the local SQLite DB and Supabase.

Deploy the code first with the column temporarily nullable (`@Column(nullable = true)`) so `ddl-auto=update` can add it to a table that already has rows, then:

```sql
-- Existing data is all Castilian
UPDATE subject SET language = 'ES' WHERE language IS NULL;

-- Only if doing step 3
ALTER TABLE vocab RENAME COLUMN esp TO term;
```

(`RENAME COLUMN` works in both Postgres and SQLite ≥ 3.25.)

After that, switch the annotation back to `nullable = false` for new rows. Hibernate's `update` mode won't add a NOT NULL constraint to an existing column. If you want it enforced at the DB level in Supabase, run:

```sql
ALTER TABLE subject ALTER COLUMN language SET NOT NULL;
```

Take a backup before running these (Supabase dashboard backup, and copy `language.db` locally).

### 7. Frontend

- Hold the "current language" in app state (a toggle/dropdown).
- Prefix every API call with it: `/${lang}/subject/getall`.
- Rename `esp` → `term` in request/response handling if step 3 was done.

## Summary of changes

| File | Change |
|---|---|
| `model/Language.java` | New enum |
| `model/Subject.java` | Add `language` field |
| `model/Vocab.java`, `CreateVocab`, `VocabSummary` | Optional `esp` → `term` rename |
| `config/LanguageConverter.java` | New, lowercase URL support |
| `controller/*Controller.java` | `/{language}` prefix + `@PathVariable Language` |
| `repository/*.java` | Add language-filtered finder methods |
| `service/*.java` | Pass `language` through and use filtered finders |
| DB (SQLite + Supabase) | One-off `UPDATE` (and optional `RENAME COLUMN`) |

## When to revisit

Consider moving to schema-per-language (or separate databases) only if one of these becomes true:

- Different languages need **structurally different tables** (e.g. Catalan needs columns Spanish doesn't).
- You need **separate backups/restores** or strict data isolation per language.
- Data volume per language grows large enough that sharing tables causes performance problems (very unlikely at this scale; an index on `subject.language` would cover it).

## Optional, later: migration tooling

`ddl-auto=update` is convenient now, but it can't rename columns or tighten constraints (as step 6 shows). Once the schema starts changing more often, switching to **Flyway** (versioned `.sql` files in `src/main/resources/db/migration`, applied automatically on startup) would make changes like this repeatable across SQLite and Supabase without manual SQL. It isn't needed for this change.
