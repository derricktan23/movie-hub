package com.moviehub.ppl.service

import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.repository.PersonRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.Optional
import java.util.UUID

class PersonServiceTest {

    private lateinit var personRepository: PersonRepository
    private lateinit var personService: PersonService

    @BeforeEach
    fun setUp() {
        personRepository = mock(PersonRepository::class.java)
        personService = PersonService(personRepository)
    }

    @Test
    fun `findAll returns all people from repository`() {
        val people = listOf(
            Person(UUID.randomUUID(), "Leonardo DiCaprio", "ACTOR"),
            Person(UUID.randomUUID(), "Christopher Nolan", "DIRECTOR")
        )
        `when`(personRepository.findAll()).thenReturn(people)

        val result = personService.findAll()

        assertEquals(2, result.size)
        assertEquals("Leonardo DiCaprio", result[0].name)
    }

    @Test
    fun `findById returns person when found`() {
        val id = UUID.randomUUID()
        val person = Person(id, "Cillian Murphy", "ACTOR")
        `when`(personRepository.findById(id)).thenReturn(Optional.of(person))

        val result = personService.findById(id)

        assertNotNull(result)
        assertEquals("Cillian Murphy", result?.name)
    }

    @Test
    fun `findById returns null when not found`() {
        val id = UUID.randomUUID()
        `when`(personRepository.findById(id)).thenReturn(Optional.empty())

        val result = personService.findById(id)

        assertNull(result)
    }

    @Test
    fun `findAllByIds returns people matching IDs`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val people = listOf(
            Person(id1, "Leonardo DiCaprio", "ACTOR"),
            Person(id2, "Christopher Nolan", "DIRECTOR")
        )
        `when`(personRepository.findAllById(listOf(id1, id2))).thenReturn(people)

        val result = personService.findAllByIds(listOf(id1, id2))

        assertEquals(2, result.size)
        assertEquals(listOf("Leonardo DiCaprio", "Christopher Nolan"), result.map { it.name })
        verify(personRepository).findAllById(listOf(id1, id2))
    }

    @Test
    fun `findAllByIds returns empty list when given empty IDs`() {
        val result = personService.findAllByIds(emptyList())

        assertTrue(result.isEmpty())
        verifyNoInteractions(personRepository)
    }

    @Test
    fun `create persists new person`() {
        val person = Person(UUID.randomUUID(), "Hans Zimmer", "COMPOSER")
        `when`(personRepository.save(any(Person::class.java) ?: person)).thenReturn(person)

        val created = personService.create("Hans Zimmer", "COMPOSER")

        assertEquals("Hans Zimmer", created.name)
        assertEquals("COMPOSER", created.role)
    }

    @Test
    fun `delete returns true when person exists`() {
        val id = UUID.randomUUID()
        `when`(personRepository.existsById(id)).thenReturn(true)

        val deleted = personService.delete(id)

        assertTrue(deleted)
        verify(personRepository).deleteById(id)
    }

    @Test
    fun `delete returns false when person does not exist`() {
        val id = UUID.randomUUID()
        `when`(personRepository.existsById(id)).thenReturn(false)

        val deleted = personService.delete(id)

        assertFalse(deleted)
        verify(personRepository, never()).deleteById(id)
    }
}
