# QA — Evidence from This Review

# Backend Software Quality Test Report

## 1. Introduction

This report documents and evaluates the tests identified in the backend of **Di Lucca MVP / OdontoSys API**, a modular monolith developed with Spring Boot. Its purpose is to provide traceability of what was tested, the observed results, the quality assessment, and the remaining improvement opportunities.

The scope includes authentication, patients, procedures, appointments, schedules, medical records, invoicing, JWT security, HTTP controllers, and application startup.

The repository contains an automated JUnit test suite. The evidence used for this report consists of the test source code, Maven Surefire results, and JaCoCo coverage generated during the verified execution.

## 2. Executive Summary

| Indicator | Verified result |
|---|---:|
| Execution date | September 2, 2026 |
| Reviewed version | `2f8521e` (`main`) |
| Test classes | 28 |
| Tests executed / passed | 112 / 112 |
| Failures / errors / skipped | 0 / 0 / 0 |
| Pass rate | 100% |
| Build result | `BUILD SUCCESS` |

**Conclusion:** the existing suite is stable and all its automated acceptance conditions pass. The result is positive for the covered units and isolated layers. It does not, by itself, prove full system behavior with PostgreSQL, a real email provider, containers, a browser, or concurrent traffic.

## 3. Scope, Strategy, and Environment

| Test level | Tools | What it validates | What it does not validate |
|---|---|---|---|
| Application unit | JUnit 5 and Mockito | Business rules, exceptions, state changes, and calls to mocked ports | Real persistence, network, and external services |
| Web layer | `@WebMvcTest`, MockMvc, Spring Security Test | Routes, HTTP status codes, serialization, and delegation | Complete flows through the database |
| Security unit | JUnit and Mockito | Token behavior and the JWT authentication filter | Real attacks and production configuration |
| Application context | `@SpringBootTest`, test profile, H2 | Beans, JPA mappings, repositories, and startup | Full PostgreSQL compatibility |

Verified environment:

- Java 21.0.9.
- Spring Boot 3.4.5.
- Maven Wrapper with Maven 3.9.16.
- H2 in-memory database for the context test.
- JaCoCo 0.8.13 for coverage measurement.
- Windows operating system and `America/Bogota` time zone.

Run from `backend/odontosys-api`:

```powershell
.\mvnw.cmd test
```

The overall pass criterion was a successful build with no failures, errors, or skipped tests.

## 4. Results by Functional Area

### 4.1 Appointments

**Test objective:** verify appointment creation, retrieval, filtered listing, cancellation, rescheduling, slot availability, and notifications.

| Scenario | Result | Assessment |
|---|---|---|
| Create a valid appointment, reserve its slot, and send a notification | Passed | Positive flow behaves as expected |
| Reject a missing patient or dentist | Passed | Referential integrity is protected |
| Reject an unavailable slot | Passed | Invalid reservations are prevented |
| Create an appointment without email when the patient has no address | Passed | Optional notification degrades safely |
| Cancel an appointment and release its slot | Passed | State transition and related effect are correct |
| Reject cancellation of a missing appointment | Passed | No invalid side effects occur |
| Reschedule, update both slots, and notify | Passed | Positive rescheduling flow is correct |
| Reject an unavailable destination slot | Passed | Overlapping reservations are prevented |
| Retrieve an existing or missing appointment | Passed | Positive and negative paths are correct |
| List with filters and return an empty result when appropriate | Passed | Query behavior is correct |

**Feedback:** add concurrent reservation tests, boundary dates, time zones, terminal-state transitions, and transaction tests using real persistence.

### 4.2 Authentication and Password Recovery

**Test objective:** verify user registration, login, refresh token handling, logout, current-user retrieval, and password reset.

