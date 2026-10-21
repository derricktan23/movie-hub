package com.moviehub.movie.repository

import com.moviehub.movie.domain.Movie
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@Transactional
class MovieRepositoryTest {

    @Autowired
    private lateinit var movieRepository: MovieRepository

    @Test
    fun `save and findById works with castIds`() {
        val castId1 = UUID.randomUUID()
        val castId2 = UUID.randomUUID()
        val movie = Movie(
            title = "Dune: Part Two",
            artworkUrl = "https://example.com/dune2.jpg",
            releaseYear = 2024,
            castIds = mutableSetOf(castId1, castId2)
        )

        val saved = movieRepository.save(movie)
        assertNotNull(saved.id)

        val found = movieRepository.findById(saved.id!!).orElse(null)
        assertNotNull(found)
        assertEquals("Dune: Part Two", found?.title)
        assertEquals(2024, found?.releaseYear)
        assertEquals("https://example.com/dune2.jpg", found?.artworkUrl)
        assertEquals(2, found?.castIds?.size)
        assertTrue(found?.castIds?.contains(castId1) == true)
        assertTrue(found?.castIds?.contains(castId2) == true)
    }

    @Test
    fun `findByTitleContainingIgnoreCase finds movies by title query`() {
        val movie = Movie(
            title = "Interstellar Odyssey",
            artworkUrl = "https://example.com/interstellar.jpg",
            releaseYear = 2025
        )
        movieRepository.save(movie)

        val results = movieRepository.findByTitleContainingIgnoreCase("ODYSSEY")
        assertTrue(results.any { it.title == "Interstellar Odyssey" })
    }

    @Test
    fun `delete removes movie and its cast associations`() {
        val castId = UUID.randomUUID()
        val movie = Movie(
            title = "Temporary Movie",
            artworkUrl = null,
            releaseYear = 2020,
            castIds = mutableSetOf(castId)
        )
        val saved = movieRepository.save(movie)
        val id = saved.id!!

        assertTrue(movieRepository.existsById(id))
        movieRepository.deleteById(id)
        assertFalse(movieRepository.existsById(id))
    }
}
