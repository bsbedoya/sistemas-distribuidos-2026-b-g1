<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       03-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 03

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Brayan Smith Bedoya
- GITHUB_USER: bsbedoya
- TEAM: OdontoSys
- SPRINT_GOAL: Define and document the Appointment Scheduling bounded context for OdontoSys, including its data model, persistence strategy, domain concepts, service responsibilities, and the distributed systems concepts studied during Week 03.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-APT-001 | Document the Appointment Scheduling bounded context and appointment-service responsibilities | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/f848d01c471c75f83ae21fea855c4d17889ae51f |
| DOC-APT-002 | Define the appointment-service data model and persistence strategy | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/1be370c3dedae0510549ad7f7d764a283109c0ee |
| DOC-CLASS-001 | Document Week 03 Distributed Systems Session 1 through a summary diagram | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/84741618eda09e3baf335b09ac1fe89b4617dee1 |
| DOC-CLASS-002 | Document Week 03 Distributed Systems Session 2 through a summary diagram | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/94035bbea84c075fd33cdf394d13d826fc4ba2b1 |

## 2. My individual contribution

During Week 03, I worked mainly on the **Appointment Scheduling** bounded context and the documentation of the `appointment-service` for the **OdontoSys** distributed system.

### Appointment Scheduling bounded context

- I documented the **Appointment Scheduling** bounded context and the responsibilities of the `appointment-service`.
- I defined the service as responsible for managing the dental appointment agenda.
- I documented the main appointment operations:
  - Creating appointments.
  - Checking appointment availability.
  - Rescheduling appointments.
  - Cancelling appointments.
  - Confirming attendance.
  - Changing appointment statuses.
  - Managing dentist schedules.
  - Managing availability slots.
  - Maintaining the history of appointment status changes.
- I reviewed the relationship between the Appointment Scheduling context and other OdontoSys services.
- I established that patients and dentists are referenced through UUID identifiers instead of direct foreign keys to external microservice databases.

### Appointment Service data model

- I defined **PostgreSQL 15+** as the persistence engine and source of truth for the `appointment-service`.
- I documented four main tables:
  - `appointments`
  - `dentists_schedule`
  - `availability_slots`
  - `appointment_status_history`
- I documented the fields, data types, constraints, relationships, indexes, and SQL schema associated with these tables.
- I defined the valid appointment states:
  - `PROGRAMADA`
  - `CONFIRMADA`
  - `EN_ATENCION`
  - `FINALIZADA`
  - `CANCELADA`
  - `NO_ASISTIO`
- I defined the availability slot states:
  - `DISPONIBLE`
  - `RESERVADO`
  - `BLOQUEADO`
- I added a `UNIQUE` constraint to the appointment `slot_id` reference to prevent two appointments from reserving the same availability slot.
- I documented the relationship between schedules, availability slots, appointments, and status history.
- I documented indexes for the most common queries, including appointment queries by patient, dentist, date, slot, and status history.
- I defined **Flyway** as the database migration strategy using versioned and forward-only migrations.
- I documented that the `appointment-service` owns its database exclusively and that other microservices must communicate with it through APIs or domain events.

### Domain documentation

- I documented the Appointment Scheduling domain events associated with the appointment lifecycle.
- The documented events include:
  - `AppointmentScheduled`
  - `AppointmentConfirmed`
  - `AppointmentRescheduled`
  - `AppointmentAttentionStarted`
  - `AppointmentCompleted`
  - `AppointmentCancelled`
  - `AppointmentNoShow`
- I added Appointment Scheduling concepts to the shared glossary, including:
  - Appointment
  - Appointment Status
  - Dentist Schedule
  - Availability Slot
  - Double Booking
  - No-Show
  - Appointment Service
  - Appointment Scheduling
- I identified an inconsistency between the project documentation that referenced `Prisma Migrate` and the Spring Boot appointment-service, which uses **Flyway**.

