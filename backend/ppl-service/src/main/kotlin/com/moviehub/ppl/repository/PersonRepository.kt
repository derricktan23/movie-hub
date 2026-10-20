package com.moviehub.ppl.repository

import com.moviehub.ppl.domain.Person
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PersonRepository : JpaRepository<Person, UUID> {
    fun findAllByIdIn(ids: Collection<UUID>): List<Person>
}
