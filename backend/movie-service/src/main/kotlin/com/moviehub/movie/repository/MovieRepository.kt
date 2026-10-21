package com.moviehub.movie.repository

import com.moviehub.movie.domain.Movie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MovieRepository : JpaRepository<Movie, UUID> {
    fun findByTitleContainingIgnoreCase(title: String): List<Movie>
}
