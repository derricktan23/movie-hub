package com.moviehub.ppl.service

import com.moviehub.ppl.domain.Person
import com.moviehub.ppl.repository.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PersonService(
    private val personRepository: PersonRepository
) {
    /**
     * Retrieve all people.
     */
    fun findAll(): List<Person> = personRepository.findAll()

    /**
     * Find a person by their unique UUID. Returns null if not found.
     */
    fun findById(id: UUID): Person? = personRepository.findById(id).orElse(null)

    /**
     * Find all people matching the given list of UUIDs.
     */
    fun findAllByIds(ids: List<UUID>): List<Person> {
        if (ids.isEmpty()) return emptyList()
        return personRepository.findAllById(ids)
    }

    /**
     * Create and persist a new person.
     */
    @Transactional
    fun create(name: String, role: String): Person {
        val person = Person(name = name, role = role)
        return personRepository.save(person)
    }

    /**
     * Save an existing or new person entity.
     */
    @Transactional
    fun create(person: Person): Person = personRepository.save(person)

    /**
     * Delete a person by their unique UUID.
     */
    @Transactional
    fun delete(id: UUID): Boolean {
        return if (personRepository.existsById(id)) {
            personRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