| Scenario | Result | Assessment |
|---|---|---|
| Create a valid user | Passed | Registration flow is correct |
| Reject a duplicated email or document number | Passed | Uniqueness is protected |
| Valid login creates a response and stores a refresh token | Passed | Authentication flow is correct |
| Reject an invalid email or password | Passed | Unauthorized access is denied |
| Reject a disabled user | Passed | Account state is respected |
| Invalidate previous reset codes, create a new code, and send email | Passed | Recovery request flow is correct |
| Do not process a missing or disabled user | Passed | Account enumeration is reduced and state is respected |
| Change the password and revoke sessions with a valid code | Passed | Password reset flow is correct |
| Reject an invalid or missing reset code | Passed | Reset validation is correct |
| Revoke refresh tokens on logout | Passed | Session termination is correct |
| Reject an unknown refresh token | Passed | Invalid token is controlled |
| Return an error for a missing current user | Passed | Missing identity is handled |

**Feedback:** add reset-code expiration and reuse, repeated attempts, password-policy cases, refresh-token rotation, token claims, and role-based authorization tests.

### 4.3 Patients

**Test objective:** verify patient creation, retrieval, listing, update, and logical deactivation.

| Scenario | Result | Assessment |
|---|---|---|
| Create a valid patient | Passed | Creation is correct |
| Reject a duplicate document during creation or update | Passed | Uniqueness is protected |
| Retrieve an existing or missing patient | Passed | Positive and negative paths are correct |
| List patients | Passed | Response mapping is correct |
| Deactivate a patient and save the change | Passed | Logical deletion is correct |

**Feedback:** add validation for every editable field, null and boundary values, search, pagination, and relationships with appointments, records, and invoices.

### 4.4 Procedures

**Test objective:** verify maintenance of the dental procedure catalog.

| Scenario | Result | Assessment |
|---|---|---|
| Create a valid procedure | Passed | Creation is correct |
| Reject a duplicate name during creation or update | Passed | Uniqueness is protected |
| Retrieve an existing or missing procedure | Passed | Positive and negative paths are correct |
| List procedures | Passed | Query behavior is correct |
| Deactivate a procedure | Passed | Logical deletion is correct |

**Feedback:** test negative prices, decimal precision, zero or extreme duration, name normalization, and references from records and invoices.

### 4.5 Schedules and Availability

**Test objective:** verify schedule configuration and copying, slot generation, and availability retrieval.

| Scenario | Result | Assessment |
|---|---|---|
| Copy a schedule to every dentist | Passed | Bulk-copy behavior is correct |
| Do not modify targets when the source schedule is missing | Passed | Negative path is safe |
| Skip existing slots and breaks during generation | Passed | Generation rule is correct |
| Retrieve and map available slots | Passed | Query behavior is correct |
| Reject a missing dentist | Passed | Invalid association is prevented |
| Return empty results when schedule or availability is absent | Passed | Missing data is handled |

**Feedback:** add midnight boundaries, non-divisible intervals, overlapping breaks, holidays, daylight-saving changes, reversed ranges, idempotency, and concurrency.

### 4.6 Medical Records

**Test objective:** verify medical-record creation and its coordination with appointments and invoicing, plus retrieval and update errors.

| Scenario | Result | Assessment |
|---|---|---|
| Create a record, update its appointment, and generate an invoice | Passed | Orchestration works with mocked dependencies |
| Reject an invalid patient | Passed | Logical integrity is protected |
| Reject a missing record during retrieval or update | Passed | Missing data is controlled |
| Reject a missing patient during history retrieval | Passed | Missing data is controlled |

**Feedback:** prove rollback across record, appointment, and invoice using real persistence. Add successful update, multiple procedures, and clinical field validation.

### 4.7 Invoicing and Payments

**Test objective:** verify invoice creation from a medical record, retrieval, update, payment registration, and email requests.

| Scenario | Result | Assessment |
|---|---|---|
| Create an invoice from a valid medical record | Passed | Positive flow is correct |
| Return an existing invoice without duplicating it | Passed | Basic idempotency is correct |
| Reject a missing medical record | Passed | Missing source is controlled |
| Register a payment and request a notification | Passed | Positive payment flow is correct |
| Reject payment for a missing invoice | Passed | Invalid payment is prevented |
| Reject a missing invoice during retrieval, update, or email sending | Passed | Negative paths are correct |
| Return an empty list when no invoices exist | Passed | Empty state is handled |

