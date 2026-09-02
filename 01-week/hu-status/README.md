<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.

```
 Your weekly grade is read AUTOMATICALLY from this file:
   01-week/hu-status/README.md  (inside YOUR fork). English. -->
```

# Weekly Status - Week 01

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->

* FULL_NAME: Brayan Smith Bedoya
* GITHUB_USER: bsbedoya
* TEAM: OdontoSys
* SPRINT_GOAL: Define the initial distributed architecture of OdontoSys, establish the main microservice boundaries, select the technology and persistence strategy for each service, and document the responsibilities of the Appointment Service.

<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID       | Title                                                                      | Status (todo/doing/done) | Evidence (PR or commit URL) |
| ----------- | -------------------------------------------------------------------------- | ------------------------ | --------------------------- |
| HU-ARCH-001 | Define the initial architecture and microservice boundaries of OdontoSys   | done                     | YOUR_COMMIT_URL             |
| HU-APT-001  | Define the responsibilities and main operations of the Appointment Service | done                     | YOUR_COMMIT_URL             |
| HU-ARCH-002 | Define independent persistence for the main OdontoSys microservices        | done                     | YOUR_COMMIT_URL             |

## 2. My individual contribution

* I contributed to the initial architecture documentation for **OdontoSys**, defining how the system will be divided into independent services according to business responsibilities.
* I documented the general architecture with an **API Gateway**, a transversal **Auth Service**, four main business microservices, **RabbitMQ**, and an analytics component.
* I contributed to the definition of the four main business microservices: **Patients, Appointments, Clinical, and Billing**.
* I documented the technology and persistence strategy associated with the main services, using **Spring Boot**, **Go**, **PostgreSQL**, and **MongoDB** according to the responsibility of each component.
* I defined the **Appointment Service** as a Spring Boot microservice with PostgreSQL persistence.
* I documented the main responsibilities of the Appointment Service, including appointment creation, availability queries, rescheduling, cancellation, attendance confirmation, appointment state management, and dentist schedule consultation.
* I defined the initial appointment lifecycle with the following states: `PROGRAMADA`, `CONFIRMADA`, `EN_ATENCION`, `FINALIZADA`, `CANCELADA`, and `NO_ASISTIO`.
* I contributed to the definition of the persistence isolation rule: each microservice owns its database and must not directly access another microservice's database.
* I documented the initial asynchronous communication approach using **RabbitMQ** as the messaging mechanism between system components.
* I helped establish an architecture focused on service decoupling so that the different components can be developed, deployed, and evolved independently.

## 3. Blockers and risks

* The architecture is still at an initial documentation stage, so some service contracts and communication details need to be refined during implementation.
* The REST API contracts between the API Gateway, Auth Service, and business microservices have not yet been fully defined.
* The exact events that will be exchanged through RabbitMQ still need to be documented.
* Authentication and authorization integration using JWT and RBAC must remain consistent across all microservices.
* Database isolation must be preserved during implementation to avoid direct dependencies between services.
* The final deployment and infrastructure strategy for the distributed architecture still requires validation.
* The Appointment Service requires additional business rules for availability, scheduling conflicts, appointment transitions, and dentist schedules before implementation can be considered complete.

## 4. Plan for next week

* Continue refining the architecture documentation according to the project requirements.
* Define the REST API contract for the Appointment Service.
* Define the main domain entities and business rules related to appointments and dentist availability.
* Specify valid appointment state transitions and validation rules.
* Define how the Appointment Service will communicate with the Patient, Clinical, Billing, and Auth services.
* Detail the events that may be published or consumed through RabbitMQ.
* Start organizing the Appointment Service using DDD and hexagonal architecture principles.
* Define the PostgreSQL persistence model required by the Appointment Service.
* Keep the architecture documentation aligned with the implementation decisions made by the team.

## 5. Compliance self-check

* [ ] Conventional Commits - `type(scope): summary`
* [ ] Per-environment HU branch + PR to that environment (`hu-xxx-dev -> develop`, ...)
* [ ] Testable acceptance criteria
* [ ] Tests added/updated (unit / integration)
* [x] DDD / hexagonal boundaries considered in the architecture
* [x] Independent persistence defined for each microservice
* [x] No direct database access between microservices
* [x] No secrets documented; configuration should be managed through environment variables

### Notes on the unchecked items

* The work completed during this week was mainly focused on architecture and project documentation.
* Implementation-specific HU branches and pull requests were not yet part of this architectural documentation stage.
* Acceptance criteria still need to be formalized when the Appointment Service business rules are detailed.
* No production implementation was completed as part of this architectural documentation, so unit and integration tests are still pending.

## 6. Evidence links

* Architecture and microservices documentation: `02_arquitectura_y_microservicios.md`
* Architecture documentation commit: YOUR_COMMIT_URL
* Course learning material (OVAs): https://code-corhuila.github.io/ova-web/2026-B/distribuidos/
