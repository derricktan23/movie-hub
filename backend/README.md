# backend

This project uses [Gradle](https://gradle.org/).
To build and run the application, use the *Gradle* tool window by clicking the Gradle icon in the right-hand toolbar,
or run it directly from the terminal:

* Run `./gradlew run` to build and run the application.
* Run `./gradlew build` to only build the application.
* Run `./gradlew check` to run all checks, including tests.
* Run `./gradlew clean` to clean all build outputs.

Note the usage of the Gradle Wrapper (`./gradlew`).
This is the suggested way to use Gradle in production projects.

[Learn more about the Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html).

[Learn more about Gradle tasks](https://docs.gradle.org/current/userguide/command_line_interface.html#common_tasks).

This project follows the suggested multi-module setup and consists of the `app` and `utils` subprojects.
The shared build logic was extracted to a convention plugin located in `buildSrc`.

This project uses a version catalog (see `gradle/libs.versions.toml`) to declare and version dependencies
and both a build cache and a configuration cache (see `gradle.properties`).

<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/4c598961-b031-412f-b6ac-93a31019bdd9" />

### Backend Services Architecture (Summary)

* **`movie-service` (BFF Gateway — HTTP `:8081`)**:
  * **Public Entry Point**: Exposes GraphQL to the frontend.
  * **Orchestrator**: Queries external **TMDb API** for live movie data/posters, and calls `ppl-service` via internal gRPC to resolve cast details.
  * **Database**: Manages `movie_db` (movies & cast links).

* **`ppl-service` (Internal Microservice — gRPC `:9090`)**:
  * **Private Domain Service**: Has no public HTTP port; communicates exclusively with `movie-service` over fast binary **gRPC (ProtoBuf)**.
  * **Database**: Manages `ppl_db` (actors, directors, creators).

* **Data Layer (PostgreSQL 16 — `:5432`)**:
  * Follows the **Database-per-Service** pattern (`movie_db` & `ppl_db`).
  * Completely isolated with **zero cross-database foreign keys** for true microservice decoupling.
