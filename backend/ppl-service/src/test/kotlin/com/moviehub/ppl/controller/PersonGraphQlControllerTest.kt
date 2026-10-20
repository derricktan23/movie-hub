package com.moviehub.ppl.controller

import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.service.PersonService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.UUID

class PersonGraphQlControllerTest {

    private lateinit var personService: PersonService
    private lateinit var controller: PersonGraphQlController

    @BeforeEach
    fun setUp() {
        personService = mock(PersonService::class.java)
        controller = PersonGraphQlController(personService)
    }

    @Test
    fun `people query returns all people`() {
        val people = listOf(
            Person(UUID.randomUUID(), "Leonardo DiCaprio", "ACTOR"),
            Person(UUID.randomUUID(), "Christopher Nolan", "DIRECTOR")
        )
        `when`(personService.findAll()).thenReturn(people)

        val result = controller.people()

        assertEquals(2, result.size)
        assertEquals("Leonardo DiCaprio", result[0].name)
    }

    @Test
    fun `person query returns person by valid id`() {
        val id = UUID.randomUUID()
        val person = Person(id, "Cillian Murphy", "ACTOR")
        `when`(personService.findById(id)).thenReturn(person)

        val result = controller.person(id.toString())

        assertNotNull(result)
        assertEquals("Cillian Murphy", result?.name)
    }

    @Test
    fun `person query returns null for invalid uuid format`() {
        val result = controller.person("not-a-uuid")
        assertNull(result)
        verifyNoInteractions(personService)
    }

    @Test
    fun `createPerson mutation creates person`() {
        val person = Person(UUID.randomUUID(), "Tom Hardy", "ACTOR")
        `when`(personService.create("Tom Hardy", "ACTOR")).thenReturn(person)

        val result = controller.createPerson("Tom Hardy", "ACTOR")

        assertEquals("Tom Hardy", result.name)
        assertEquals("ACTOR", result.role)
    }

    @Test
    fun `deletePerson mutation deletes person`() {
        val id = UUID.randomUUID()
        `when`(personService.delete(id)).thenReturn(true)

        val result = controller.deletePerson(id.toString())

        assertTrue(result)
    }
}
