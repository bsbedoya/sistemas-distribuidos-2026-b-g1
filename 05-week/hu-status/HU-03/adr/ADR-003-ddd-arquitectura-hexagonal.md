# ADR-003: Use domain models, use-case ports and adapters with explicit boundary limitations

- Status: Recorded architectural direction; partially implemented.
- Date: 2026-09-06 (audit date).
- Scope: Backend of MVP 1 / Cut 1 at `7a44eb8`.

## Context

[architecture.md](../../HU-01/architecture.md) appropriately describes a separation “close to” hexagonal architecture. A stronger claim of complete DDD or strict hexagonal conformance would not match the dependencies or the implemented invariants.

The audit examined behavior, imports, composition, repositories and transaction boundaries, rather than treating folder names as proof.

## Decision

Retain domain objects, inbound use-case interfaces, outbound ports and infrastructure adapters as the architectural direction. Classify the current implementation as **DDD-inspired and partially hexagonal**, with known exceptions. Do not describe all aggregates as complete consistency boundaries or all capabilities as isolated bounded contexts.

## Evidence in the project

Paths are relative to [the Java root](../../backend/odontosys-api/src/main/java/com/odontosys/odontosys_api).

| Evidence | Architectural interpretation |
|---|---|
| `domain/model/Appointment.java` | Factory, state and behavior; cancellation/rescheduling reject completed or cancelled appointments. This is actual domain behavior. |
| `domain/model/DentistSchedule.java` | Validates time order, break order and slot duration from 1 to 240 minutes. |
| `domain/model/MedicalRecord.java`, `Invoice.java` | Own collections and calculate totals with BigDecimal. Applied procedure names/prices are snapshots. |
| `domain/port/out/*` | Repository, password encoder, token and email abstractions isolate many application services from concrete infrastructure. |
| `application/*/*Service.java` | Most implement inbound interfaces and coordinate outbound ports. |
| `infrastructure/config/AppConfig.java` | Explicit dependency composition supplies concrete adapters through ports. |
| `infrastructure/adapter/out/persistence/mapper/*` | Converts separate domain models and JPA entities; controllers generally use request/response DTOs. |
| `domain/port/in/*` | Imports commands/responses from `application`, while application imports domain: the package dependency is bidirectional, although the Java build permits it. |
| `application/schedule/CopyDentistScheduleService.java` | Spring `@Service` and transaction dependency, no inbound use-case interface; the controller injects the concrete service. |
| `infrastructure/adapter/in/web/controller/HolidayController.java` | Direct JPA repository access and JPA entity responses bypass the domain/use-case boundary. |
| `UserController.java`, `ScheduleController.java` | Some operations access outbound repositories from the web layer instead of going through use cases. |

Domain models themselves do not import Spring/JPA. This positive separation does not remove the inbound-port dependency cycle.

## Alternatives considered

- **Direct controller-to-JPA application:** simpler CRUD plumbing, but reduces isolation of business behavior. The holiday path currently follows this pattern.
- **Conventional layered services:** could describe parts of the current implementation without claiming ports/adapters everywhere.
- **Strict hexagonal boundaries:** moving application contracts out of domain-owned interfaces and routing remaining operations through use cases would strengthen conformance. Such refactoring is future work, not part of this documentation change.
- These comparisons explain the tradeoff; no new architecture approval or historical deliberation is invented.

## Consequences

### Positive

- Domain factories and behavior can be exercised without a running server.
- Repository and email ports allow Mockito-based unit tests.
- Separate persistence mappings avoid annotating core models with JPA.

### Negative

- Some declared aggregate rules are incomplete: overpayment is allowed, practitioner/slot identity is not checked, and several state changes are unrestricted.
- Most critical multi-repository workflows lack an encompassing transaction.
- Authorization mostly lives in controllers; use cases do not receive an authenticated actor for ownership checks.
- Interfaces and mapping layers add maintenance, and stale tests already refer to a nonexistent copy-schedule interface.
- Multiple modules share repository types and concrete concepts, making future extraction nontrivial.

## Implementation status

**Partially implemented.** Concrete invariants, factories, ports, adapters and composition exist. Strict dependency direction, context isolation, consistent use-case boundaries and aggregate transaction guarantees do not.

Examples requiring release review:

- `CancelAppointmentService` releases a slot before `Appointment.cancel` validates the terminal state.
- `RescheduleAppointmentService` changes both slots before the domain rejects a completed/cancelled appointment.
- `RegisterPaymentService` saves a payment before `Invoice.addPaymentAmount` rejects a paid/cancelled invoice.
- `CreateMedicalRecordService` changes an appointment and saves a record separately, then suppresses invoice exceptions.
- `UpdateMedicalRecordService` replaces current detail data without an immutable amendment history or invoice reconciliation.

These ordering and boundary findings are verified in source. Their database effects under failure/concurrency are **NO VERIFICADO** because PostgreSQL was not run.

## Scope

This ADR documents architectural fidelity and limitations. It does not certify DDD, repair source code, or claim passing tests. Detailed feature and security findings are in [CHANGELOG.md](../../CHANGELOG.md).

