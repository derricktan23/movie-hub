pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "backend"

include("grpc-proto")
if (file("ppl-service").exists()) {
    include("ppl-service")
}
if (file("movie-service").exists()) {
    include("movie-service")
}