package com.moviehub.movie.grpc

import com.moviehub.grpc.person.PersonServiceGrpcKt
import io.grpc.Channel
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcClientConfig {

    @GrpcClient("ppl-service")
    private lateinit var channel: Channel

    @Bean
    fun personServiceCoroutineStub(): PersonServiceGrpcKt.PersonServiceCoroutineStub {
        return PersonServiceGrpcKt.PersonServiceCoroutineStub(channel)
    }
}
