package com.moviehub.movie.grpc

import com.moviehub.grpc.person.CreatePersonRequest
import com.moviehub.grpc.person.DeletePersonRequest
import com.moviehub.grpc.person.GetAllPeopleRequest
import com.moviehub.grpc.person.GetPeopleByIdsRequest
import com.moviehub.grpc.person.GetPersonByIdRequest
import com.moviehub.grpc.person.PersonResponse
import com.moviehub.grpc.person.PersonServiceGrpcKt
import com.moviehub.movie.dto.PersonDto
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Client wrapper for inter-service gRPC communication with ppl-service.
 */
@Service
class PersonGrpcClient(
    private val stub: PersonServiceGrpcKt.PersonServiceCoroutineStub
) {
    private val logger = LoggerFactory.getLogger(PersonGrpcClient::class.java)

    /**
     * Fetch all people from ppl-service.
     */
    suspend fun getAllPeople(): List<PersonDto> {
        return try {
            val response = stub.getAllPeople(GetAllPeopleRequest.getDefaultInstance())
            response.peopleList.mapNotNull { it.toDto() }
        } catch (e: Exception) {
            logger.warn("Failed to retrieve all people from ppl-service: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Create a new person record via ppl-service.
     */
    suspend fun createPerson(name: String, role: String): PersonDto? {
        val request = CreatePersonRequest.newBuilder()
            .setName(name)
            .setRole(role)
            .build()

        return try {
            val response = stub.createPerson(request)
            response.toDto()
        } catch (e: Exception) {
            logger.warn("Failed to create person via ppl-service: ${e.message}", e)
            null
        }
    }

    /**
     * Delete a person by UUID via ppl-service.
     */
    suspend fun deletePerson(id: UUID): Boolean {
        val request = DeletePersonRequest.newBuilder()
            .setId(id.toString())
            .build()

        return try {
            val response = stub.deletePerson(request)
            response.success
        } catch (e: Exception) {
            logger.warn("Failed to delete person $id via ppl-service: ${e.message}", e)
            false
        }
    }

    /**
     * Bulk fetch people details by list of UUIDs.
     * Returns empty list if ids is empty or if service is unreachable.
     */
    suspend fun getPeopleByIds(ids: List<UUID>): List<PersonDto> {
        if (ids.isEmpty()) return emptyList()

        val request = GetPeopleByIdsRequest.newBuilder()
            .addAllIds(ids.map { it.toString() })
            .build()

        return try {
            val response = stub.getPeopleByIds(request)
            response.peopleList.mapNotNull { it.toDto() }
        } catch (e: Exception) {
            logger.warn("Failed to retrieve people details from ppl-service: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Fetch a single person detail by UUID.
     * Returns null if not found or if an error occurs.
     */
    suspend fun getPersonById(id: UUID): PersonDto? {
        val request = GetPersonByIdRequest.newBuilder()
            .setId(id.toString())
            .build()

        return try {
            val response = stub.getPersonById(request)
            response.toDto()
        } catch (e: StatusRuntimeException) {
            if (e.status.code == Status.Code.NOT_FOUND) {
                logger.debug("Person not found in ppl-service for id: {}", id)
            } else {
                logger.warn("gRPC error retrieving person $id from ppl-service: ${e.message}", e)
            }
            null
        } catch (e: Exception) {
            logger.warn("Unexpected error retrieving person $id from ppl-service: ${e.message}", e)
            null
        }
    }

    private fun PersonResponse.toDto(): PersonDto? {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return null
        return PersonDto(
            id = uuid,
            name = name,
            role = role
        )
    }
}
