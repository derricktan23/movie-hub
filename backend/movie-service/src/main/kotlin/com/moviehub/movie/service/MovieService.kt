package com.moviehub.movie.service

import com.moviehub.movie.domain.Movie
import com.moviehub.movie.repository.MovieRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class MovieService(
    private val movieRepository: MovieRepository
) {
    @Transactional(readOnly = true)
    fun findAll(): List<Movie> = movieRepository.findAll()

    @Transactional(readOnly = true)
    fun findById(id: UUID): Movie? = movieRepository.findById(id).orElse(null)

    fun create(title: String, releaseYear: Int?, artworkUrl: String?, pplIds: Set<UUID>?): Movie {
        val movie = Movie(
            title = title,
            releaseYear = releaseYear,
            artworkUrl = artworkUrl,
            pplIds = pplIds?.toMutableSet() ?: mutableSetOf()
        )
        return movieRepository.save(movie)
    }

    fun delete(id: UUID): Boolean {
        return if (movieRepository.existsById(id)) {
            movieRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
