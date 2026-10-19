# Movie App

A microservices-based Movie Hub.

## Architecture

- **Frontend**: Svelte + TypeScript (communicating via GraphQL)
- **Backend**: Spring Boot in Kotlin (`movie-service` and `ppl-service`)
- **Inter-service Communication**: gRPC
- **Databases**: PostgreSQL 16 (Database-per-service: `movie_db` and `ppl_db`)
- **Database Management**: Adminer

---

## Getting Started

### 1. Start Infrastructure

```
 rom within the `docker/` directory:
```bash
cd docker
docker compose up -d
```

To stop the containers:
```bash
docker compose -f docker/docker-compose.yml down
docker compose down
```

### 2. Access Adminer

Open [http://localhost:8080](http://localhost:8080) in your browser and log in:

- **System**: `PostgreSQL`
- **Server**: `postgres`
- **Username**: `postgres`
- **Password**: `password` *(or the value configured in `docker/.env`)*
- **Database**: `movie_db` or `ppl_db`