**Feedback:** cover partial payments, overpayments, zero or negative amounts, rounding, duplicate payments, state transitions, successful updates, real email delivery, and accounting consistency.

### 4.8 JWT Security

**Test objective:** verify token generation, validation, hashing, and authentication-context creation.

| Scenario | Result | Assessment |
|---|---|---|
| Generate and validate an access token | Passed | Basic token issuance works |
| Reject an invalid token | Passed | Negative validation works |
| Generate a UUID refresh token and SHA-256 hash | Passed | Format and storage protection are correct |
| Authenticate a valid token in the filter | Passed | Security context is established |
| Do not authenticate an invalid/missing token or disabled user | Passed | Access is not granted |

**Feedback:** add expired tokens, wrong signatures, modified claims, malformed headers, disallowed algorithms, issuer/audience validation, and authorization by endpoint and role.

### 4.9 HTTP Controllers

**Test objective:** verify routes and successful responses using MockMvc and mocked use cases.

| Controller | Verified operations | Cases | Result |
|---|---|---:|---|
| Appointments | Create (201), reschedule/retrieve/list (200), cancel (204) | 5 | Passed |
| Authentication | Login, refresh, current user, recovery (200), logout (204) | 6 | Passed |
| Invoicing | Create (201), pay/retrieve/list/update (200), email (204) | 6 | Passed |
| Medical records | Create (201), history/retrieve/update (200) | 4 | Passed |
| Patients | Create (201), list/retrieve/update (200), delete (204) | 5 | Passed |
| Procedures | Create (201), list/retrieve/update (200), delete (204) | 5 | Passed |
| Schedules | Dentists, copy, configure, retrieve, generate, and availability (200) | 6 | Passed |

**Feedback:** successful paths dominate. Add invalid payload (400), unauthenticated (401), insufficient role (403), missing resource (404), conflict (409), consistent error contracts, content types, boundary parameters, and CORS. No equivalent controller tests were identified for `UserController` or `HolidayController`.

### 4.10 Startup and Test Persistence

**Test objective:** verify that the complete context starts with the `test` profile, H2, JPA repositories, and reference-data initialization.

**Result:** passed. Spring discovered 13 JPA repositories and the initializer completed without failure.

**Feedback:** this test detects bean and basic mapping problems but does not replace repository tests or execution against PostgreSQL 16. Testcontainers is recommended.

## 5. Code Coverage

| JaCoCo metric | Covered | Total | Coverage |
|---|---:|---:|---:|
| Instructions | 7,443 | 12,145 | 61.28% |
| Lines | 1,851 | 3,104 | 59.63% |
| Branches | 168 | 426 | 39.44% |
| Methods | 550 | 988 | 55.67% |
| Complexity | 587 | 1,201 | 48.88% |

Line coverage is moderate and branch coverage is low. Passing every existing test does not mean complete functional coverage. No JaCoCo threshold was found that would fail the build when coverage decreases.

## 6. Findings and Improvement Plan

| Priority | Finding | Risk | Recommendation |
|---|---|---|---|
| High | No PostgreSQL integration tests | H2/SQL differences may surface in production | Add PostgreSQL 16 Testcontainers tests |
| High | Authorization and HTTP errors have limited coverage | Unauthorized access or inconsistent contracts | Build an endpoint-by-role matrix and 400/401/403/404/409 cases |
| High | Branch coverage is 39.44% | Relevant conditional paths remain untested | Prioritize business rules and introduce a gradual coverage threshold |
| Medium | No concurrency or transaction tests | Double booking/payment or partial state | Test locking, idempotency, and rollback |
| Medium | Email dependencies are mocked | Integration and delivery are not verified | Use a test SMTP server and verify content and retries |
| Medium | No performance tests | Capacity and response times are unknown | Define SLOs and load-test critical endpoints |
| Medium | Mockito self-attaches its agent | A future Java release may prevent execution | Configure Mockito as a build agent |
| Low | `H2Dialect` is explicitly configured | Deprecated, redundant configuration warning | Allow dialect auto-detection |
| Low | Initializer text displayed an encoding issue | Logs and evidence are harder to read | Standardize files and console output as UTF-8 |

