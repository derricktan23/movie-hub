import com.google.protobuf.gradle.*

plugins {
    `java-library`
    kotlin("jvm")
    id("com.google.protobuf") version "0.9.4"
}

kotlin {
    jvmToolchain(21)
}

val grpcVersion = "1.63.0"

dependencies {
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:1.4.1")
    api("com.google.protobuf:protobuf-kotlin:3.25.5")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}
