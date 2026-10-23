package com.moviehub.movie.grpc

import com.moviehub.grpc.person.GetPeopleByIdsRequest
import com.moviehub.grpc.person.GetPeopleByIdsResponse
import com.moviehub.grpc.person.GetPersonByIdRequest
import com.moviehub.grpc.person.PersonResponse
import com.moviehub.grpc.person.PersonServiceGrpcKt
import io.grpc.Status
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import java.util.UUID

class PersonGrpcClientTest {

    private lateinit var stub: PersonServiceGrpcKt.PersonServiceCoroutineStub
    private lateinit var client: PersonGrpcClient

    @BeforeEach
    fun setUp() {
        stub = mock()
        client = PersonGrpcClient(stub)
    }

    @Test
    fun `getPeopleByIds returns mapped PersonDtos on success`() = runTest {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()

        val response = GetPeopleByIdsResponse.newBuilder()
            .addPeople(
                PersonResponse.newBuilder()
                    .setId(id1.toString())
                    .setName("Leonardo DiCaprio")
                    .setRole("ACTOR")
                    .build()
            )
            .addPeople(
                PersonResponse.newBuilder()
                    .setId(id2.toString())
                    .setName("Christopher Nolan")
                    .setRole("DIRECTOR")
                    .build()
            )
            .build()

        whenever(stub.getPeopleByIds(any(), any())).thenReturn(response)

        val result = client.getPeopleByIds(listOf(id1, id2))

        assertEquals(2, result.size)
        assertEquals(id1, result[0].id)
        assertEquals("Leonardo DiCaprio", result[0].name)
        assertEquals("ACTOR", result[0].role)
        assertEquals(id2, result[1].id)
        assertEquals("Christopher Nolan", result[1].name)
        assertEquals("DIRECTOR", result[1].role)

        val captor = argumentCaptor<GetPeopleByIdsRequest>()
        verify(stub).getPeopleByIds(captor.capture(), any())
        assertEquals(listOf(id1.toString(), id2.toString()), captor.firstValue.idsList)
    }

    @Test
    fun `getPeopleByIds returns empty list immediately when given empty list`() = runTest {
        val result = client.getPeopleByIds(emptyList())

        assertTrue(result.isEmpty())
        verifyNoInteractions(stub)
    }

    @Test
    fun `getPeopleByIds filters out items with invalid UUID format`() = runTest {
        val validId = UUID.randomUUID()
        val response = GetPeopleByIdsResponse.newBuilder()
            .addPeople(
                PersonResponse.newBuilder()
                    .setId(validId.toString())
                    .setName("Cillian Murphy")
                    .setRole("ACTOR")
                    .build()
            )
            .addPeople(
                PersonResponse.newBuilder()
                    .setId("not-a-valid-uuid")
                    .setName("Unknown Person")
                    .setRole("EXTRA")
                    .build()
            )
            .build()

        whenever(stub.getPeopleByIds(any(), any())).thenReturn(response)

        val result = client.getPeopleByIds(listOf(validId))

        assertEquals(1, result.size)
        assertEquals(validId, result[0].id)
        assertEquals("Cillian Murphy", result[0].name)
    }

    @Test
    fun `getPeopleByIds returns empty list and handles gRPC network error gracefully`() = runTest {
        val id = UUID.randomUUID()
        whenever(stub.getPeopleByIds(any(), any()))
            .thenThrow(StatusRuntimeException(Status.UNAVAILABLE.withDescription("Service unavailable")))

        val result = client.getPeopleByIds(listOf(id))

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getPersonById returns PersonDto when person is found`() = runTest {
        val id = UUID.randomUUID()
        val response = PersonResponse.newBuilder()
            .setId(id.toString())
            .setName("Hans Zimmer")
            .setRole("COMPOSER")
            .build()

        whenever(stub.getPersonById(any(), any())).thenReturn(response)

        val result = client.getPersonById(id)

        assertNotNull(result)
        assertEquals(id, result?.id)
        assertEquals("Hans Zimmer", result?.name)
        assertEquals("COMPOSER", result?.role)

        val captor = argumentCaptor<GetPersonByIdRequest>()
        verify(stub).getPersonById(captor.capture(), any())
        assertEquals(id.toString(), captor.firstValue.id)
    }

    @Test
    fun `getPersonById returns null when person is NOT_FOUND`() = runTest {
        val id = UUID.randomUUID()
        whenever(stub.getPersonById(any(), any()))
            .thenThrow(StatusRuntimeException(Status.NOT_FOUND.withDescription("Person not found")))

        val result = client.getPersonById(id)

        assertNull(result)
    }

    @Test
    fun `getPersonById returns null and recovers on unexpected gRPC error`() = runTest {
        val id = UUID.randomUUID()
        whenever(stub.getPersonById(any(), any()))
            .thenThrow(StatusRuntimeException(Status.INTERNAL.withDescription("Internal error")))

        val result = client.getPersonById(id)

        assertNull(result)
    }

    @Test
    fun `getAllPeople returns all people dtos on success`() = runTest {
        val id = UUID.randomUUID()
        val response = GetPeopleByIdsResponse.newBuilder()
            .addPeople(
                PersonResponse.newBuilder()
                    .setId(id.toString())
                    .setName("Tom Hardy")
                    .setRole("ACTOR")
                    .build()
            )
            .build()

        whenever(stub.getAllPeople(any(), any())).thenReturn(response)

        val result = client.getAllPeople()

        assertEquals(1, result.size)
        assertEquals(id, result[0].id)
        assertEquals("Tom Hardy", result[0].name)
    }

    @Test
    fun `createPerson returns created PersonDto`() = runTest {
        val id = UUID.randomUUID()
        val response = PersonResponse.newBuilder()
            .setId(id.toString())
            .setName("Cillian Murphy")
            .setRole("ACTOR")
            .build()

        whenever(stub.createPerson(any(), any())).thenReturn(response)

        val result = client.createPerson("Cillian Murphy", "ACTOR")

        assertNotNull(result)
        assertEquals(id, result?.id)
        assertEquals("Cillian Murphy", result?.name)
    }

    @Test
    fun `deletePerson returns success status`() = runTest {
        val id = UUID.randomUUID()
        val response = com.moviehub.grpc.person.DeletePersonResponse.newBuilder()
            .setSuccess(true)
            .build()

        whenever(stub.deletePerson(any(), any())).thenReturn(response)

        val result = client.deletePerson(id)

        assertEquals(true, result)
    }
}
