-- =====================================================================
-- set_subject_levels.sql
--
-- One-off backfill for the subject.level column added in 1.0.4.
-- Sets the level of every existing subject that has none:
--   * Castilian Spanish ('ES') -> 'B2'
--   * Catalan ('CA')           -> 'A1'
--
-- Works on both SQLite (local) and Postgres (Supabase).
-- The column must exist first: start the app once with the new code and
-- Hibernate (ddl-auto=update) adds it. Safe to re-run: rows that already
-- have a level are left alone.
--
--   SQLite:   sqlite3 language.db < db/set_subject_levels.sql
--   Supabase: run in the SQL editor (or psql)
--
-- Not needed after db/reset_and_seed.sql, which sets the level itself.
-- =====================================================================

BEGIN;

UPDATE subject SET level = 'B2' WHERE language = 'ES' AND level IS NULL;
UPDATE subject SET level = 'A1' WHERE language = 'CA' AND level IS NULL;

COMMIT;