## 7. Exit Criteria and Recommendation

The reviewed suite **meets** its technical exit criterion: 112 of 112 tests passed and the build completed successfully. For a production release, the quality recommendation is **conditional approval** because end-to-end coverage, PostgreSQL/email integration, and systematic role and concurrency verification are still missing.

Before release, the following actions are recommended:

1. Run an end-to-end smoke flow covering login, patient, appointment, medical record, invoice, and payment.
2. Validate every endpoint with ADMIN, SECRETARY_ASSISTANT, and DENTIST roles.
3. Add integration tests with PostgreSQL 16 and a controlled email server.
4. Link test evidence and defects through traceable identifiers.
5. Add the critical missing scenarios and introduce a progressive coverage threshold.

## 8. Reproducible Evidence

- Tests: `backend/odontosys-api/src/test/java`.
- Test profile: `backend/odontosys-api/src/test/resources/application-test.properties`.
- Surefire results: `backend/odontosys-api/target/surefire-reports` after running Maven.
- Coverage report: `backend/odontosys-api/target/site/jacoco/index.html` after running Maven.
- Verified command: `.\mvnw.cmd test`.

This report represents the indicated version. Every code change requires rerunning the suite and updating the date, metrics, findings, and evidence.

## 9. Detailed Test Specifications

In unit tests, “repository” and “email service” refer to Mockito test doubles. A passed result confirms service decisions and expected interactions; it does not prove a real database write or email delivery.

### 9.1 Appointment Cancellation — `CancelAppointmentServiceTest`

**Common setup:** the appointment and slot repositories are mocked. The service receives the appointment identifier.

- **Existing appointment:** the repository returns the appointment and its slot. Cancellation must change the appointment status, release the slot, and submit both changes to their repositories. All effects were verified. **Passed**.
- **Missing appointment:** the repository returns empty. `AppointmentNotFoundException` must be raised and processing must stop. The expected exception was observed. **Passed**.

### 9.2 Appointment Creation — `CreateAppointmentServiceTest`

**Common setup:** patient, user/dentist, availability, appointment, and email ports are mocked.

- **Valid command:** the patient, dentist, and available slot exist. The service must save the appointment, occupy the slot, and request a confirmation email. Response and interactions matched expectations. **Passed**.
- **Patient without email:** clinical data is valid, but no email address exists. The appointment must be created without invoking email delivery. **Passed**.
- **Unavailable slot:** `SlotNotAvailableException` must occur before creating the appointment. **Passed**.
- **Missing dentist:** `UserNotFoundException` must stop the flow. **Passed**.
- **Missing patient:** `PatientNotFoundException` must stop slot reservation. **Passed**.

### 9.3 Appointment Retrieval and Listing

| Class and case | Action | Expected and observed result | Status |
|---|---|---|---|
| `GetAppointmentByIdServiceTest` — existing ID | Repository returns an appointment | A mapped response is returned | Passed |
| `GetAppointmentByIdServiceTest` — missing ID | Repository returns empty | Domain exception is raised | Passed |
| `ListAppointmentsServiceTest` — filters | Matching records are returned | Records are mapped correctly | Passed |
| `ListAppointmentsServiceTest` — no matches | Repository returns no records | Empty list, not `null` or an error | Passed |

### 9.4 Appointment Rescheduling — `RescheduleAppointmentServiceTest`

- **Valid destination slot:** an existing appointment and free destination slot are prepared. The old slot must be released, the new slot occupied, the appointment updated, and a notification requested. The expected interactions were verified. **Passed**.
- **Occupied destination slot:** `SlotNotAvailableException` must be raised and invalid rescheduling prevented. **Passed**.

