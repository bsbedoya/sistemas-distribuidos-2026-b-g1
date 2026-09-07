# ADR-001: Retain the implemented Angular, Spring Boot and PostgreSQL stack

- Status: Recorded existing decision; implementation present, release acceptance pending.
- Date: 2026-09-06 (audit date, not an inferred original decision date).
- Scope: MVP 1 / Cut 1 at commit `7a44eb80ab32db30f67f9c635ada8bf619a5825d`.

## Context

Di Lucca already contains an Angular staff interface and a single Java backend for authentication, personnel, patients, schedules, appointments, clinical records, procedures, invoices and internal payments. Replacing either runtime would require a rewrite before demonstrating the existing workflows.

[The stack decision](../../HU-01/decision-stack.md) and the three comparison documents describe continuity as the reason for choosing these technologies. Their statements about team experience are explicitly an academic narrative, not independently established team history. Their claims that Docker was already proven are not supported by this audit.

## Decision

Retain Angular with TypeScript, Spring Boot with Java, and PostgreSQL for Cut 1. Build the browser application with npm/Angular CLI, build the API with Maven, and use Nginx to serve the browser assets and proxy API requests in Compose.

This records the technology selection, not a declaration that all features work or that the release has been approved.

## Evidence in the project

| Layer | Observed evidence |
|---|---|
| Frontend | [package.json](../../frontend/dilucca/package.json) declares Angular `^20.1.0`, Angular CLI `^20.1.6`, TypeScript `~5.8.2`, RxJS, forms and routing; `package-lock.json` supplies resolved dependencies. |
| UI structure | [app.routes.ts](../../frontend/dilucca/src/app/app.routes.ts), standalone feature components, `core/services`, models, guards and interceptors implement a staff SPA. |
| Backend | [pom.xml](../../backend/odontosys-api/pom.xml) selects Spring Boot `3.4.5`, Java release `21`, Web, Validation, Security, Data JPA, Mail, Actuator, JJWT `0.12.6` and SpringDoc `2.8.8`. Artifact version is `0.1.0-SNAPSHOT`. |
| Database | PostgreSQL JDBC driver and [application.properties](../../backend/odontosys-api/src/main/resources/application.properties); Compose selects `postgres:16-alpine`. |
| Serving | [Nginx configuration](../../frontend/dilucca/nginx.conf) serves static assets and proxies `/api/` to `backend:9000`. The Docker frontend does not run the residual Express/SSR source files. |
| Verification | On 2026-09-06, `npm.cmd run build` succeeded using Node `22.16.0`; Angular produced `dist/dilucca/browser`. This is not verification of the Docker Node `20.19-alpine` build. |
| Backend verification | `mvn test` compiled 256 production Java sources with local Java `24.0.2`, targeting release 21, then failed compiling tests. No backend test passed in this run. |
| Frontend tests | `npm.cmd test -- --watch=false --browsers=ChromeHeadless` executed two Jasmine component tests: one passed, one failed. |

The [CHANGELOG audit](../../CHANGELOG.md) records the exact failures, functional traceability and release limitations.

## Alternatives considered

- **React:** discussed in [comparison-frontend.md](../../HU-01/comparison-frontend.md); technically possible, but no React implementation or migration benefit is demonstrated.
- **Go:** discussed in [comparison-backend.md](../../HU-01/comparison-backend.md); would replace Java use cases, security and persistence. No comparative load benchmark exists.
- **MongoDB:** discussed in [comparison-database.md](../../HU-01/comparison-database.md); no current driver, deployment or clinical document store exists.
- These are documented alternatives, not prototypes tested during this review.

## Consequences

### Positive

- Retains existing HTTP clients, forms, domain behavior and persistence adapters.
- Uses one backend ecosystem for REST, validation, security and relational persistence.
- A single database supports local transactions when the application actually defines them.

### Negative

- Framework and runtime upgrades affect a broad application.
- Angular types and Java DTOs are maintained separately and already diverge in several contracts.
- Compilation does not prove authorization, database integrity or successful browser workflows.
- The interface uses localStorage for tokens and has no automatic refresh flow, despite a backend refresh endpoint.

## Implementation status

**Implemented as a technology stack; partially implemented as an accepted MVP.** Angular builds locally and backend production sources compile. Backend tests do not compile; one frontend test fails. PostgreSQL execution, SMTP delivery, containers and the complete demo are **NO VERIFICADO**.

## Scope

This ADR selects the current stack only. It does not approve production readiness, introduce microservices, promise E2E coverage, or change source code. Deployment, modularity, hexagonal boundaries, database drift and future extraction are addressed in ADR-002 through ADR-006.

