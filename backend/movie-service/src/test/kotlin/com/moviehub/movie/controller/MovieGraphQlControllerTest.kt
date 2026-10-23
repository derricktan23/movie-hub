package com.moviehub.movie.controller

import com.moviehub.movie.domain.Movie
import com.moviehub.movie.dto.PersonDto
import com.moviehub.movie.grpc.PersonGrpcClient
import com.moviehub.movie.service.MovieService
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.UUID

class MovieGraphQlControllerTest {

    private lateinit var movieService: MovieService
    private lateinit var personGrpcClient: PersonGrpcClient
    private lateinit var controller: MovieGraphQlController

    @BeforeEach
    fun setUp() {
        movieService = mock(MovieService::class.java)
        personGrpcClient = mock(PersonGrpcClient::class.java)
        controller = MovieGraphQlController(movieService, personGrpcClient)
    }

    @Test
    fun `movies query returns all movies`() {
        val movies = listOf(
            Movie(id = UUID.randomUUID(), title = "Inception", releaseYear = 2010),
            Movie(id = UUID.randomUUID(), title = "Interstellar", releaseYear = 2014)
        )
        `when`(movieService.findAll()).thenReturn(movies)

        val result = controller.movies()

        assertEquals(2, result.size)
        assertEquals("Inception", result[0].title)
    }

    @Test
    fun `movie query returns movie by id`() {
        val id = UUID.randomUUID()
        val movie = Movie(id = id, title = "Oppenheimer", releaseYear = 2023)
        `when`(movieService.findById(id)).thenReturn(movie)

        val result = controller.movie(id.toString())

        assertNotNull(result)
        assertEquals("Oppenheimer", result?.title)
    }

    @Test
    fun `movie query returns null for invalid uuid`() {
        val result = controller.movie("invalid-uuid")
        assertNull(result)
    }

    @Test
    fun `people field resolver calls personGrpcClient`() = runTest {
        val personId = UUID.randomUUID()
        val movie = Movie(
            id = UUID.randomUUID(),
            title = "Inception",
            pplIds = mutableSetOf(personId)
        )
        val people = listOf(PersonDto(id = personId, name = "Leonardo DiCaprio", role = "ACTOR"))
        `when`(personGrpcClient.getPeopleByIds(listOf(personId))).thenReturn(people)

        val result = controller.people(movie)

        assertEquals(1, result.size)
        assertEquals("Leonardo DiCaprio", result[0].name)
    }

    @Test
    fun `createMovie mutation saves and returns movie`() {
        val personId = UUID.randomUUID()
        val created = Movie(id = UUID.randomUUID(), title = "Dunkirk", releaseYear = 2017, pplIds = mutableSetOf(personId))
        `when`(movieService.create("Dunkirk", 2017, null, setOf(personId))).thenReturn(created)

        val result = controller.createMovie("Dunkirk", 2017, null, listOf(personId.toString()))

        assertEquals("Dunkirk", result.title)
        assertEquals(2017, result.releaseYear)
    }

    @Test
    fun `deleteMovie mutation returns true when deleted`() {
        val id = UUID.randomUUID()
        `when`(movieService.delete(id)).thenReturn(true)

        val result = controller.deleteMovie(id.toString())

        assertTrue(result)
    }

    @Test
    fun `people query delegates to personGrpcClient`() = runTest {
        val person = PersonDto(UUID.randomUUID(), "Cillian Murphy", "ACTOR")
        `when`(personGrpcClient.getAllPeople()).thenReturn(listOf(person))

        val result = controller.people()

        assertEquals(1, result.size)
        assertEquals("Cillian Murphy", result[0].name)
    }

    @Test
    fun `person query delegates to personGrpcClient`() = runTest {
        val id = UUID.randomUUID()
        val person = PersonDto(id, "Tom Hardy", "ACTOR")
        `when`(personGrpcClient.getPersonById(id)).thenReturn(person)

        val result = controller.person(id.toString())

        assertNotNull(result)
        assertEquals("Tom Hardy", result?.name)
    }

    @Test
    fun `createPerson mutation delegates to personGrpcClient`() = runTest {
        val id = UUID.randomUUID()
        val created = PersonDto(id, "Emma Stone", "ACTOR")
        `when`(personGrpcClient.createPerson("Emma Stone", "ACTOR")).thenReturn(created)

        val result = controller.createPerson("Emma Stone", "ACTOR")

        assertEquals("Emma Stone", result.name)
    }

    @Test
    fun `deletePerson mutation delegates to personGrpcClient`() = runTest {
        val id = UUID.randomUUID()
        `when`(personGrpcClient.deletePerson(id)).thenReturn(true)

        val result = controller.deletePerson(id.toString())

        assertTrue(result)
    }
}
