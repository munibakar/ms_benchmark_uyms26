# A Comparative Analysis of gRPC, GraphQL, and REST for Synchronous Microservice Communication: Benchmarking a Video Streaming Architecture

This project implements the same **video streaming platform** (Netflix-clone) microservices architecture using three different communication protocols, enabling a direct performance comparison.

| Project | Protocol | Inter-Service Communication | Gateway |
|---|---|---|---|
| `ms_benchmark_rest` | REST (HTTP/JSON) | Spring `RestTemplate` / `WebClient` | Spring Cloud Gateway (`:8765`) |
| `ms_benchmark_gRPC` | gRPC (HTTP/2 + Protobuf) | gRPC Stubs (`.proto`) | Spring Cloud Gateway (`:8765`) |
| `ms_benchmark_GraphQL` | GraphQL (HTTP/JSON) | GraphQL Subgraph Queries | Spring Cloud Gateway (`:8765`) + Apollo Federation Gateway (`:4000`) |

---

## Architecture Overview

All three projects share the **same 6 microservices** and infrastructure components:

```
                          ┌─────────────────────────┐
                          │      API Gateway         │
                          │   (Spring Cloud :8765)   │
                          └──────────┬──────────────┘
          ┌──────────┬───────────┬───┴────┬────────────┬──────────────┐
          ▼          ▼           ▼        ▼            ▼              ▼
   ┌────────────┐┌────────┐┌─────────┐┌────────┐┌──────────────┐┌────────────┐
   │ Auth       ││ User   ││ Profile ││ Sub &  ││ Content Mgmt ││ Video      │
   │ Service    ││ Service││ Service ││ Billing││ Service      ││ Streaming  │
   │ :8000      ││ :9000  ││ :9001   ││ :9100  ││ :9200        ││ :9300      │
   └─────┬──────┘└───┬────┘└────┬────┘└───┬────┘└──────┬───────┘└────────────┘
         ▼           ▼          ▼         ▼            ▼
   ┌──────────┐┌──────────┐┌──────────┐┌──────────┐┌──────────┐
   │ Postgres ││ Postgres ││ Postgres ││ Postgres ││ Postgres │
   │ :5433    ││ :5434    ││ :5436    ││ :5435    ││ :5437    │
   └──────────┘└──────────┘└──────────┘└──────────┘└──────────┘
```

> The **GraphQL** version additionally includes an **Apollo Federation Gateway** (Node.js, `:4000`). This gateway merges all service GraphQL schemas into a single endpoint, enabling cross-service queries.

### Shared Infrastructure

| Component | Technology | Port |
|---|---|---|
| Config Server | Spring Cloud Config Server | `8888` |
| API Gateway | Spring Cloud Gateway | `8765` |
| Databases | PostgreSQL 16 Alpine | `5433–5437` |
| GraphQL Gateway *(GraphQL only)* | Apollo Federation (Node.js 18) | `4000` |

### Microservices

| Service | Description | HTTP Port | gRPC Port *(gRPC only)* |
|---|---|---|---|
| **Authentication Service** | User registration, login, JWT token management | `8000` | — |
| **User Service** | User CRUD operations, dashboard | `9000` | `9090` |
| **Profile Service** | User profile management | `9001` | `9091` |
| **Subscription & Billing** | Subscription plans, payment simulation | `9100` | `9190` |
| **Content Management** | Movie/series content management | `9200` | `9290` |
| **Video Streaming** | Video streaming (byte-range) | `9300` | — |

---

## Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot, Spring Cloud |
| **Build Tool** | Maven 3.9 |
| **Database** | PostgreSQL 16 |
| **Containerization** | Docker (Multi-stage build) |
| **Orchestration** | Docker Swarm (overlay network) |
| **gRPC** *(gRPC project only)* | gRPC-Java, Protocol Buffers 3 |
| **GraphQL** *(GraphQL project only)* | Spring GraphQL, Apollo Federation 2, Node.js 18 |

