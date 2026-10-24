package com.moviehub.movie.tmdb

import com.moviehub.movie.config.TmdbProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

/**
 * HTTP client that proxies search requests to the TMDb REST API.
 *
 * All calls are authenticated via Bearer token (TMDB_READ_ACCESS_TOKEN).
 * The token is never exposed to the frontend — all calls are server-side.
 *
 * Endpoint used: GET https://api.themoviedb.org/3/search/movie
 * Docs: https://developer.themoviedb.org/reference/search-movie
 */
@Service
class TmdbClient(private val tmdb: TmdbProperties) {

    private val log = LoggerFactory.getLogger(TmdbClient::class.java)

    private val restClient: RestClient = RestClient.builder()
        .baseUrl(tmdb.baseUrl)
        .defaultHeader("accept", "application/json")
        .defaultHeader("Authorization", "Bearer ${tmdb.readAccessToken}")
        .build()

    /**
     * Search TMDb for movies matching [query].
     *
     * @param query Raw search text typed by the user (e.g. "Inception")
     * @return Up to 20 matching results with pre-built poster URLs, or empty list on any error.
     */
    fun searchMovies(query: String): List<TmdbSearchResult> {
        if (query.isBlank()) return emptyList()

        return try {
            val response = restClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/search/movie")
                        .queryParam("query", query)
                        .queryParam("include_adult", "false")
                        .queryParam("language", "en-US")
                        .queryParam("page", "1")
                        .build()
                }
                .retrieve()
                .body(TmdbSearchResponse::class.java)

            response?.results?.map { it.toSearchResult() } ?: emptyList()

        } catch (e: RestClientException) {
            log.error("TMDb search failed for query='{}': {}", query, e.message)
            emptyList()
        } catch (e: Exception) {
            log.error("Unexpected error during TMDb search for query='{}': {}", query, e.message)
            emptyList()
        }
    }

    /**
     * Fetch top cast and creators for a specific TMDb movie ID.
     *
     * Endpoint used: GET https://api.themoviedb.org/3/movie/{movie_id}/credits
     */
    fun getMovieCredits(tmdbId: String): TmdbCreditsResult {
        if (tmdbId.isBlank()) return TmdbCreditsResult()

        return try {
            val response = restClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/movie/{id}/credits")
                        .queryParam("language", "en-US")
                        .build(tmdbId)
                }
                .retrieve()
                .body(TmdbCreditsResponse::class.java)

            if (response == null) return TmdbCreditsResult()

            // Top 8 cast members sorted by billing order
            val cast = response.cast
                .sortedBy { it.order }
                .take(8)
                .map {
                    TmdbCreditPerson(
                        id = it.id.toString(),
                        name = it.name,
                        role = "ACTOR",
                        characterOrJob = it.character,
                        profileUrl = it.profilePath?.let { path -> "https://image.tmdb.org/t/p/w185$path" }
                    )
                }

            // Key crew members (Director, Writer, Producer, Composer)
            val keyJobs = setOf("Director", "Writer", "Screenplay", "Producer", "Original Music Composer", "Music")
            val crew = response.crew
                .filter { it.job in keyJobs }
                .distinctBy { it.id to it.job }
                .take(8)
                .map {
                    val role = when {
                        it.job.equals("Director", ignoreCase = true) -> "DIRECTOR"
                        it.job.contains("Writer", ignoreCase = true) || it.job.contains("Screenplay", ignoreCase = true) -> "WRITER"
                        it.job.contains("Producer", ignoreCase = true) -> "PRODUCER"
                        it.job.contains("Music", ignoreCase = true) || it.job.contains("Composer", ignoreCase = true) -> "COMPOSER"
                        else -> "CREW"
                    }
                    TmdbCreditPerson(
                        id = it.id.toString(),
                        name = it.name,
                        role = role,
                        characterOrJob = it.job,
                        profileUrl = it.profilePath?.let { path -> "https://image.tmdb.org/t/p/w185$path" }
                    )
                }

            TmdbCreditsResult(cast = cast, crew = crew)

        } catch (e: RestClientException) {
            log.error("TMDb credits lookup failed for tmdbId='{}': {}", tmdbId, e.message)
            TmdbCreditsResult()
        } catch (e: Exception) {
            log.error("Unexpected error during TMDb credits lookup for tmdbId='{}': {}", tmdbId, e.message)
            TmdbCreditsResult()
        }
    }

    /**
     * Maps a raw [TmdbMovieResult] to the clean [TmdbSearchResult] DTO:
     *   - Extracts release year from "YYYY-MM-DD" (or null if missing/invalid)
     *   - Builds the full poster URL by prepending imageBaseUrl
     */
    private fun TmdbMovieResult.toSearchResult(): TmdbSearchResult {
        val year = releaseDate.takeIf { it.length >= 4 }
            ?.substring(0, 4)
            ?.toIntOrNull()

        val posterUrl = posterPath?.let { "${tmdb.imageBaseUrl}$it" }

        return TmdbSearchResult(
            tmdbId = id.toString(),
            title = title,
            releaseYear = year,
            posterUrl = posterUrl,
            overview = overview
        )
    }
}
