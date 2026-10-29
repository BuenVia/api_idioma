# Language Learning API — Known Quirks (v1.0.3)

> **Audience:** an AI assistant (or developer) working on this backend or on a client that calls it.
> **Scope:** behaviour that is surprising, inconsistent, or risky. Covers the API contract, data integrity, configuration, and code hygiene. For the endpoint reference itself, see `docs/API_ENDPOINTS.md`.
> **How this was produced:** code read on branch `1.0.3` (including uncommitted changes and the fixed `SubjectService.deleteSubject`). Items marked **[verified]** were reproduced against the running app with curl on 2026-10-29, using a throwaway SQLite database.
>
> Paths are relative to `src/main/java/com/language/vocab/` unless they start with the repo root.

Severity key: 🔴 can lose/corrupt data or break a deployment · 🟠 wrong or inconsistent behaviour a client will hit · 🟡 cosmetic / misleading · ⚪ code hygiene

---

## 1. Data integrity

### 1.1 🔴 Lesson body is capped at 255 characters in Postgres
`Lesson.body` (and every other `String` field) has no `@Column(length=…)` / `@Lob`, so Hibernate creates `varchar(255)`. SQLite ignores the length, so long lessons work locally. Postgres (prod/Supabase) enforces it, so **saving a lesson longer than 255 characters fails with a `500` in production**. The same limit applies to `subjectName`, `term`, `translation`, `lessonTitle`.
- Where: `model/Lesson.java` (`body`). The local schema confirms `body varchar(255)`.
- Fix: `@Column(columnDefinition = "TEXT")` on `body`, plus `ALTER TABLE lesson ALTER COLUMN body TYPE TEXT;` on Supabase, because `ddl-auto=update` won't widen an existing column.

### 1.2 🔴 No transactions: multi-step writes can half-complete
No method is `@Transactional`, so each repository call commits on its own. `SubjectService.deleteSubject` deletes vocab, then the lesson, then the subject in three separate commits. If a later step fails, earlier deletes are **not** rolled back. With the current fix the lesson step can't realistically fail, but any future change (or a DB error) would leave a subject with its vocab gone.
- Where: `service/SubjectService.java:69-89`.
- Fix: annotate `deleteSubject` (and ideally every write method) with `@Transactional`.

### 1.3 🟠 Lesson endpoints ignore the language for subject lookups
Lesson create, update, and getbysubject look the subject up with `findById` instead of `findByIdAndLanguage`, so a lesson can be **attached to a subject in the other language**. **[verified]**: `POST /es/lesson/create` with a Catalan `subjectId` succeeded. `dbplanning.md` (step 5) says these lookups should be language-filtered; vocab does it, lessons don't.
- Where: `service/LessonService.java:37` (getbysubject), `:44` (create), `:56` (update).
- Fix: use `subjectRespository.findByIdAndLanguage(subjectId, language)` in all three.

### 1.4 🟠 SQLite reuses ids after deletes (local only)
SQLite tables use `INTEGER PRIMARY KEY` without `AUTOINCREMENT`, so deleting the highest-id row lets the next insert reuse that id. **[verified]**: after deleting subject 3, the next created subject was also id 3. A client caching by id can show stale data locally. Postgres `IDENTITY` doesn't reuse ids, so local and prod behave differently.

### 1.5 🟠 No input validation
No `@Valid` / `@NotBlank` anywhere. `POST /subject/create` with `{}` creates a subject named `null`. **[verified]** Vocab with null `term`/`translation`, and lessons with null `body`, are accepted. Duplicate subject names are allowed. **[verified]**

### 1.6 🟡 Stale `note_id` column on `subject`
The local schema still has `subject.note_id` from the old "notes" feature (renamed to lesson in 1.0.2). `ddl-auto=update` never drops columns. Harmless, but it's noise and probably exists in Supabase too.

### 1.7 🟡 Multi-language migration plan only renames half the vocab columns
`dbplanning.md` step 6 renames `esp → term` but keeps `eng`. The code went further and also renamed `eng → translation`. If the prod DB was migrated by following the plan as written, `vocab.eng` still holds the data and `ddl-auto` will have added an empty `translation` column, so **every translation would come back `null` in prod**. Check Supabase for `vocab.eng`. If it's there: `ALTER TABLE vocab RENAME COLUMN eng TO translation;` (drop the empty column first if Hibernate created one).

---

## 2. API contract inconsistencies

### 2.1 🟠 Same resource, different shapes per endpoint
| Resource | Shape | Has `id` as | Has `subjectId` | Has `createdAt` | Returned by |
|---|---|---|---|---|---|
| Subject | `Subject` entity | `id` | n/a | n/a | getall, get, update |
| Subject | `SubjectSummary` | `id` | n/a | n/a | getsubjectnames |
| Subject | `ReadSubject` | **`subjectId`** | n/a | n/a | create |
| Vocab | `Vocab` entity | `id` | ❌ | n/a | getall, create, update |
| Vocab | `VocabSummary` | `id` | ✅ | n/a | get |
| Lesson | `Lesson` entity | `id` | ❌ | ✅ | getbysubject, create, update |
| Lesson | `LessonSummary` | `id` | ✅ | ❌ | get |

