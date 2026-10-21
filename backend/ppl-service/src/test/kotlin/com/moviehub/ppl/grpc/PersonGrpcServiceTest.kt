package com.moviehub.ppl.grpc

import com.moviehub.grpc.person.GetPeopleByIdsRequest
import com.moviehub.grpc.person.GetPersonByIdRequest
import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.service.PersonService
import io.grpc.Status
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.UUID

class PersonGrpcServiceTest {

    private lateinit var personService: PersonService
    private lateinit var personGrpcService: PersonGrpcService

    @BeforeEach
    fun setUp() {
        personService = mock(PersonService::class.java)
        personGrpcService = PersonGrpcService(personService)
    }

    @Test
    fun `getPeopleByIds returns matching people in response`() = runTest {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val people = listOf(
            Person(id1, "Leonardo DiCaprio", "ACTOR"),
            Person(id2, "Christopher Nolan", "DIRECTOR")
        )
        `when`(personService.findAllByIds(listOf(id1, id2))).thenReturn(people)

        val request = GetPeopleByIdsRequest.newBuilder()
            .addAllIds(listOf(id1.toString(), id2.toString()))
            .build()

        val response = personGrpcService.getPeopleByIds(request)

        assertEquals(2, response.peopleCount)
        assertEquals("Leonardo DiCaprio", response.getPeople(0).name)
        assertEquals("Christopher Nolan", response.getPeople(1).name)
    }

    @Test
    fun `getPeopleByIds ignores invalid UUIDs gracefully`() = runTest {
        val id = UUID.randomUUID()
        val person = Person(id, "Cillian Murphy", "ACTOR")
        `when`(personService.findAllByIds(listOf(id))).thenReturn(listOf(person))

        val request = GetPeopleByIdsRequest.newBuilder()
            .addAllIds(listOf(id.toString(), "not-a-valid-uuid"))
            .build()

        val response = personGrpcService.getPeopleByIds(request)

        assertEquals(1, response.peopleCount)
        assertEquals("Cillian Murphy", response.getPeople(0).name)
        verify(personService).findAllByIds(listOf(id))
    }

    @Test
    fun `getPeopleByIds returns empty response for empty request`() = runTest {
        val request = GetPeopleByIdsRequest.newBuilder().build()

        val response = personGrpcService.getPeopleByIds(request)

        assertEquals(0, response.peopleCount)
        verifyNoInteractions(personService)
    }

    @Test
    fun `getPersonById returns person for valid existing id`() = runTest {
        val id = UUID.randomUUID()
        val person = Person(id, "Hans Zimmer", "COMPOSER")
        `when`(personService.findById(id)).thenReturn(person)

        val request = GetPersonByIdRequest.newBuilder()
            .setId(id.toString())
            .build()

        val response = personGrpcService.getPersonById(request)

        assertEquals(id.toString(), response.id)
        assertEquals("Hans Zimmer", response.name)
        assertEquals("COMPOSER", response.role)
    }

    @Test
    fun `getPersonById throws NOT_FOUND when person does not exist`() = runTest {
        val id = UUID.randomUUID()
        `when`(personService.findById(id)).thenReturn(null)

        val request = GetPersonByIdRequest.newBuilder()
            .setId(id.toString())
            .build()

        val exception = assertThrows<StatusRuntimeException> {
            personGrpcService.getPersonById(request)
        }

        assertEquals(Status.Code.NOT_FOUND, exception.status.code)
    }

    @Test
    fun `getPersonById throws INVALID_ARGUMENT for malformed UUID`() = runTest {
        val request = GetPersonByIdRequest.newBuilder()
            .setId("invalid-uuid")
            .build()

        val exception = assertThrows<StatusRuntimeException> {
            personGrpcService.getPersonById(request)
        }

        assertEquals(Status.Code.INVALID_ARGUMENT, exception.status.code)
    }
}