### Week 03 class documentation

- I documented **Distributed Systems Session 1** through a visual summary focused on distributed applications.
- I reviewed how distributed applications separate client-side and server-side responsibilities and communicate through a network.
- I documented important characteristics of distributed applications such as network communication, component independence, scalability, fault tolerance, and concurrency.
- I documented **Distributed Systems Session 2** through a visual summary focused on consistency models and the CAP theorem.
- I reviewed strong consistency, eventual consistency, causal consistency, read-your-writes, monotonic reads, and monotonic writes.
- I studied the trade-offs between Consistency, Availability, and Partition Tolerance in distributed systems.
- I reviewed the CP, AP, and CA combinations and how the appropriate choice depends on application requirements.

### Main work delivered

- **Appointment Service work summary:** documented the Appointment Scheduling bounded context, domain events, glossary updates, and service responsibilities.
- **Appointment Service data model:** documented the PostgreSQL schema, relationships, constraints, indexes, migration strategy, and data ownership rules.
- **Week 03 Session 1 diagram:** documented distributed application concepts.
- **Week 03 Session 2 diagram:** documented consistency models and the CAP theorem.

## 3. Blockers and risks

- The communication contracts between `appointment-service` and the other OdontoSys microservices still need to be formally defined.
- The project must confirm which services need to consume appointment domain events such as `AppointmentCancelled` and `AppointmentNoShow`.
- The inconsistency between `Prisma Migrate` and `Flyway` in the shared project documentation needs to be resolved.
- Business rules for dentist availability and appointment scheduling must remain consistent with the database constraints.
- The service must prevent double booking at both the application and persistence levels.
- Patient and dentist identifiers belong to other bounded contexts, so direct database relationships between services must be avoided.
- The final consistency requirements between Appointment Scheduling and the other services still need to be validated during implementation.

## 4. Plan for next week

- Continue refining the `appointment-service` domain model and business rules.
- Define the REST API contract for creating, consulting, rescheduling, and cancelling appointments.
- Define the communication contracts between Appointment Scheduling and the Patient, Auth, Clinical, and Billing services.
- Validate the appointment lifecycle and allowed status transitions.
- Define how appointment domain events will be published and consumed.
- Translate the documented PostgreSQL model into Flyway migration scripts.
- Start implementing the `appointment-service` based on the documented domain and persistence model.
- Add unit and integration tests for scheduling, availability, status changes, and double-booking prevention.
- Keep the domain, architecture, data model, and implementation documentation synchronized.

## 5. Compliance self-check

- [x] Conventional Commits used for my documented contributions.
- [ ] Per-environment HU branch + PR to the corresponding environment.
- [x] Domain and service responsibilities documented.
- [x] Data model and persistence constraints documented.
- [ ] Tests added/updated (unit / integration).
- [x] Bounded-context and microservice data ownership principles respected.
- [x] No shared database access between microservices.
- [x] No secrets added to the documentation.
- [x] Database migrations documented through Flyway.

### Notes on the unchecked items

- This week's work was mainly focused on domain documentation, distributed systems concepts, and the Appointment Service data model.
- No production implementation or automated testing was part of the documented work for this week.
- Per-environment HU branches and pull requests were not included as evidence in the Week 03 work.
- Unit and integration tests will become applicable when the documented Appointment Service model is implemented.

## 6. Evidence links

- [Commit — docs: appointment-service-work-summary](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/f848d01c471c75f83ae21fea855c4d17889ae51f)
- [Commit — docs: Progress on Folder 06-data – Data models by service](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/1be370c3dedae0510549ad7f7d764a283109c0ee)
- [Commit — docs: Distributed Systems session 1](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/84741618eda09e3baf335b09ac1fe89b4617dee1)
- [Commit — docs: Distributed Systems session 2](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/94035bbea84c075fd33cdf394d13d826fc4ba2b1)
- Week 03 commits: https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commits/main/03-week