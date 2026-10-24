package com.moviehub.movie.tmdb

/**
 * Clean, GraphQL-facing DTO for a TMDb movie search result.
 * This is what gets returned to the frontend via the `searchTmdbMovies` query.
 *
 * Poster URL is pre-built by TmdbClient: imageBaseUrl + poster_path
 * e.g. "https://image.tmdb.org/t/p/w500/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg"
 */
data class TmdbSearchResult(
    /** TMDb movie ID (not stored in our DB — used only for deduplication on the frontend) */
    val tmdbId: String,

    val title: String,

    /** Extracted from release_date (e.g. "1999-10-15" → 1999). Null if date is empty. */
    val releaseYear: Int?,

    /** Full pre-built poster URL, or null if TMDb has no poster for this movie. */
    val posterUrl: String?,

    val overview: String
)
