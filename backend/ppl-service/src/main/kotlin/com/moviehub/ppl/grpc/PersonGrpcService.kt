package com.moviehub.ppl.grpc

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
