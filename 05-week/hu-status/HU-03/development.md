# Configuration and development
## Phase 1 — Diagnosis
| Area | Exists | Missing |
|---|---|---|
| Architecture | Monolith with frontend, backend and database. | Reinforce internal modular boundaries before future extractions. |
| Frontend | Angular, routes, guards, services and screens. | QA of prioritized flows and correction of proven defects. |
| Variables | `.env.example` of the monolith. | Valid local secrets to run the environment. |
| Persistence | PostgreSQL, SQL schema, initial data and JPA. | Validate initialization, constraints and persistence in execution. |
| Docker | Dockerfiles, Compose, network, volume and health checks. | Run the full stack and preserve evidence. |
| Communication | Angular/Nginx `/api` → Spring Boot. | Validate proxy, CORS and error handling. |
| Security | JWT/RBAC, BCrypt and public/protected routes. | Authentication, authorization and expiration tests. |
| README | Start with documented Docker Compose. | Confirm that it works from a clean environment. |
## Phase 2 — Necessary work
1. Prepare local `.env` without versioning secrets.
2. Build frontend, backend and PostgreSQL using the existing Compose.
3. Verify the prioritized flows of HU-02 and correct real defects.
4. Complete acceptance criteria and automated tests of the monolith.
5. Deliver a reproducible version of Cut 1.
After stabilizing Cut 1, the documented modules and contracts will be used to choose the first capacity to extract using Strangler Fig. No extraction should begin before having regression tests that protect the monolithic behavior.
## Environment preparation
The local environment requires Docker Desktop with Compose. Before the first boot, `.env.example` is copied to `.env` and only sensitive local values ​​are replaced. `POSTGRES_PASSWORD` and `JWT_SECRET` are required; the secret JWT must meet the length indicated by the project. Mail variables can remain empty if actual sending will not be tested.
Compose creates three containers connected by `dilucca-network`: Nginx/frontend, Spring Boot/backend, and PostgreSQL. The database persists data using `postgres_data`. The backend waits for the PostgreSQL health check and the frontend depends on the healthy state of the backend.
## Responsibilities per layer
| Layer | Responsibility | Expected evidence |
|---|---|---|
| Angular | Early validation, navigation, states and consumption HTTP. | Correct build and testing of components/services. |
| Nginx | Serve frontend and forward `/api`. | Functional proxy from the browser. |
| Spring Boot | Authorization, rules, transactions and API. | Unit/integration tests and documented answers. |
| JPA/Hibernate | Mapping and repositories. | Persistence without relationship errors. |
| PostgreSQL | Restrictions, data and durability. | Initial scheme, constraints and restart without loss. |
## Development plan by functionality
### Authentication and users
Review login, refresh, logout, current user, recovery and administration. Token revocation, BCrypt, inactive user and `/api/admin/**` protection should be checked. The `ADMIN`/`ADMINISTRATOR` difference requires a single decision before final testing.
### Patients
Validate registration, search, detail, editing and logical cancellation. The document must remain unique and the forms must handle optional fields without losing information. Physical deletion should not be used for history or billing related data.
### Agenda and appointments
Verify creation/copy of schedules, slot generation, availability query, appointment creation, rescheduling and cancellation. Operations must be protected against double booking and maintain consistency between appointment and slot.
### Clinical care
Check creation and consultation by patient of histories, optional association with appointment and selection of procedures. The name and price applied must be kept as historical data.
### Billing and payments
Validate creation from medical history, query, update allowed, payment record and balance calculation. Negative amounts, payments over balance and double shipments must be rejected.
## Definition of done for a feature
- revised acceptance criteria;
- interface connected with real endpoint;
- critical validations in backend;
- proven persistence;
- tested authorization for allowed and denied roles;
- automated testing proportional to risk;
- updated documentation;
- pending defect registered and not hidden.
## Relevant technical debt
The automated coverage found is minimal, there is no proven CI and `ddl-auto=update` does not replace versioned migrations for stable delivery. Base constraints are also missing for some invariants, especially single reservation of slots and monetary values. These deficiencies should be prioritized by risk and not necessarily block an academic demonstration if they are transparently recorded.
