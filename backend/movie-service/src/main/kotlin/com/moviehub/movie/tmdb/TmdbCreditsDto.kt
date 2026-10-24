package com.moviehub.movie.tmdb

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Raw deserialized response from TMDb GET /movie/{movie_id}/credits endpoint.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbCreditsResponse(
    val id: Long = 0,
    val cast: List<TmdbCastDto> = emptyList(),
    val crew: List<TmdbCrewDto> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbCastDto(
    val id: Long = 0,
    val name: String = "",
    val character: String? = null,
    val order: Int = 0,
    @JsonProperty("profile_path")
    val profilePath: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TmdbCrewDto(
    val id: Long = 0,
    val name: String = "",
    val job: String = "",
    val department: String? = null,
    @JsonProperty("profile_path")
    val profilePath: String? = null
)

/**
 * Clean GraphQL-facing DTO for TMDb credits.
 */
data class TmdbCreditsResult(
    val cast: List<TmdbCreditPerson> = emptyList(),
    val crew: List<TmdbCreditPerson> = emptyList()
)

data class TmdbCreditPerson(
    val id: String,
    val name: String,
    val role: String,
    val characterOrJob: String?,
    val profileUrl: String?
)
