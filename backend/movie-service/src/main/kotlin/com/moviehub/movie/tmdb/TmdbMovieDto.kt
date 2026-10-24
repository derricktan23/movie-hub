package com.moviehub.movie.tmdb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Raw deserialized response from TMDb GET /search/movie endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbSearchResponse(
    val page: Int = 0,
    val results: List<TmdbMovieResult> = emptyList(),

    @JsonProperty("total_results")
    val totalResults: Int = 0,

    @JsonProperty("total_pages")
    val totalPages: Int = 0
)

/**
 * A single movie entry in the TMDb search results array.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbMovieResult(
    val id: Long = 0,
    val title: String = "",

    @JsonProperty("original_title")
    val originalTitle: String = "",

    val overview: String = "",

    @JsonProperty("release_date")
    val releaseDate: String = "",          // e.g. "1999-10-15" or ""

    @JsonProperty("poster_path")
    val posterPath: String? = null,        // e.g. "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg"

    @JsonProperty("backdrop_path")
    val backdropPath: String? = null,

    val popularity: Double = 0.0,

    @JsonProperty("vote_average")
    val voteAverage: Double = 0.0,

    @JsonProperty("vote_count")
    val voteCount: Int = 0,

    val adult: Boolean = false
)
