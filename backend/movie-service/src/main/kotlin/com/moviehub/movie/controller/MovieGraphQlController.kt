package com.moviehub.movie.controller

import com.moviehub.movie.domain.Movie
import com.moviehub.movie.dto.PersonDto
import com.moviehub.movie.grpc.PersonGrpcClient
import com.moviehub.movie.service.MovieService
import kotlinx.coroutines.runBlocking
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class MovieGraphQlController(
    private val movieService: MovieService,
    private val personGrpcClient: PersonGrpcClient
) {
    /**
     * Query all movies in movie_db.
     */
    @QueryMapping
    fun movies(): List<Movie> = movieService.findAll()

    /**
     * Query a single movie by UUID.
     */
    @QueryMapping
    fun movie(@Argument id: String): Movie? {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return null
        return movieService.findById(uuid)
    }

    /**
     * Resolve associated people details for a Movie via gRPC from ppl-service.
     */
    @SchemaMapping(typeName = "Movie", field = "people")
    fun people(movie: Movie): List<PersonDto> {
        val ids = movie.pplIds.toList()
        if (ids.isEmpty()) return emptyList()
        return runBlocking {
            personGrpcClient.getPeopleByIds(ids)
        }
    }

    /**
     * Create a new movie with optional associated person UUIDs.
     */
    @MutationMapping
    fun createMovie(
        @Argument title: String,
        @Argument releaseYear: Int?,
        @Argument artworkUrl: String?,
        @Argument pplIds: List<String>?
    ): Movie {
        val uuids = pplIds?.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }?.toSet()
        return movieService.create(title, releaseYear, artworkUrl, uuids)
    }

    /**
     * Delete a movie by UUID.
     */
    @MutationMapping
    fun deleteMovie(@Argument id: String): Boolean {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return false
        return movieService.delete(uuid)
    }
}