### 9.5 User Creation — `CreateUserServiceTest`

- **Valid data:** email and document are available, the password is encoded, and the user is saved. The expected response is returned. **Passed**.
- **Existing email:** a duplicate is simulated. The service raises the expected duplication exception and does not complete registration. **Passed**.
- **Existing document:** a duplicate is simulated and rejected. **Passed**.

### 9.6 Login — `LoginServiceTest`

- **Valid credentials:** the user exists, is enabled, and the password matches. Access and refresh tokens must be produced, the refresh-token hash stored, and an authentication response returned. **Passed**.
- **Invalid email:** the user is absent and `InvalidCredentialsException` is expected. **Passed**.
- **Invalid password:** password verification fails and `InvalidCredentialsException` is expected. **Passed**.
- **Disabled account:** `UserDisabledException` must be raised and no session issued. **Passed**.

### 9.7 Password-Recovery Request — `RequestPasswordResetServiceTest`

- **Active user:** old tokens are invalidated, a new code is created, and an email is requested. All three interactions were verified. **Passed**.
- **Missing user:** processing finishes without a visible error or email, avoiding account disclosure. **Passed**.
- **Disabled user:** no code or email is produced. **Passed**.

### 9.8 Password Reset — `ResetPasswordServiceTest`

- **Valid code:** a usable token is found; the new password is encoded and saved, the code is consumed, and sessions are revoked. The simulated workflow was verified. **Passed**.
- **Invalid code:** `InvalidResetCodeException` is expected. **Passed**.
- **Missing token:** no reset record exists and the same controlled exception is expected. **Passed**.

### 9.9 Invoice Creation — `CreateInvoiceServiceTest`

- **Valid medical record:** the record and procedure details are retrieved, and an invoice is built and saved. **Passed**.
- **Existing invoice:** the existing invoice is returned without creating a duplicate. **Passed**.
- **Missing medical record:** the domain exception is raised and no invoice is created. **Passed**.

### 9.10 Payment Registration — `RegisterPaymentServiceTest`

- **Valid payment:** the invoice is found, payment is registered, the applicable financial state is updated, and a notification is requested. **Passed**.
- **Missing invoice:** the relevant exception is raised and no payment is recorded. **Passed**.

### 9.11 Medical-Record Creation — `CreateMedicalRecordServiceTest`

- **Valid command:** the patient is verified, the record is created, the related appointment is updated, and invoice generation is requested. All collaborations were verified. **Passed**.
- **Invalid patient:** `PatientNotFoundException` is raised and the operation stops. **Passed**.

### 9.12 Patient Application Tests

| Case | Verified behavior | Status |
|---|---|---|
| Create valid patient | Saves the aggregate and returns its data | Passed |
| Duplicate document on creation | Raises `PatientAlreadyExistsException` | Passed |
| Retrieve existing patient | Maps entity to response | Passed |
| Retrieve missing patient | Raises `PatientNotFoundException` | Passed |
| List patients | Maps all returned items | Passed |
| Delete patient | Deactivates and saves the aggregate | Passed |
| Duplicate document on update | Rejects the change | Passed |

### 9.13 Procedure Application Tests

| Case | Verified behavior | Status |
|---|---|---|
| Create valid procedure | Saves and returns the procedure | Passed |
| Duplicate name on creation | Raises `ProcedureAlreadyExistsException` | Passed |
| Retrieve existing procedure | Maps entity to response | Passed |
| Retrieve missing procedure | Raises `ProcedureNotFoundException` | Passed |
| List procedures | Maps the returned collection | Passed |
| Delete procedure | Deactivates and saves the aggregate | Passed |
| Duplicate name on update | Rejects the change | Passed |

### 9.14 Schedule Services — `ScheduleServicesTest`

- **Copy to all dentists:** a source configuration replaces each target schedule. Repository operations for the targets were verified. **Passed**.
- **Copy without source:** no source exists, so target schedules must remain unchanged. **Passed**.
- **Generate slots:** intervals are produced from the schedule while breaks and existing slots are skipped. **Passed**.
- **Retrieve availability:** repository objects are converted to response objects. **Passed**.

