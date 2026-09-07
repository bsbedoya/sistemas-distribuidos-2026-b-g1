# ADR-005: Use Docker Compose as the intended local execution topology

- Status: Recorded deployment decision; configuration present, execution not verified.
- Date: 2026-09-06 (audit date).
- Scope: Development/demo topology of MVP 1 / Cut 1 at `7a44eb8`.

## Context

The [README](../../README.md) instructs users to prepare `.env` and run `docker compose up --build`. [release.md](../../HU-03/release.md) requires clean-environment reproducibility before creating the final tag/release.

The existence of Dockerfiles and health checks is not evidence that images build, containers become healthy or browser requests reach PostgreSQL.

## Decision

Retain Compose for the intended three-service local/demo environment. Record it as **configured, not operationally accepted**. The architectural decision does not waive the reproducibility gate.

## Evidence in the project

| Component | Configuration |
|---|---|
| Frontend build | [Dockerfile](../../frontend/dilucca/Dockerfile): `node:20.19-alpine`, `npm ci`, `npm run build`; copies `dist/dilucca/browser` into `nginx:1.27-alpine`. |
| Backend build | [Dockerfile](../../backend/odontosys-api/Dockerfile): Maven `3.9.9` / Temurin 21 Alpine builder, dependency prefetch, `mvn -B -DskipTests package`; Temurin 21 JRE runtime as non-root user `spring`. |
| PostgreSQL | [docker-compose.yml](../../docker-compose.yml): `postgres:16-alpine`, named volume mounted at `/var/lib/postgresql/data`, no published host port. |
| Network | Shared bridge `dilucca-network`; backend JDBC uses `postgres:5432`; Nginx proxies `/api/` to `backend:9000`. |
| Ports | Frontend defaults to host 8080 → 80; backend host 9000 → 9000. |
| Startup order | Backend depends on healthy PostgreSQL; frontend depends on healthy backend. |
| Health checks | PostgreSQL `pg_isready`; backend `wget` to public `/actuator/health`. There is no frontend health check. |
| Secrets | `POSTGRES_PASSWORD` and `JWT_SECRET` are required interpolation variables. `.env.example` has placeholders; `.env` is ignored. |
| Optional integration | SMTP variables are passed through; mail health is disabled. Healthy API status would therefore not prove email delivery. |
| Build context | Both projects have `.dockerignore`; generated artifacts/dependencies are excluded. |
| Development alternative | Angular [proxy.conf.json](../../frontend/dilucca/proxy.conf.json) targets local API port 9000. Local database default 5434 is not exposed by this Compose setup. |

### Verification performed

| Check on 2026-09-06 | Result |
|---|---|
| Docker client | Version `29.2.0` available. |
| Engine access, retried outside sandbox | Failed: `dockerDesktopLinuxEngine` named pipe not found. No container execution was possible in the observed environment. |
| `docker compose config --quiet` with checkout defaults | Failed: `POSTGRES_PASSWORD` missing. No local `.env` was present. |
| `docker compose --env-file .env.example config --quiet` | Configuration validation completed without a Compose configuration error; sandbox emitted Docker-config access warnings. Placeholder validation is not a real deployment. |
| Local Angular production build | Passed outside sandbox, on Node 22.16.0. Does not establish Docker image build success. |
| Local backend build through `mvn test` | Main code compiled; test compilation failed on incompatible/missing test types. |
| Image builds, `docker compose up`, health checks, proxy, restart persistence | **NO VERIFICADO**. |

### Identified build blocker

The backend image uses `-DskipTests`, which skips test execution but still compiles test sources in the normal Maven lifecycle. The current tests fail to compile because `ScheduleControllerTest` references nonexistent `CopyDentistScheduleUseCase` and `JwtAuthenticationFilterTest` supplies `TokenProviderPort` where `JwtTokenProvider` is required.

Therefore the Docker build command is expected to encounter the same source-level blockers. This is a lifecycle/source inference, **not an observed Docker build failure**. No source changes or test-skipping workaround were applied.

## Alternatives considered

- **Local Java/Node/PostgreSQL processes:** supported in part by configuration, but require separately prepared services and environment variables.
- **Kubernetes or independently deployed services:** no manifests or operational evidence were found; unnecessary to assert for Cut 1.
- **Compose:** already present and suitable for a bounded local topology; successful execution still needs evidence.

## Consequences

### Positive

- Declares service names, ports, dependency health and volume placement together.
- Uses same-origin API requests through Nginx.
- Separates build tools from runtime images.

### Negative

- Requires a working Docker engine, image/dependency downloads and valid secrets.
- Current backend test compilation can block image creation even with test execution skipped.
- Health checks are narrower than functional acceptance and do not verify SMTP.
- No TLS/reverse-proxy production hardening, orchestration failover or tested backup process is established by these files.
- Running `docker compose up` without required variables fails before starting services.

## Implementation status

**Configured; end-to-end execution NO VERIFICADO.** Do not use the wording “Docker works” or “reproducible release” until clean image builds, healthy startup, API/browser smoke tests and database restart checks have been recorded for the same revision.

## Scope

Local/development demo only. This audit did not start Docker Desktop, modify environment files, deploy containers, remove volumes or create a release.