Clients have to know which shape each call returns. Returning one DTO per resource would remove this.

### 2.2 🟠 `GET /lesson/getbysubject/{id}` returns 200 with an empty body
When the subject has no lesson, the controller returns `null` and Spring sends `200` with no body (not `null`, not `404`). **[verified]** Calling `response.json()` on it throws.
- Where: `controller/LessonController.java:26` / `service/LessonService.java:40`.

### 2.3 🟠 Missing `subjectId` → inconsistent errors
| Call | Without `subjectId` |
|---|---|
| `POST /vocab/create` | `404 "Vocab not found with id: null"` **[verified]** |
| `PUT /vocab/update/{id}` | `404 "Subject not found with id: null"` **[verified]** |
| `POST /lesson/create` | `500` (`findById(null)` throws) **[verified]** |
| `PUT /lesson/update/{id}` | `500` **[verified]** |

### 2.4 🟠 Second lesson for a subject → 500
`lesson.subject_id` is `UNIQUE` (one-to-one), but nothing checks this first, so the DB constraint surfaces as a generic `500`. **[verified]** `PUT /lesson/update/{id}` that moves a lesson to a subject that already has one fails the same way.

### 2.5 🟡 404 messages name the wrong resource
| Where | Situation | Message says |
|---|---|---|
| `service/VocabService.java:36` | vocab id not found | `"Subject not found with id: X"` |
| `service/VocabService.java:54` | subject not found on vocab create | `"Vocab not found with id: <subjectId>"` |
| `service/LessonService.java:57` | subject not found on lesson update | `"lesson not found with id: <lessonId>"` |
| `service/SubjectService.java:79` | lesson missing during subject delete | uses the subject id |

Casing is also mixed: `"Subject"` vs `"lesson"`.

### 2.6 🟡 Two different error body formats
404s come from `GlobalExceptionHandler` as `{status, message, timestamp}` with a **local, zone-less** timestamp (`2026-10-29T18:03:13.157504869`). Every other error (400/405/415/500) is Spring's default `{timestamp, status, error, path}` with a **UTC** timestamp (`…Z`). **[verified]**

### 2.7 🟡 Create returns 200, not 201
All three `create` endpoints return `200 OK` with no `Location` header.

### 2.8 🟡 RPC-style routes
Routes use verbs (`/get/{id}`, `/create`, `/update/{id}`, `/delete/{id}`, `/getall`) rather than REST resource paths. Fine as long as it's consistent, but `getsubjectnames` and `getbysubject` break the pattern further.

### 2.9 🟡 `createdAt` accepted on create but ignored
`CreateLesson` has a `createdAt` field (`model/CreateLesson.java:10`), but `LessonController.createLesson` never copies it. The server always sets the time. Lesson `update` can't change it either.

### 2.10 🟡 `vocab/getall` can't be grouped by subject
It returns a flat list without `subjectId`. To show vocab per subject you have to use `subject/getall`, which returns everything (all subjects, lessons, and vocab) in one payload.

---

## 3. Configuration & deployment

### 3.1 🔴 IntelliJ run configs use profiles that don't exist
`.run/Language - Production.run.xml` sets profile `production` and `.run/Language - Staging.run.xml` sets `staging`, but the only profile files are `application-local.properties` and `application-production.properties`. The `SPRING_PROFILES_ACTIVE` env var overrides `spring.profiles.active=local`, so neither the local nor the prod datasource settings load, and the app fails to start with no datasource URL.
- Fix: rename the profile in the run configs to `prod`, or rename the file to `application-production.properties`. Add an `application-staging.properties` if staging is real.

### 3.2 🔴 Docker image defaults to the local SQLite profile
`application.properties` sets `spring.profiles.active=local`, and the `Dockerfile` doesn't override it. The container uses SQLite (a fresh `language.db` inside the container, lost on redeploy) **unless the host sets `SPRING_PROFILES_ACTIVE=prod`**. Make sure the hosting platform sets it, or set `ENV SPRING_PROFILES_ACTIVE=prod` in the Dockerfile.

### 3.3 🟠 `EXPOSE 10000` vs port 8080
The `Dockerfile` exposes 10000, but the app listens on `${PORT:8080}` (prod) or 8080 (local). It works on hosts that inject `PORT=10000` (e.g. Render). Anywhere else, the exposed port and the listening port won't match.