---

## Getting Started — Step-by-Step Guide

### Prerequisites

Make sure the following software is installed on your system before proceeding:

| Software | Minimum Version | Check Command |
|---|---|---|
| **Docker** | 20.10+ | `docker --version` |
| **Docker Compose** | v2.0+ | `docker compose version` |
| **Git** | 2.x | `git --version` |

> [!IMPORTANT]
> These projects are configured to run with **Docker Swarm mode**. Use `docker stack deploy` instead of `docker compose up`.

---

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd ms_benchmark_uyms26
```

After cloning, you will see three directories:

```
ms_benchmark_uyms26/
├── ms_benchmark_rest/       # REST version
├── ms_benchmark_gRPC/       # gRPC version
└── ms_benchmark_GraphQL/    # GraphQL version
```

---

### Step 2: Initialize Docker Swarm

If you haven't already, enable Docker Swarm mode:

```bash
docker swarm init
```

> If already active, you will see `This node is already part of a swarm` — you can skip this step.

---

### Step 3: Create the `videos` Folder (Required for Video Streaming Service)

The **Video Streaming Service** streams video files from the host machine via a Docker volume mount (`./videos:/videos:ro`). You must create a `videos/` directory **with the following structure** inside the project folder you want to run **before deploying**.

```
videos/
├── movies/
│   ├── movie-1.mp4
│   └── movie-2.mp4
└── series/
    ├── series-1/
    │   └── episode-1.mp4
    └── series-2/
        └── episode-1.mp4
```

Create the directory for the version you want to run:

```bash
# For REST version:
mkdir -p ms_benchmark_rest/videos/movies ms_benchmark_rest/videos/series

# For gRPC version:
mkdir -p ms_benchmark_gRPC/videos/movies ms_benchmark_gRPC/videos/series

# For GraphQL version:
mkdir -p ms_benchmark_GraphQL/videos/movies ms_benchmark_GraphQL/videos/series
```

Place your `.mp4` video files inside the appropriate subdirectories (`movies/` or `series/`). The service will serve them via the `/api/stream/` endpoint.

> [!WARNING]
> If the `videos/` folder does not exist, the `video-streaming-service` container will **fail to start** or will not be able to serve any video content. Docker will not automatically create this directory.

---

### Step 4: Build and Deploy

Navigate to the directory of the version you want to run and follow the steps below.

> [!WARNING]
> All three projects use the **same ports** (8765, 8888, 5433–5437, etc.), so you can only run **one version at a time**. Stop the current version before starting another.

#### Option A — REST Version

> Make sure `ms_benchmark_rest/videos/` exists before deploying (see Step 3).

```bash
cd ms_benchmark_rest

# 1. Build Docker images
docker compose -f docker-compose.microservices.yml build

# 2. Deploy with Docker Swarm
docker stack deploy -c docker-compose.microservices.yml ms-rest
```

#### Option B — gRPC Version

> Make sure `ms_benchmark_gRPC/videos/` exists before deploying (see Step 3).

```bash
cd ms_benchmark_gRPC

# 1. Build Docker images
docker compose -f docker-compose.microservices.yml build

# 2. Deploy with Docker Swarm
docker stack deploy -c docker-compose.microservices.yml ms-grpc
```

#### Option C — GraphQL Version

> Make sure `ms_benchmark_GraphQL/videos/` exists before deploying (see Step 3).

```bash
cd ms_benchmark_GraphQL

# 1. Build Docker images
docker compose -f docker-compose.microservices.yml build

