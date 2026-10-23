package com.moviehub.ppl

import com.moviehub.ppl.grpc.PersonGrpcService
import com.moviehub.ppl.repository.PersonRepository
import com.moviehub.ppl.service.PersonService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["grpc.server.port=0"])
class PplServiceApplicationTests {

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var personService: PersonService

    @Autowired
    private lateinit var personGrpcService: PersonGrpcService

    @Test
    fun contextLoads() {
        assertNotNull(personRepository)
        assertNotNull(personService)
        assertNotNull(personGrpcService)
    }

    @Test
    fun `seeds are present in database`() {
        val people = personService.findAll()
        // If postgres is running with seed data, verify there are seeded people
        assertTrue(people.isNotEmpty(), "Database should have seeded people")
    }
}
