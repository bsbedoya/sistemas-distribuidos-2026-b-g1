# Changelog — Di Lucca MVP 1 / Cut 1

## [Unreleased]

Baseline audited on **2026-09-06**. This record documents accumulated capabilities present in the code and documentation added by this review; it does not claim that they were developed now or constitute a published version. **MVP not validated for release; complete demo NOT VERIFIED.**

| Repository | Version evidence and initial state |
|---|---|
| `MVP/di-lucca-mvp` | `main`, HEAD `21a38a7090114a1cebe7a11ae8d81ebed9ceeca4`; initially clean working tree; backend `0.1.0-SNAPSHOT`, frontend `0.0.0`. |
| `dlc-docs` | `main`, HEAD `ce889a155c00a239fe314755b906a697ce436f02`; **`?? docs/` already existed before this review**. HU-01/02/03 are local, unversioned documents in that baseline. They are preserved. |

Local Git showed no tags or `develop`/`qa` branches. The GitHub API query confirmed only `main` in both repositories, with matching HEADs, and returned empty release and tag-reference lists. There is no evidence of `v1.0.0` in those accessible sources. Queries: [MVP branches](https://api.github.com/repos/DanielPerez1822/di-lucca-mvp/branches), [MVP releases](https://api.github.com/repos/DanielPerez1822/di-lucca-mvp/releases), [MVP tags](https://api.github.com/repos/DanielPerez1822/di-lucca-mvp/git/matching-refs/tags/), [docs branches](https://api.github.com/repos/code-corhuila/dlc-docs/branches), [docs releases](https://api.github.com/repos/code-corhuila/dlc-docs/releases), [docs tags](https://api.github.com/repos/code-corhuila/dlc-docs/git/matching-refs/tags/). This is a dated observation, not a guarantee about later changes or inaccessible information.

### Added

Capabilities present in the baseline, verified through implementation inspection:

- Authentication REST API: login, refresh with rotation, logout that revokes the user's refresh tokens, `/me` lookup, password-recovery requests, and password reset. BCrypt, signed JWTs, and active-user checks in the filter. The frontend connects login, logout, and recovery; **it does not consume refresh or `/me`**.
- Staff administration: creation, listing, lookup, update, activation, and deactivation. The actual administrative role is **`ADMIN`**, alongside `DENTIST` and `SECRETARY_ASSISTANT`; there is no `ADMINISTRATOR` enum value or patient role.
- Patients: registration, listing, detail, update, and logical deactivation; duplicate identity-document checks. Search and active-only filters sent by Angular are not implemented in the listing endpoint.
- Procedures: catalog with name, description, price, duration, update, and logical deactivation. HTTP queries and forms in Angular; local filtering in the procedure list.
- Weekly schedules, shifts/breaks, schedule copying, and slot generation/querying. Calendar populated from API data. A holiday endpoint exists only in the backend, with no generation/booking integration or Angular client found.
- Appointments: creation, filtered queries, rescheduling, and cancellation in the backend; booking form connected from the calendar. Rescheduling/cancellation have client code, but are not exposed by the current routing.
- Care: records by patient, diagnosis/notes, and procedures with applied names and prices; creation and queries connected to Angular. Update backend exists; no connected visible editing workflow was found.
- Invoices: creation from care records, queries, details, status/paid-amount changes, and internal payment/partial-payment recording. Automatic invoicing is attempted when care is recorded. This is not financial credit or tax invoicing.
- SMTP adapter for recovery, appointment notices, and HTML receipts. Its presence does not prove delivery; sending errors may be swallowed.
- Administrator dashboard calculating indicators from patients, appointments, and invoices queried through the API. It is not an independent analytics service.
- Receipt view with printing through a browser iframe. The button labeled PDF calls `print()`: there is no backend PDF generation or verified automated download.

Sources: [controllers](./12-ux-ui/mvp/backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/infrastructure/adapter/in/web/controller), [application services](./12-ux-ui/mvp/backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/application), [HTTP clients](./12-ux-ui/mvp/frontend/dilucca/src/app/core/services), [components](./12-ux-ui/mvp/frontend/dilucca/src/app/features).

### Changed

- MVP history records dockerization in `21a38a7`, login/recovery redesign in `c127204`, visual changes in `fb414b6`, administrator indicators in `29ff643`, and page-security adjustments in `15c7226`. Current files support those general changes; commit messages **do not establish functional acceptance or absence of defects**.
- This review adds only the six ADRs and this CHANGELOG. It does not modify backend, frontend, SQL, Compose, previous documents, or tests. No contradiction is silently corrected.

### Architecture

- Configured topology: **Angular → Nginx → Spring Boot → PostgreSQL**. The API also publishes host port 9000; not all external access has to pass through Nginx. The Docker frontend serves the browser build, although the project contains auxiliary SSR files.
- One JAR and a shared database; seven functional application packages. Monolith verified, modularity partial: no Maven modules per context or architectural boundary tests.
- Domain behavior, actual ports, and adapters. Partial hexagonal fidelity: ports in `domain` import application DTOs, some controllers bypass use cases, and holidays expose JPA entities.
- PostgreSQL 16, `ddl-auto=update`, error-tolerant initialization SQL; no versioned migrations. The effective schema was not queried in a live database.
- Compose: frontend/backend/PostgreSQL, bridge network, data volume, and health dependencies. Valid static configuration with temporary variables; **startup NOT VERIFIED**.
- Strangler Fig, Go, MongoDB, RabbitMQ, outbox/inbox, and four microservices represent later intent/design, not implemented components of this cut.

Details and evidence: [ADR-001](./docs/adr/ADR-001-stack-tecnologico.md), [ADR-002](./docs/adr/ADR-002-monolito-modular.md), [ADR-003](./docs/adr/ADR-003-ddd-arquitectura-hexagonal.md), [ADR-004](./docs/adr/ADR-004-postgresql.md), [ADR-005](./docs/adr/ADR-005-docker-compose.md), [ADR-006](./docs/adr/ADR-006-strangler-fig.md).

### Testing

**Actual runtime inventory: 2 test files, 3 declared cases. None established as passing in this review.** The copy inside `dlc-docs` contains the same tests and is not a second independent suite.

| Type | Count found | Actual coverage |
|---|---:|---|
| Domain/service/use-case unit tests | 0 | No JUnit cases test business rules or services. |
| Spring context integration | 1 | [contextLoads](./12-ux-ui/mvp/backend/odontosys-api/src/test/java/com/odontosys/odontosys_api/OdontosysApiApplicationTests.java), with `@SpringBootTest`; an empty method that only attempts to start the context. It does not demonstrate workflows or persistence. |
| Angular component | 2 | [app.spec.ts](./12-ux-ui/mvp/frontend/dilucca/src/app/app.spec.ts): create `App` and look for `Hello, dilucca` in an `h1`. The actual template contains only `router-outlet`; inspection shows a mismatched expectation, not an executed failure. |
| Security/RBAC/JWT | 0 | The `spring-security-test` dependency exists, but no authorization, filter, or token tests. |
| Controllers/API/MockMvc | 0 | No runtime HTTP suites or contract tests were found. |
| Repositories/PostgreSQL/Testcontainers | 0 dedicated | No dedicated schema, concurrency, transaction, or restart tests. |
| Browser E2E | 0 | No Cypress/Playwright/Selenium or equivalent automated workflow was found. |
| Manual functional tests | 0 executed in this review | No login or patient, appointment, record, or payment creation was performed against an instance. |

**About 112/112:** neither that figure nor a supporting report was found in the examined working files of either repository or in the Markdown files inside `docs/hu.zip`. Figma references such as `12:112` are not tests. Its origin is **NOT VERIFIED**; it is not converted into unit, API, or E2E evidence. Test examples in `_stacks`, `11-quality`, and architecture guides are documentation, not runtime tests. No retained Surefire/Karma/coverage report proving that figure was found either.

| Check during the audit | Result and limitation |
|---|---|
| Inventory and SHA-256 comparison | MVP code/configuration matches its copy in `12-ux-ui/mvp`; root README exists only in the MVP. This supports comparison against a common implementation, not adding duplicate tests. |
| `docker compose config --quiet` | Exit code 0 using temporary non-production values for `POSTGRES_PASSWORD`/`JWT_SECRET`; Docker configuration-access warnings inside the sandbox. Static validation only. No `.env` was created. |
| `docker version` | Client 29.2.0 found; connection also fails outside the sandbox because the `dockerDesktopLinuxEngine` engine pipe does not exist. Stack not started. |
| `mvn -B -o test` | First attempt blocked by the `C:\.m2\repository` cache. Outside-sandbox retry: `BUILD FAILURE`, dependency `spring-boot-starter-actuator:3.4.5` missing from the offline cache. **The test was never executed**; this is not evidence of a project compilation defect. |
| `npm.cmd run build` | Failed before compilation: `ng` not recognized; `node_modules` not installed. Build **NOT VERIFIED**. |
| `npm.cmd test -- --watch=false --browsers=ChromeHeadless` | Same `ng` blocker; zero cases executed. |
| Git/GitHub | Status, branches, commits, tags, and releases queried without mutations. No tag/release or functional runtime CI was found. |

Observed environment: Windows, Java **24.0.2**, Node **22.16.0**, Maven installed; Dockerfiles declare Java 21 and Node 20.19. Dependencies were not downloaded to reconstruct the environment; SMTP/healthchecks/browser proxy behavior were not tested; load and database recovery tests were not executed. The backend Dockerfile uses `-DskipTests`, and the frontend does not run Karma; building images, even if achieved later, does not equal testing the workflows.

`dlc-docs/.github/workflows/board-sync.yml` synchronizes issue statuses with a board; it is not an MVP build, test, or deployment pipeline. HU-03's `qa.md` acknowledges static inspection and pending functional tests; it contains no evidence of 112 successes.

### Documentation

- Six independent ADRs are created with context, decision, evidence, alternatives, consequences, implementation status, and scope. Their dates correspond to the audit; no approvals or historical dates are invented.
- The runtime is distinguished from the general guides and designs in `dlc-docs`. Both trees were inventoried, including hidden configuration, scripts, code, tests, and documentation, and HU-01/02/03 documents were compared with each area's code.
- Claims in context/domain, product/requirements, architecture/previous ADRs, data, OpenAPI/service catalog, DevOps/quality, navigation/wireframes, and operations, training, and governance guides were reviewed. Their examples and expected procedures are not counted as functionality or execution evidence.
- Remote Figma/dbdiagram designs and visual validation of the PDF mockup, images, and product accessibility are **NOT VERIFIED** in this review. Functional behavior is not inferred from those artifacts.
- The following matrix and differences preserve audit results without changing previous documents. ADRs `docs/adr/ADR-001..006` are the requested cut-specific series; they do not silently replace the language/persistence ADRs under `05-architecture/decisions`.

### Out of scope

- Deployed microservices, transition gateway, executed Strangler Fig migration, Go, MongoDB, Redis, RabbitMQ, saga/process manager, outbox/inbox, DLQ, and distributed reconciliation.
- MFA, signup email verification, advanced device/session management, attempt-based locking, and complete refresh-token-family reuse detection.
- Patient portal/login, mobile app, insurers, real payment gateway, electronic tax invoicing, and AI diagnosis.
- The 24-hour modification rule, audited administrator override, scheduled reminders, and signed-link confirmation: documented in the general design, not implemented in this cut.
- Longitudinal treatments, a complete odontogram, justified materials/extras, versioned prices, and immutable amendment records: these are not equivalent to the simple records/procedures implemented.
- Financial credit with limits, interest, or installments: not implemented. Internal **partial payments** do exist; `CREDIT_CARD` is a recorded method, not a credit integration.
- Advanced analytics and a persistent waiting list. Actual basic administrator KPIs exist, as does a waiting-list method that only opens a modal, without an API/persistence model.
- Browser printing is partially implemented although HU-02 excludes it from the approved scope. It is not automatically considered an accepted deliverable or complete PDF export.

### Known limitations

#### Functional Audit Matrix

Legend: **Yes (code)** means an identifiable call path, not successful execution. **Partial** identifies a concrete gap. **NV = NOT VERIFIED**. All persistence rows have JPA save intent; none has correct persistence demonstrated against live PostgreSQL. “No dedicated tests” does not exclude the generic context test, which does not cover that functionality.

| Functionality | Backend | Frontend | FE–BE connection | Persistence by code / execution | Validations | Permissions | Dedicated tests | Demo-ready |
|---|---|---|---|---|---|---|---|---|
| Login | Yes | Yes | Yes (code) | User/roles and refresh hash; NV | Credentials, active state, email | Public login; filter protects API | No | NV; seed risk |
| Refresh, logout, `/me` | Yes | Logout yes; refresh/me no | Partial | Per-repository rotation/revocation; no global transaction; NV | Valid/expired/revoked token and active user | Public refresh; authenticated logout/me | No | NV |
| Recovery | Yes | Yes | Yes, mismatched password contract | Token without `user_id`; SQL/JPA conflict; NV | Expiry/use, code; FE 6 vs BE 8 password characters | Public; no attempt limiting found | No | NV; SMTP pending |
| Staff | Yes | Yes | Yes, edited identity-document number not saved | JPA user/roles; NV | Duplicate email/document on creation; roles | ADMIN; sole-admin protection incomplete | No | NV |
| Roles | Enum and annotations | Model and admin guard | Yes for ADMIN/DENTIST/SECRETARY_ASSISTANT | N:M user/role; NV | Enum roles; no dynamic ACL | See exact matrix below | No | NV; contradictory documentation |
| Patients | CRUD with logical deactivation | List/form | Partial: search/active filters ignored | Complete mapper; NV | Unique document; birth date required only for BE creation; not required by FE | All three roles, no dentist assignment restriction | No | NV |
| Schedules | Save/query/copy | Yes | Yes, incorrect response typing compensated with `any` | Transactional replacement; NV | Time order/duration; no overlap checks | All save/read; copy ADMIN only | No | NV |
| Availability | Generate/query | Yes | Yes (code) | Slots saved; no locking/uniqueness; NV | Query avoids some duplicates; no holiday/overlap checks | All three roles | No | NV |
| Create/query appointment | Yes | Calendar/form | Yes; `date` does not match BE filter | Saves slot before appointment; NV | Patient/user/free slot exist; active state, role, and ownership missing | All three in API; UI booking ADMIN/secretary only | No | NV |
| Reschedule | Yes | Code in list/form | **Partial: list not routed** | Modifies slots before validating appointment state; NV | Rejects cancelled/completed late; no 24 h or slot-dentist check | All three in API | No | NV; no normal route access |
| Cancel | Yes | Code in list | **Partial: list not routed; wrong field** | Releases slot before validating appointment; NV | Partial state checks; no 24 h rule | All three in API | No | NV; no normal route access |
| Care/medical history | Create/read/edit | Create/read; no connected edit flow found | Partial; appointment not passed from calendar | Header/details; closure/invoice separate; NV | Diagnosis and price; no verified clinical author/ownership | API allows secretary; UI does not | No | NV |
| Procedures | CRUD with logical deactivation | Yes | Yes; service filters ignored by API, list filters locally | Historical price/name in care record; NV | Unique name, non-negative price, positive duration | All read; ADMIN/secretary write; UI restrictions differ | No | NV |
| Invoicing | Yes | Yes | Yes; detail uses POST even for dentist | Items in `invoice_items`, vulnerable numbering; NV | Partial; paid total can be changed independently | All read; ADMIN/secretary write | No | NV |
| Payments/partial payments | Yes | Yes | Yes (code) | Payment saved before invoice validation; NV | Positive; **no balance limit or idempotency** | ADMIN/secretary in API; UI shows dentist actions | No | NV |
| Financial credit | No | No | No | No model | No | No | No | Not implemented |
| Holidays | Partial CRUD | No client/screen found | No | JPA `holidays`; NV | Date/reason; no effective schedule blocking | All read; ADMIN writes | No | Not as a complete workflow |
| Email | Actual adapter | Messages/actions | Partial: success does not guarantee sending | No persisted delivery state found; NV | Depends on email and SMTP | Per operation; public recovery | No | NV |
| Dashboard/printing | Generic data API | ADMIN KPIs and print iframe | KPIs yes; local printing | No dedicated analytics/PDF engine | Partial | KPIs visible to ADMIN; exposed detail has API mismatch | No | NV |

#### Effective Permissions by Endpoint

`A = ADMIN`, `D = DENTIST`, `S = SECRETARY_ASSISTANT`. **`ADMINISTRATOR` is not recognized by the enum and must not be sent as a substitute.** Source: [SecurityConfig](./12-ux-ui/mvp/backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/infrastructure/config/SecurityConfig.java), the nine controllers, and `JwtAuthenticationFilter`.

| REST operation | A | D | S |
|---|:---:|:---:|:---:|
| `/api/admin/users`: POST, GET list/id, PUT id, DELETE id | Yes | No | No |
| `/api/patients`: POST, GET list/id, PUT id, DELETE id | Yes | Yes | Yes |
| `/api/procedures`: GET list/id | Yes | Yes | Yes |
| `/api/procedures`: POST, PUT id, DELETE id | Yes | No | Yes |
| `/api/schedules`: GET dentists/schedules/slots; POST save/generate, including singular/plural variants | Yes | Yes | Yes |
| `/api/schedules/copy`: POST | Yes | No | No |
| `/api/appointments`: POST, GET list/id, PUT id/reschedule, DELETE id | Yes | Yes | Yes |
| `/api/medical-records`: POST, PUT id, GET id and patient/id | Yes | Yes | Yes |
| `/api/invoices`: GET list/id | Yes | Yes | Yes |
| `/api/invoices`: POST from-medical-record/id, id/payments, id/send-email; PUT id | Yes | No | Yes |
| `/api/holidays`: GET | Yes | Yes | Yes |
| `/api/holidays`: POST, DELETE id | Yes | No | No |

Login, refresh, forgot-password, reset-password, `/actuator/health`, and Swagger/OpenAPI are public. `/api/auth/logout` and `/api/auth/me` require authentication. Configuration is stateless, with CSRF disabled and CORS configured. The filter reloads the user and roles from the database; it does not rely solely on JWT roles. Logout and reset revoke refresh tokens; **they do not immediately invalidate already-issued access tokens**.

No backend authorization by assigned patient, appointment/record ownership, or authoring dentist identity was found. An optional query filter is not an access restriction. The API permits more operations than the documented profiles. The frontend applies `authGuard` only to the layout and `adminGuard` to staff management; other limits depend on incomplete component controls. The menu shows schedules only to ADMIN, but `/schedules` has no role guard and the API allows D/S to save/generate schedules; hiding that link does not block direct access.

#### Implementation Findings and Risks

These are static findings reproducible by reading the code, not defects reproduced against a running instance. Priority indicates impact to evaluate before release.

| ID / priority | Evidence | Consequence |
|---|---|---|
| SEC-01 High | `DataInitializer.seedAdminUser/seedDentistUser` updates passwords for the three known accounts on every startup, without a demo-only profile. | Fixed credentials in code and loss of password changes for those accounts; contradicts preservation/secret-handling criteria. |
| SEC-02 High | `MedicalRecordController` allows S to POST/PUT; `CreateMedicalRecordService` accepts client-supplied `dentistId` and only checks existence. | Secretary can author clinical content through the API; the author is not bound to the authenticated user or clinical role. |
| SEC-03 High | Controllers/read use cases accept unrestricted IDs/filters, without assignment checks. | The dentist's “own patients/appointments only” scope is not implemented. |
| SEC-04 High | `UserController` allows administrator deactivation or removal of ADMIN; it only prevents assigning ADMIN to others. Frontend hides/prohibits some of this. | The “protected sole administrator” policy is not guaranteed by the backend. PUT `documentNumber` is ignored when calling the three-argument `updateProfile` overload. |
| SEC-05 High | Recovery uses `java.util.Random`, stores the code in plaintext, and logs it if SMTP fails; no throttling/attempt-based locking was found. | Does not meet the general design's hardened recovery with hashed challenges. |
| AUTH-01 Medium | `AuthService` stores JWT/refresh/user in `localStorage`; `isAuthenticated` checks presence only; does not consume refresh/me. | Session expiry/renewal and visual permission updates are incomplete; tokens are exposed if scripts execute. |
| AUTH-02 Medium | `error.interceptor.ts` logs out on both 401 and 403 and replaces the error with `Error(message)`. | A forbidden operation logs the user out; status and validation details are lost for consumers expecting `err.error`. |
| APT-01 High | Appointment creation checks only user/patient existence and slot availability. | Does not validate an active DENTIST user, active patient, `slot.dentistId == dentistId`, future date, or procedure duration. |
| APT-02 High | Booking, rescheduling, and cancellation perform multiple saves without a use-case transaction; cancellation/rescheduling save slots before `appointment.cancel/reschedule`. | State rejection or a later failure can leave inconsistent slots. With no `@Version`, `@Lock`, or active-booking constraint found, concurrency is also a risk. |
| APT-03 Medium | `AppointmentListComponent` exists/is imported, but `/appointments` points to `CalendarViewComponent`; its selector is not used in templates. | Rescheduling/cancellation have no normal path from current routes. Client sends `cancellationReason`; backend DTO reads `reason`. |
| APT-04 Medium | Appointment client sends `date`; backend receives only `startDate/endDate`. Procedure selector only appends the name to `reason`. | API does not apply the date filter; validating contiguous slots in the UI does not reserve every interval required by the procedure. |
| SCH-01 High | `GenerateSlotsService` checks existence by start time, then saves; it does not query `HolidayJpaRepository` or check global overlaps. | Holidays do not block scheduling; overlapping shifts and concurrency can generate incompatible availability. |
| SCH-02 Medium | Schedules are replaced, but existing slots are not recalculated; `SetDentistSchedulePayloadRequestDto.days` has no nested `@Valid`. | Old availability may remain; nested validation is insufficient, although the model checks some schedule/duration rules. |
| PAT-01 Medium | `PatientController.getAllPatients()` and the procedure listing ignore `search/activeOnly`. | Patient search and selectors requesting active-only records receive all records. The procedure list does filter locally. |
| UI-01 Medium | Patient creation requires birth date in the backend DTO but not the form; password reset requires 8 characters in BE versus 6 in FE. | Apparently valid forms can be rejected by the API. `UpdatePatientRequest` is typed as partial although BE requires complete fields; the current form does send those fields. |
| CLN-01 High | `CreateMedicalRecordService` marks an appointment COMPLETED without checking patient/dentist/state consistency and hides invoice exceptions. | Can save care without an invoice or with an incorrect clinical link. Calendar passes patientId but not appointmentId; the list does not bind it to the form. |
| CLN-02 Medium | Clinical PUT replaces content/items; it records neither amendments nor the editing actor. | Not an immutable longitudinal history. Updating care does not explicitly synchronize the already-issued invoice. |
| FIN-01 High | `RegisterPaymentService` saves Payment before `invoice.addPaymentAmount`; that method rejects paid/cancelled invoices but permits overpayment. | Payment may persist despite rejection; negative balance and inconsistent financial states. FE also has no balance maximum. |
| FIN-02 High | Invoice PUT assigns `paidAmount` and status without reconciling payment records; `CreateInvoiceService` numbers invoices with `count()+1`. | Paid total may differ from the sum of payments; numbering collisions or duplicate invoicing of care under concurrency. No idempotency key. |
| UI-02 Medium | Invoice/procedure/schedule-copy views show actions without restrictions equivalent to the backend. Invoice detail starts with a creation POST. | Dentist may receive 403 and logout when viewing detail or using buttons; schedule copying causes the same for D/S. |
| DB-01 High | SQL/JPA differ in invoice_items/details, holidays, breaks, schedule_id, and reset user_id. Hibernate runs before SQL `IF NOT EXISTS`. | A 15-table model and all its foreign keys cannot be guaranteed; initialization can hide errors. See ADR-004. |
| MAIL-01 Medium | `SpringMailAdapter` catches failures; UI confirms receipt sending without verifying delivery; no durable sending state/retry exists. | A successful response does not prove email recovery or receipt of notifications. |
| UI-03 Medium | `InvoiceDetailModalComponent.downloadPdf()` interpolates data into HTML and uses `document.write` on an iframe without explicit escaping. | HTML/script injection risk during printing; exploitation NOT VERIFIED. Also not a backend PDF generator. |
| QA-01 High | Three basic tests, incomplete execution dependencies, no E2E or persistence evidence. | Insufficient basis to certify every workflow or release `v1.0.0`. |

#### Documentation Versus Implementation

| Documents | Verified difference |
|---|---|
| `docs/hu/HU-01/decision-stack.md`, `comparison-frontend.md` | Describe Docker/Nginx as tested. The audit validated only configuration; `qa.md` and `release.md` also acknowledge missing execution. |
| `docs/hu/HU-01/comparison-backend.md`, `comparison-database.md` | Spring/PostgreSQL transaction capabilities do not imply implemented global transactions. Critical services perform separate writes. |
| `docs/hu/HU-02/discovery.md`, `user-profiles.md`, `01-context/scope.md` | “Verified ADMINISTRATOR,” prohibited secretary authorship, and dentist limits do not match the enum/controllers. |
| `docs/hu/HU-02/data-model.md`, `docs/hu/HU-03/mvp.md` | 15 tables refers to the script; JPA specifies other names/tables. Schedule-slot and recovery relationships do not fully match mappers. |
| `docs/hu/HU-02/user-flows.md` | Overpayment rejection, appointment/slot consistency, and clinical-invoice closure are requirements, not actual guarantees of current services. |
| `12-ux-ui/navigation-map.md` | Designed `/app/...`, `/recover-password`, `/billing`, `/clinical/...` versus actual `/patients`, `/forgot-password`, `/invoices`, `/medical-records` routes and modals. |
| `01-context`, `02-domain`, `03-product/vision.md`, `04-requirements`, `05-architecture`, `06-data`, `09-microservices` | Describe four services, Clinical Go/MongoDB, owned schemas, events, prices outside Clinical, and 24 h rules. Runtime: one backend, shared PostgreSQL, prices in care, and internal calls. They do not establish Cut 1 acceptance. |
| `05-architecture/decisions/ADR-002-polyglot-persistence.md` | Declares Accepted status and says it replaces Clinical in PostgreSQL. Does not match the executable cut. This record neither deletes it nor invents approval resolving the contradiction. |
| `03-product/product-backlog.md`, `04-requirements/user-stories.md`, `traceability-matrix.md` | Backlog stories/Pending validations; they do not constitute 16 accepted stories even if some code exists. |
| `10-devops`, `11-quality`, `13-operations`, `_stacks` | CI, Kubernetes, testing, and observability procedures/examples are not executed MVP pipelines, deployments, or reports. |
| `docs/hu/HU-03/qa.md` | Its “initially unchanged” Git state belongs to another review; this audit already had untracked `docs/`. The document is preserved and the difference made explicit. |
| `docs/hu/HU-03/release.md` | Declares release pending; matches Git/GitHub and the lack of demonstrated reproducibility. |

#### Release Prerequisites

1. Agree on the cut's scope and permission matrix; resolve clinical authorship, access to other dentists' data, and administrator protection in particular.
2. Correct or explicitly accept high security/consistency risks. Hiding Angular actions is not sufficient.
3. Prepare dependencies and operational Docker; build backend/frontend and run tests on the declared versions. Replace obsolete expectations and add business-rule, API, RBAC, PostgreSQL, and concurrency tests.
4. Unify SQL/JPA, establish migrations, and check the actual table, foreign-key, constraint, and seed catalog. Verify persistence/restart without resetting changed credentials.
5. Connect and test rescheduling/cancellation, filters, errors, the appointment-care link, and invoice/payment continuity. Test both allowed and denied operations for each role.
6. Execute login → patient → schedule/slot → appointment → care/procedures → invoice → partial/full payment using test data, from the interface through PostgreSQL. Retain evidence by commit, command/scenario, and result.
7. Verify SMTP with actual configuration if included in the demo; distinguish accepted responses from effective delivery. Validate printing and accessibility if included in acceptance.
8. Resolve documentation contradictions and confirm acceptance. Only then apply the promotion and versioning workflow specified in `release.md`.

**Audit conclusion:** broad capabilities implemented in code, partial integration and architecture, several documented rules absent, and functional validation pending. The MVP is not declared demo-ready or release-ready. No commits, pushes, tags, or publications were made.
