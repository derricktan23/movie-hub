package com.moviehub.ppl.controller

import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.service.PersonService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import java.util.UUID

@Controller
class PersonGraphQlController(
    private val personService: PersonService
) {
    /**
     * Query all people in the database.
     */
    @QueryMapping
    fun people(): List<Person> = personService.findAll()

    /**
     * Query a single person by their UUID.
     */
    @QueryMapping
    fun person(@Argument id: String): Person? {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return null
        return personService.findById(uuid)
    }

    /**
     * Create a new person record.
     */
    @MutationMapping
    fun createPerson(@Argument name: String, @Argument role: String): Person {
        return personService.create(name, role)
    }

    /**
     * Delete an existing person record by UUID.
     */
    @MutationMapping
    fun deletePerson(@Argument id: String): Boolean {
        val uuid = runCatching { UUID.fromString(id) }.getOrNull() ?: return false
        return personService.delete(uuid)
    }
}