### 3.4 🟠 Ignoring `application.properties` has no effect
The uncommitted `.gitignore` change adds `src/main/resources/application.properties`, but that file is **already tracked**, so git keeps tracking it. To stop tracking it: `git rm --cached src/main/resources/application.properties`. Note that fresh clones (and any host that builds the Docker image from git) would then have no `application.properties`, so `spring.application.name` and `spring.profiles.active` would need to come from somewhere else, such as env vars.

### 3.5 🟠 No CORS configuration
No `@CrossOrigin` or `WebMvcConfigurer`. **[verified]**: responses carry no `Access-Control-Allow-Origin` header, so a browser app on another origin is blocked. It only works through a same-origin proxy or a native client.

### 3.6 🟠 Database files committed to git
`language.db` is tracked (and modified in the working tree), and `backup.sql` is committed. `backup_language.db` is untracked but sits in the repo root. Every local data change shows up as a binary diff. Consider ignoring `*.db` and `git rm --cached language.db`.

### 3.7 🟡 `spring.jpa.show-sql=true` everywhere, including prod
It's set in `application.properties`, `application-local.properties`, and `application-production.properties`. Every SQL statement gets logged in production.

### 3.8 🟡 `spring.jpa.open-in-view` left at the default (enabled)
Spring logs a warning at startup. **[verified]** The API depends on it: `Subject.vocabs` is lazy and gets loaded while JSON is serialised, after the service method returns. If open-in-view is turned off without adding `@Transactional`/fetch joins, `getall`/`get` will throw `LazyInitializationException`.

### 3.9 🟡 `ddl-auto=update` in production
The schema is managed by Hibernate's `update` mode. It can't rename or drop columns or change constraints (see 1.1, 1.6, 1.7), so prod schema changes need manual SQL, and it's easy for local and prod to drift. `dbplanning.md` already suggests Flyway.

### 3.10 🟡 Language parsing depends on the JVM locale
`config/LanguageConverter.java:10` uses `source.toUpperCase()` without a locale. On a Turkish-locale JVM, `"ca"`/`"es"` still work, but any future code containing `i` (e.g. `it`) would uppercase to `İT` and fail. Use `toUpperCase(Locale.ROOT)`.

---

## 4. Testing

### 4.1 🟠 Only a context-load test
`src/test/java/com/language/LanguageApplicationTests.java` contains just `contextLoads()`. No endpoint, service, or repository tests. It also runs against the `local` profile, so it opens the real `language.db` in the repo root.

---

## 5. Code hygiene

| # | Item | Where |
|---|---|---|
| 5.1 | ⚪ `SubjectRespository` is misspelled (Respository) | `repository/SubjectRespository.java` and every injection site |
| 5.2 | ⚪ `ReadLesson` is unused, and its accessors don't follow bean naming (`getlessonTitle`, `setlessonId`) | `model/ReadLesson.java` |
| 5.3 | ⚪ `VocabService.getAllVocabBySubject` is unused, and its `language` parameter is ignored | `service/VocabService.java:48` |
| 5.4 | ⚪ `language` parameter is unused in `LessonService.getLessonBySubjectId` and `createLesson` (see 1.3) | `service/LessonService.java` |
| 5.5 | ⚪ Commented-out code | `service/VocabService.java:66` |
| 5.6 | ⚪ Mixed injection styles: field `@Autowired` in `SubjectController`/`VocabController`, constructor injection elsewhere | `controller/` |
| 5.7 | ⚪ Unused import `org.springframework.stereotype.Component` | `controller/LessonController.java` |
| 5.8 | ⚪ `VocabService` depends on both `SubjectRespository` and `SubjectService` for the same lookups | `service/VocabService.java:19-21` |
| 5.9 | ⚪ Business logic in controllers: `create` endpoints build entities in the controller, other endpoints pass DTOs to the service | `controller/VocabController.java`, `controller/LessonController.java` |
| 5.10 | ⚪ Services return `ResponseEntity` from delete methods (HTTP concerns in the service layer) | all three services |
| 5.11 | ⚪ `Lesson.subject` is `@JsonIgnore` but `Subject.lesson` isn't, which is why lesson responses lack `subjectId` (see 2.1) | `model/Lesson.java`, `model/Vocab.java` |
| 5.12 | ⚪ Handler method named `handleSubjectNotFound` handles all resources | `exception/GlobalExceptionHandler.java` |
| 5.13 | ⚪ `pom.xml` has empty `<url/>`, `<licenses>`, `<developers>`, `<scm>` placeholders, and a redundant `spring-boot-starter` alongside `starter-web` | `pom.xml` |

---

## 6. Resolved since the last review

- ✅ **`SubjectService.deleteSubject` lesson lookup.** It previously used the subject id as the lesson id, which returned 404 for most subjects and could delete another subject's lesson. It now uses `findSubject.getLesson().getId()` and skips the step when there's no lesson. **[verified]**: subjects with no lesson, with a lesson whose id differs from the subject id, and with only vocab all delete with `204`, and other subjects' lessons are untouched. The remaining risk is the missing transaction (1.2).
