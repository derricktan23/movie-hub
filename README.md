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
- **Password**: `postgresPassword` *(or the value configured in `docker/.env`)*
- **Database**: `movie_db` or `ppl_db`

---

### 3. Run Frontend (Svelte + TypeScript)

```bash
cd frontend
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173) to access the Movie Hub dashboard.

---

## Service Endpoints & Ports

| Service | Technology | Port / Endpoint | Description |
|---|---|---|---|
| **Frontend** | Svelte 5 + TypeScript + Vite | `http://localhost:5173` | Enterprise UI Dashboard |
| **Movie Service (BFF)** | Spring Boot (Kotlin) | `http://localhost:8081/graphql` | BFF Gateway: Movies & People GraphQL API + gRPC Client |
| **People Service** | Spring Boot (Kotlin) | `localhost:9090` (internal) | Private Service: gRPC Server only (no HTTP) |
| **Adminer** | Adminer Web DB UI | `http://localhost:8080` | PostgreSQL Database Manager |
| **PostgreSQL** | PostgreSQL 16 | `localhost:5432` | `movie_db` & `ppl_db` |