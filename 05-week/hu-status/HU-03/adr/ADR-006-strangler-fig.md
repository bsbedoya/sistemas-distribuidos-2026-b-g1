# ADR-006: Reserve Strangler Fig for a future, evidence-driven migration

- Status: Proposed future strategy; no extraction implemented.
- Date: 2026-09-06 (audit date; original approval date not established).
- Scope: Evolution after MVP 1 / Cut 1; current baseline `7a44eb8`.

## Context

[architecture.md](../../HU-01/architecture.md), [decision-stack.md](../../HU-01/decision-stack.md), [discovery.md](../../HU-02/discovery.md) and [mvp-scope.md](../../HU-02/mvp-scope.md) defer microservice extraction until the monolith is stable.

Other UX documents describe MongoDB-backed clinical commits and event-driven billing as if part of a user journey. Those passages are design intent; the implementation contains a shared PostgreSQL database and synchronous in-process calls.

## Decision

Record Strangler Fig as the **documented future direction**, not a completed migration or an approved extraction schedule. After the monolith has a reproducible baseline, consider introducing a facade and moving one capability at a time behind it.

No first service, technology choice, timeline, owner or migration acceptance has been proven. These remain decisions for a later stage.

## Evidence in the project

| Source | What is actually present |
|---|---|
| [Compose](../../docker-compose.yml) | One backend and one PostgreSQL service; no gateway, broker or separately deployed business service. |
| [Nginx configuration](../../frontend/dilucca/nginx.conf) | All `/api/` traffic goes to `backend:9000`. Static-file serving plus one proxy upstream is not an implemented strangler migration. |
| [Backend POM](../../backend/odontosys-api/pom.xml) | Spring MVC, JPA, security and mail; no MongoDB/RabbitMQ/service-gateway implementation is supplied. |
| [Clinical creation](../../backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/application/medicalrecord/CreateMedicalRecordService.java) | Direct repository calls and synchronous invoice use-case invocation; no clinical integration event, outbox, inbox, DLQ or reconciliation worker. |
| [Navigation design](../../HU-02/navigation-map.md) and [wireframes](../../HU-02/wireframes.md) | Describe independent Clinical/Appointments/Billing processing and MongoDB, contradicting the current execution model if treated as implemented scope. |
| [Release criteria](../../HU-03/release.md) | A later Strangler Fig plan must not block monolithic delivery, but monolithic reproducibility and acceptance are still pending. |

## Alternatives considered

- **Keep the monolith:** remains valid if no measured need for independent deployment, scaling or isolation appears.
- **Big-bang rewrite:** would replace the entire system before establishing parity; no such implementation or approved plan exists here.
- **Incremental extraction:** the documented preferred direction, conditional on bounded capability ownership and regression evidence.

No alternative has been benchmarked or operationally compared during this audit.

## Consequences

### Positive

- A future migration can preserve a working baseline and limit the traffic moved at each step.
- Boundaries can be selected using actual dependencies and data rather than folder names.
- Cut 1 does not need distributed infrastructure to demonstrate its core scope.

### Negative

- Coexistence would add routing, observability, contract/version and data-consistency work.
- Shared tables and synchronous clinical-to-billing coupling make extraction costly.
- Today's test failures and missing E2E/PostgreSQL evidence do not provide a safe parity baseline.
- Routing rollback alone would not reverse data changes made by an extracted service.

## Implementation status

**Future / not implemented.** Before any extraction, establish a passing regression baseline, choose a capability with an explicit reason, assign data ownership, define contracts and migration/rollback checks, then demonstrate parity before retiring monolith routes.

These are future conditions, not assertions that a facade, extracted service or data migration exists today.

## Scope

No microservices, gateway, RabbitMQ, MongoDB, outbox/DLQ or traffic migration belongs to the implemented Cut 1 release claim. This ADR changes documentation only and does not authorize or perform extraction.

