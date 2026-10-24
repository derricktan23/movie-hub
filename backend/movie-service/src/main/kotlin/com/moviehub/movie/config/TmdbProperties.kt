package com.moviehub.movie.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Binds the `tmdb.*` configuration block from application.yml.
 *
 * Values are injected from environment variables via:
 *   tmdb.read-access-token: ${TMDB_READ_ACCESS_TOKEN:}
 *
 * Usage: inject TmdbProperties wherever TMDb API calls are made (e.g. TmdbClient).
 */
@ConfigurationProperties(prefix = "tmdb")
data class TmdbProperties(
    /** Base URL for the TMDb REST API, e.g. https://api.themoviedb.org/3 */
    val baseUrl: String = "https://api.themoviedb.org/3",

    /** Base URL for TMDb poster images, e.g. https://image.tmdb.org/t/p/w500 */
    val imageBaseUrl: String = "https://image.tmdb.org/t/p/w500",

    /** Bearer token (Read Access Token) from TMDb developer settings.
     *  Injected from TMDB_READ_ACCESS_TOKEN environment variable. */
    val readAccessToken: String = ""
)
