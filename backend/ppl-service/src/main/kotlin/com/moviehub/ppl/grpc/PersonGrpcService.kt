package com.moviehub.ppl.grpc

import com.moviehub.grpc.person.CreatePersonRequest
import com.moviehub.grpc.person.DeletePersonRequest
import com.moviehub.grpc.person.DeletePersonResponse
import com.moviehub.grpc.person.GetAllPeopleRequest
import com.moviehub.grpc.person.GetPeopleByIdsRequest
import com.moviehub.grpc.person.GetPeopleByIdsResponse
import com.moviehub.grpc.person.GetPersonByIdRequest
import com.moviehub.grpc.person.PersonResponse
import com.moviehub.grpc.person.PersonServiceGrpcKt
import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.service.PersonService
import io.grpc.Status
import net.devh.boot.grpc.server.service.GrpcService
import java.util.UUID

@GrpcService
class PersonGrpcService(
    private val personService: PersonService
) : PersonServiceGrpcKt.PersonServiceCoroutineImplBase() {

    /**
     * Retrieve all people records.
     */
    override suspend fun getAllPeople(request: GetAllPeopleRequest): GetPeopleByIdsResponse {
        val people = personService.findAll()
        val personResponses = people.map { it.toProto() }
        return GetPeopleByIdsResponse.newBuilder()
            .addAllPeople(personResponses)
            .build()
    }

    /**
     * Create and persist a new person record.
     */
    override suspend fun createPerson(request: CreatePersonRequest): PersonResponse {
        if (request.name.isBlank()) {
            throw Status.INVALID_ARGUMENT
                .withDescription("Person name cannot be blank")
                .asRuntimeException()
        }
        val person = personService.create(name = request.name, role = request.role)
        return person.toProto()
    }

    /**
     * Delete a person by UUID.
     */
    override suspend fun deletePerson(request: DeletePersonRequest): DeletePersonResponse {
        val uuid = runCatching { UUID.fromString(request.id) }.getOrNull()
            ?: throw Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID string format: '${request.id}'")
                .asRuntimeException()

        val success = personService.delete(uuid)
        return DeletePersonResponse.newBuilder()
            .setSuccess(success)
            .build()
    }

    /**
     * Handles bulk ID lookups from movie-service.
     * Takes a list of UUID strings and returns person details for each found match.
     */
    override suspend fun getPeopleByIds(request: GetPeopleByIdsRequest): GetPeopleByIdsResponse {
        val uuids = request.idsList.mapNotNull { idStr ->
            runCatching { UUID.fromString(idStr) }.getOrNull()
        }

        val people = if (uuids.isEmpty()) {
            emptyList()
        } else {
            personService.findAllByIds(uuids)
        }

        val personResponses = people.map { it.toProto() }

        return GetPeopleByIdsResponse.newBuilder()
            .addAllPeople(personResponses)
            .build()
    }

    /**
     * Handles single ID lookup by UUID string.
     */
    override suspend fun getPersonById(request: GetPersonByIdRequest): PersonResponse {
        val uuid = runCatching { UUID.fromString(request.id) }.getOrNull()
            ?: throw Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID string format: '${request.id}'")
                .asRuntimeException()

        val person = personService.findById(uuid)
            ?: throw Status.NOT_FOUND
                .withDescription("Person not found with id: $uuid")
                .asRuntimeException()

        return person.toProto()
    }

    private fun Person.toProto(): PersonResponse {
        return PersonResponse.newBuilder()
            .setId(id?.toString() ?: "")
            .setName(name)
            .setRole(role)
            .build()
    }
}
