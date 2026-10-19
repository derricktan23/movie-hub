-- ============================================================================
-- 01: Create Databases (Database-per-Service Pattern)
-- ============================================================================

CREATE DATABASE movie_db;
CREATE DATABASE ppl_db;

GRANT ALL PRIVILEGES ON DATABASE movie_db TO postgres;
GRANT ALL PRIVILEGES ON DATABASE ppl_db TO postgres;