### 9.15 Complementary Services — `RemainingApplicationServicesTest`

This class contains fifteen edge cases that complete paths not covered by specialized files:

| Area | Verified case | Observed result |
|---|---|---|
| Authentication | Missing current user | Expected exception; passed |
| Authentication | Logout | All refresh tokens revoked; passed |
| Authentication | Unknown refresh token | Token rejected; passed |
| Invoicing | Retrieve missing invoice | Expected exception; passed |
| Invoicing | Empty repository listing | Empty list; passed |
| Invoicing | Email missing invoice | Expected exception; passed |
| Invoicing | Update missing invoice | Expected exception; passed |
| Medical records | Retrieve missing record | Expected exception; passed |
| Medical records | Missing patient history | Expected exception; passed |
| Medical records | Update missing record | Expected exception; passed |
| Schedules | Configure missing dentist | Expected exception; passed |
| Schedules | Retrieve absent schedule | Empty response; passed |
| Schedules | Retrieve absent availability | Empty list; passed |
| Schedules | Generate without schedule | Empty list; passed |
| Schedules | Copy without source configuration | Empty list; passed |

### 9.16 HTTP Controllers — Detailed Interpretation

The 37 MockMvc tests prepare use-case responses, invoke HTTP routes, and verify status codes and, where applicable, serialized fields. They do not access real repositories.

- **Appointments (5):** creation returns 201; retrieval, listing, and rescheduling return 200; cancellation returns 204. **All passed**.
- **Authentication (6):** login, refresh, current user, password request, and password reset return 200; logout returns 204. **All passed**.
- **Invoicing (6):** creation returns 201; payment, retrieval, listing, and update return 200; email request returns 204. **All passed**.
- **Medical records (4):** creation returns 201; retrieval, history, and update return 200. **All passed**.
- **Patients (5):** creation returns 201; listing, retrieval, and update return 200; deletion returns 204. **All passed**.
- **Procedures (5):** creation returns 201; listing, retrieval, and update return 200; deletion returns 204. **All passed**.
- **Schedules (6):** dentist listing, copying, configuration, retrieval, generation, and availability return 200. **All passed**.

**Quality interpretation:** these tests confirm the basic successful HTTP contract. Because security filters are disabled or isolated at this test level, they do not establish that every route is correctly protected in production.

### 9.17 Authentication Filter — `JwtAuthenticationFilterTest`

- **Valid token:** the provider validates the token and the user is enabled; authentication must be set in the security context. **Passed**.
- **Invalid token:** authentication must not be established and the chain continues anonymously. **Passed**.
- **No token:** the filter must not create an identity and must continue the chain. **Passed**.
- **Disabled user:** the account must not be authenticated even when the token can be processed. **Passed**.

### 9.18 JWT Provider — `JwtTokenProviderTest`

- **Access token:** a generated token is considered valid by the provider. **Passed**.
- **Invalid string:** validation returns `false`. **Passed**.
- **Refresh token:** the generated value can be parsed as a UUID. **Passed**.
- **Hash:** the refresh token produces a SHA-256 representation suitable for storage. **Passed**.

### 9.19 Application Startup — `OdontosysApiApplicationTests`

**Context:** the `test` profile is activated, Spring starts, H2 connects in memory, 13 JPA repositories are registered, and reference-data initialization runs.

**Expected result:** the context must build without configuration, dependency-injection, entity, or repository exceptions. **Observed result:** startup completed and the test finished without failure. **Status: passed**.

## 10. How to Interpret a Passed Test

A passed test means that, for its prepared input and mocked dependencies, the observed behavior matched the programmed assertions. It does not automatically mean that:

- every possible value was tested;
- the production database was accessed;
- an email was delivered;
- complete authorization was validated;
- the flow was exercised from the frontend;
- no defects exist outside the executed paths.

This distinction keeps the report technically accurate and prevents the current suite's scope from being overstated.