# 2. Deploy with Docker Swarm
docker stack deploy -c docker-compose.microservices.yml ms-graphql
```

> [!NOTE]
> The first build may take **10–20 minutes** as Maven downloads all dependencies. Subsequent builds will be much faster thanks to Docker cache.

---

### Step 5: Verify Service Health

After deployment, wait for all services to come up and check their status:

```bash
# Check all services in the stack:
docker stack services ms-rest      # for REST
docker stack services ms-grpc      # for gRPC
docker stack services ms-graphql   # for GraphQL
```

Ensure all services show **1/1** in the `REPLICAS` column. It may take approximately **2–4 minutes** for all services to become healthy.

#### Detailed Health Check

View logs for a specific service:

```bash
# View logs for a specific service:
docker service logs ms-rest_authentication-service --follow
docker service logs ms-rest_api-gateway --follow

# View all container statuses:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

#### Health Endpoints

Each service exposes a health endpoint. Access them through the API Gateway:

```bash
# Config Server
curl http://localhost:8888/actuator/health

# API Gateway
curl http://localhost:8765/actuator/health

# Services (via API Gateway)
curl http://localhost:8765/api/auth/health
curl http://localhost:8765/api/users/health
curl http://localhost:8765/api/profiles/health
curl http://localhost:8765/api/subscription/health
curl http://localhost:8765/api/contents/health
curl http://localhost:8765/api/stream/health
```

For the **GraphQL version**, also check the Apollo Gateway:

```bash
curl http://localhost:4000/health
```

---

### Step 6: Test the API

All services are accessible through the **API Gateway** (`:8765`). Below is the basic usage flow:

#### 5.1 — Register a User

```bash
curl -X POST http://localhost:8765/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!@#",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### 5.2 — Login

```bash
curl -X POST http://localhost:8765/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test123!@#"
  }'
```

> The response will contain a **JWT token**. Use this token for subsequent requests.

#### 5.3 — Access Protected Endpoints

```bash
# Assign the token to a variable:
TOKEN="<token-from-login-response>"

# Get profile info
curl http://localhost:8765/api/profiles/me \
  -H "Authorization: Bearer $TOKEN"

# List content
curl http://localhost:8765/api/contents \
  -H "Authorization: Bearer $TOKEN"

# Subscription plans (public — no token required)
curl http://localhost:8765/api/subscription/plans
```

#### 5.4 — GraphQL Queries *(GraphQL version only)*

In the GraphQL version, you can fetch data from **multiple services in a single query** via the Apollo Federation Gateway:

```bash
curl -X POST http://localhost:4000/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "query": "query { users { id email firstName lastName } }"
  }'
```

You can also access **Apollo Sandbox** in your browser:

```
http://localhost:4000/graphql
```

---

### Step 7: Stop the Project

```bash
# Remove the stack:
docker stack rm ms-rest       # for REST
docker stack rm ms-grpc       # for gRPC
docker stack rm ms-graphql    # for GraphQL
```

To permanently clean up database data, remove Docker volumes:

```bash
# Remove all unused volumes
docker volume prune -f

