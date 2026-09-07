# ADR-002: Deliver one backend with internal functional modules

- Status: Recorded existing decision; monolithic deployment implemented, modular boundaries partial.
- Date: 2026-09-06 (audit date).
- Scope: MVP 1 / Cut 1, commit `7a44eb8`.

## Context

Authentication, scheduling, clinical care and collection share data and collaborate within one process. [architecture.md](../../HU-01/architecture.md) calls these internal modules and explicitly defers independently deployed microservices.

The repository provides one Maven application, not one application per capability. Its top-level Java packages are technical layers; functional packages primarily organize application services and inbound ports. Naming those packages does not establish isolated bounded contexts.

## Decision

Keep one Spring Boot backend deployment and one PostgreSQL database for Cut 1. Describe the implementation as a **monolith with partial internal modularity**. Retain capability-specific use cases and ports while acknowledging direct dependencies and shared data.

No independent module deployment, database ownership enforcement or module isolation has been demonstrated.

## Evidence in the project

Java paths below are relative to [the backend package](../../backend/odontosys-api/src/main/java/com/odontosys/odontosys_api).

| Observation | Source and significance |
|---|---|
| One application | [pom.xml](../../backend/odontosys-api/pom.xml) and `OdontosysApiApplication.java`; no Maven module graph or independently versioned capability artifacts. |
| Capability organization | `application/auth`, `patient`, `procedure`, `schedule`, `appointment`, `medicalrecord`, `invoice`; corresponding `domain/port/in` packages. |
| Shared deployment | [docker-compose.yml](../../docker-compose.yml) has one backend, one frontend and one database. Three containers do not mean three business microservices. |
| Shared composition | `infrastructure/config/AppConfig.java` wires use cases to repository, security and email ports. |
| Cross-capability coupling | `application/medicalrecord/CreateMedicalRecordService.java` accesses patients, users, procedures and appointments, then calls invoice creation synchronously. |
| Shared persistence | `infrastructure/adapter/out/persistence` contains all repositories and mappings; database references cross capability boundaries. |
| Bypassed use cases | `UserController` reads/updates the user repository directly; `ScheduleController` reads users and injects concrete `CopyDentistScheduleService`; `HolidayController` uses a JPA repository/entity directly. |
| Isolation enforcement | No Spring Modulith/ArchUnit module tests, separately compiled modules or enforced package APIs were found in the manifests and tests. |

The deployed request path is configured as browser Angular → Nginx → Spring Boot → PostgreSQL. It was **not** exercised end to end in this audit.

## Alternatives considered

- **Immediate microservices:** future diagrams discuss a facade and extracted services. The repository does not implement their independent deployment, contracts, messaging or data ownership.
- **Unstructured monolith:** fewer interfaces initially, but would discard the existing use-case and adapter separation. This is an audit comparison, not evidence of a historical team vote.
- **Stricter internal modules:** separate capability APIs and boundary tests remain a possible incremental improvement; they are not present merely because this ADR records the monolith decision.

## Consequences

### Positive

- Clinical and financial coordination can occur without distributed messages.
- One backend is simpler to build and diagnose than multiple independent services.
- Existing functional grouping provides starting points for future boundary work.

### Negative

- A release or failure of the backend affects all capabilities.
- Shared tables and direct cross-capability calls increase extraction cost.
- The single database does not automatically make a multi-step use case atomic.
- There is no measured evidence of independent scaling or fault isolation.

## Implementation status

**Monolith implemented; modularity partially implemented.** Important orchestration is not enclosed in a use-case transaction: appointment creation/rescheduling/cancellation, clinical record creation plus invoice generation, and payment registration. Repository-level transactions cannot roll back separately committed operations as one workflow.

Clinical creation catches and ignores invoice-generation failures. This means a successful clinical API response cannot establish successful billing. These are current limitations, not benefits of monolithic deployment.

## Scope

Applies to the current backend and shared relational model. No module extraction is authorized or claimed. [ADR-003](ADR-003-ddd-arquitectura-hexagonal.md) evaluates dependency direction; [ADR-006](ADR-006-strangler-fig.md) records future migration intent.

