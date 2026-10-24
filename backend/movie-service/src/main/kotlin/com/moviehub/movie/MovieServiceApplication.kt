package com.moviehub.movie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MovieServiceApplication

fun main(args: Array<String>) {
    runApplication<MovieServiceApplication>(*args)
}