# Or list and remove specific volumes
docker volume ls
docker volume rm <volume-name>
```

To disable Docker Swarm mode:

```bash
docker swarm leave --force
```

---

## Project Structure (Common Across All Projects)

```
ms_benchmark_<protocol>/
├── docker-compose.microservices.yml    # Compose file defining all services
├── git-localconfig-repo/               # Spring Cloud Config configurations
│   ├── api-gateway.properties
│   ├── authentication.properties
│   ├── user-service.properties
│   ├── profile-service.properties
│   ├── subscription-and-billing-service.properties
│   ├── content-management-service.properties
│   └── video-streaming-service.properties
├── spring-cloud-config-server/         # Centralized configuration server
├── api-gateway/                        # Spring Cloud Gateway
├── authentication/                     # Authentication service
├── user-service/                       # User service
├── profile-service/                    # Profile service
├── subscription-and-billing-service/   # Subscription and billing service
├── content-management-service/         # Content management service
├── video-streaming-service/            # Video streaming service
├── k8s/                                # Kubernetes manifest files
└── mkdocs.yml                          # Documentation configuration
```

**Additional files (project-specific):**

| Project | Additional File/Directory | Description |
|---|---|---|
| `ms_benchmark_gRPC` | `*/src/main/proto/*.proto` | gRPC service definitions (Protocol Buffers) |
| `ms_benchmark_GraphQL` | `*/src/main/resources/graphql/schema.graphqls` | GraphQL schema definitions |
| `ms_benchmark_GraphQL` | `graphql-gateway/` | Apollo Federation Gateway (Node.js) |

---

## 🔌 Port Map

| Port | Service | Notes |
|---|---|---|
| `8888` | Config Server | Spring Cloud Config Server |
| `8765` | API Gateway | All client requests are routed through this port |
| `4000` | GraphQL Gateway | *(GraphQL only)* Apollo Federation endpoint |
| `8000` | Authentication Service | *(internal — not exposed to host)* |
| `9000` | User Service | *(internal — not exposed to host)* |
| `9001` | Profile Service | *(internal — not exposed to host)* |
| `9100` | Subscription & Billing | *(internal — not exposed to host)* |
| `9200` | Content Management | *(internal — not exposed to host)* |
| `9300` | Video Streaming | *(internal — not exposed to host)* |
| `5433` | Auth DB (PostgreSQL) | |
| `5434` | User DB (PostgreSQL) | |
| `5435` | Subscription DB (PostgreSQL) | |
| `5436` | Profile DB (PostgreSQL) | |
| `5437` | Content DB (PostgreSQL) | |

> [!TIP]
> Microservices are not directly exposed to the host. All client requests should be routed through the **API Gateway (`:8765`)**.

---

## Troubleshooting

### Issue: Services won't start / keep restarting

**Cause:** Other services attempt to start before the Config Server is ready.

**Solution:** Check the Config Server health and wait until it is fully up:

```bash
curl http://localhost:8888/actuator/health
```

The Config Server may take up to **60 seconds** to start. Other services will automatically reconnect via their built-in retry mechanisms.

### Issue: Port conflict (port already in use)

**Cause:** Another version or application is using the same port.

**Solution:**
```bash
# Remove the existing stack
docker stack rm ms-rest  # or ms-grpc, ms-graphql

# Ensure all containers are stopped
docker ps

# Manually stop containers if needed
docker stop $(docker ps -q)
```

### Issue: GraphQL Gateway fails to start (Apollo Federation)

**Cause:** Subgraph services have not yet exposed their GraphQL endpoints.

**Solution:** The Apollo Gateway waits **45 seconds** at startup and then makes up to **15 retry attempts** to connect. It may take **3–5 minutes** for all services to be fully ready. Follow the logs:

```bash
docker service logs ms-graphql_graphql-gateway --follow
```

### Issue: Video Streaming Service fails to start

**Cause:** The `videos/` directory does not exist in the project root. The `docker-compose.microservices.yml` maps `./videos:/videos:ro`, and if this folder is missing, Docker cannot create the volume mount.

**Solution:** Create the missing directory:

```bash
mkdir -p videos
```

Then redeploy:

```bash
docker stack deploy -c docker-compose.microservices.yml ms-rest  # or ms-grpc, ms-graphql
```

### Issue: Maven dependency errors during Docker build

**Solution:** Clear the Docker build cache and try again:

```bash
docker compose -f docker-compose.microservices.yml build --no-cache
```

---

## Alternative: Running with Docker Compose (Without Swarm)

If you prefer not to use Docker Swarm, you can run the project with standard Docker Compose by removing Swarm-specific configurations (`deploy`, `placement`, `constraints`) from `docker-compose.microservices.yml`:

```bash
# After editing docker-compose.microservices.yml:
docker compose -f docker-compose.microservices.yml up -d --build
```

> [!CAUTION]
> You will need to change the network driver from `overlay` to `bridge` at the bottom of the `docker-compose.microservices.yml` file:
> ```yaml
> networks:
>   microservices-network:
>     driver: bridge   # change from overlay to bridge
> ```

## License

This project was developed for academic research and benchmarking purposes.
