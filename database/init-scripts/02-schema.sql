-- ----------------------------------------------------------------------------
-- Movie Service Database (movie_db)
-- ----------------------------------------------------------------------------
\c movie_db;

CREATE TABLE IF NOT EXISTS movies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    artwork_url TEXT,
    release_year INT
);

CREATE TABLE IF NOT EXISTS movie_cast (
    movie_id UUID NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    cast_id UUID NOT NULL,
    PRIMARY KEY (movie_id, cast_id)
);

-- ----------------------------------------------------------------------------
-- People Service Database (ppl_db)
-- ----------------------------------------------------------------------------
\c ppl_db;

CREATE TABLE IF NOT EXISTS people (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
