package com.moviehub.movie

import com.moviehub.movie.repository.MovieRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MovieServiceApplicationTests {

    @Autowired
    private lateinit var movieRepository: MovieRepository

    @Test
    fun contextLoads() {
        assertNotNull(movieRepository)
    }

    @Test
    fun `seeds are present in movie database`() {
        val movies = movieRepository.findAll()
        assertTrue(movies.isNotEmpty(), "Database should have seeded movies")
    }
}
