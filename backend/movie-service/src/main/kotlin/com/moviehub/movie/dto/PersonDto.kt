package com.moviehub.movie.dto

import java.util.UUID

/**
 * Data transfer object representing a Person (actor, director, composer) fetched from ppl-service.
 */
data class PersonDto(
    val id: UUID,
    val name: String,
    val role: String
)
