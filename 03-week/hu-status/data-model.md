# Data Models per Service

> **Service:** appointment-service
> **Bounded Context:** Appointment Scheduling
> **DB Engine:** PostgreSQL 15+
> **Owner:** `appointment-service`
> **Responsible:** Bryan Smith Bedoya

---

## Service: appointment-service

**Bounded Context:** Appointment Scheduling

**DB Engine:** PostgreSQL 15+

**Owner:** `appointment-service`

**Responsible:** Bryan Smith Bedoya

### Responsibility

The `appointment-service` is responsible for managing the dental appointment agenda.

Its main responsibilities include:

- Creating appointments.
- Checking appointment availability.
- Rescheduling appointments.
- Cancelling appointments.
- Confirming attendance.
- Changing appointment statuses.
- Managing dentist schedules.
- Managing available appointment slots.
- Keeping a history of appointment status changes.

### Justification

PostgreSQL is used because appointment scheduling requires transactional consistency,
relational integrity, constraints, and reliable persistence.

The service owns its own database and does not directly access tables from other
microservices.

External entities such as patients and dentists are referenced only through their
UUID identifiers.

---

# Table: appointments

The `appointments` table stores the main information about dental appointments.

| Field | Type | Nullable | Description | Constraints |
|---|---|---|---|---|
| id | UUID | No | Appointment identifier | PK |
| patient_id | UUID | No | Identifier of the patient | External service identifier |
| dentist_id | UUID | No | Identifier of the dentist | External service identifier |
| slot_id | UUID | No | Reserved availability slot | FK availability_slots.id, UNIQUE |
| appointment_date | DATE | No | Date of the appointment | NOT NULL |
| reason | TEXT | Yes | Reason for the appointment | — |
| status | VARCHAR(30) | No | Current appointment status | CHECK allowed values |
| notes | TEXT | Yes | Additional appointment notes | — |
| confirmed_at | TIMESTAMPTZ | Yes | Date and time when the appointment was confirmed | — |
| cancelled_at | TIMESTAMPTZ | Yes | Date and time when the appointment was cancelled | — |
| created_at | TIMESTAMPTZ | No | Appointment creation timestamp | DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | No | Last update timestamp | DEFAULT NOW() |

### Appointment statuses

```text
PROGRAMADA
CONFIRMADA
EN_ATENCION
FINALIZADA
CANCELADA
NO_ASISTIO
```

---

# Table: dentists_schedule

The `dentists_schedule` table stores the working schedule of each dentist.

| Field | Type | Nullable | Description | Constraints |
|---|---|---|---|---|
| id | UUID | No | Schedule identifier | PK |
| dentist_id | UUID | No | Identifier of the dentist | External service identifier |
| day_of_week | VARCHAR(15) | No | Day of the week | NOT NULL |
| start_time | TIME | No | Workday start time | NOT NULL |
| end_time | TIME | No | Workday end time | NOT NULL |
| active | BOOLEAN | No | Indicates whether the schedule is active | DEFAULT true |
| created_at | TIMESTAMPTZ | No | Schedule creation timestamp | DEFAULT NOW() |
| updated_at | TIMESTAMPTZ | No | Last update timestamp | DEFAULT NOW() |

---

# Table: availability_slots

The `availability_slots` table stores the individual time slots available
for dental appointments.

| Field | Type | Nullable | Description | Constraints |
|---|---|---|---|---|
| id | UUID | No | Availability slot identifier | PK |
| schedule_id | UUID | No | Dentist schedule identifier | FK dentists_schedule.id |
| date | DATE | No | Date of the availability slot | NOT NULL |
| start_time | TIME | No | Slot start time | NOT NULL |
| end_time | TIME | No | Slot end time | NOT NULL |
| status | VARCHAR(20) | No | Current slot status | CHECK allowed values |
| created_at | TIMESTAMPTZ | No | Slot creation timestamp | DEFAULT NOW() |

### Slot statuses

```text
DISPONIBLE
RESERVADO
BLOQUEADO
```

---

# Table: appointment_status_history

The `appointment_status_history` table stores every status change made
to an appointment.

| Field | Type | Nullable | Description | Constraints |
|---|---|---|---|---|
| id | UUID | No | History record identifier | PK |
| appointment_id | UUID | No | Appointment identifier | FK appointments.id |
| previous_status | VARCHAR(30) | Yes | Previous appointment status | — |
| new_status | VARCHAR(30) | No | New appointment status | NOT NULL |
| changed_by | VARCHAR(255) | Yes | User who changed the status | Audit value |
| changed_at | TIMESTAMPTZ | No | Date and time of the status change | DEFAULT NOW() |

---

## Data Dictionary

### appointments

| Column | Type | Description | Example |
|---|---|---|---|
| id | UUID | Unique identifier for the appointment | 550e8400-e29b-41d4-a716-446655440000 |
| patient_id | UUID | Identifier of the patient from the patient service | 660e8400-e29b-41d4-a716-446655440000 |
| dentist_id | UUID | Identifier of the dentist from the dentist service | 770e8400-e29b-41d4-a716-446655440000 |
| slot_id | UUID | Identifier of the reserved availability slot | 880e8400-e29b-41d4-a716-446655440000 |
| appointment_date | DATE | Date scheduled for the appointment | 2026-09-15 |
| reason | TEXT | Reason for the dental appointment | Dental cleaning |
| status | VARCHAR(30) | Current status of the appointment | PROGRAMADA |
| notes | TEXT | Additional information about the appointment | Patient requested morning appointment |
| confirmed_at | TIMESTAMPTZ | Date and time when the appointment was confirmed | 2026-09-14T10:30:00Z |
| cancelled_at | TIMESTAMPTZ | Date and time when the appointment was cancelled | NULL |
| created_at | TIMESTAMPTZ | Date and time when the appointment was created | 2026-09-10T14:30:00Z |
| updated_at | TIMESTAMPTZ | Date and time of the last modification | 2026-09-10T14:30:00Z |

### dentists_schedule

| Column | Type | Description | Example |
|---|---|---|---|
| id | UUID | Unique identifier for the dentist schedule | 550e8400-e29b-41d4-a716-446655440000 |
| dentist_id | UUID | Identifier of the dentist | 660e8400-e29b-41d4-a716-446655440000 |
| day_of_week | VARCHAR(15) | Day of the week for the schedule | MONDAY |
| start_time | TIME | Start time of the working schedule | 08:00:00 |
| end_time | TIME | End time of the working schedule | 17:00:00 |
| active | BOOLEAN | Indicates whether the schedule is active | true |
| created_at | TIMESTAMPTZ | Date and time when the schedule was created | 2026-08-23T08:00:00Z |
| updated_at | TIMESTAMPTZ | Date and time of the last modification | 2026-08-23T08:00:00Z |

### availability_slots

| Column | Type | Description | Example |
|---|---|---|---|
| id | UUID | Unique identifier for the availability slot | 550e8400-e29b-41d4-a716-446655440000 |
| schedule_id | UUID | Identifier of the dentist schedule | 660e8400-e29b-41d4-a716-446655440000 |
| date | DATE | Date of the availability slot | 2026-09-15 |
| start_time | TIME | Start time of the slot | 08:00:00 |
| end_time | TIME | End time of the slot | 08:30:00 |
| status | VARCHAR(20) | Current status of the slot | DISPONIBLE |
| created_at | TIMESTAMPTZ | Date and time when the slot was created | 2026-08-23T08:00:00Z |

### appointment_status_history

| Column | Type | Description | Example |
|---|---|---|---|
| id | UUID | Unique identifier for the history record | 550e8400-e29b-41d4-a716-446655440000 |
| appointment_id | UUID | Identifier of the appointment | 660e8400-e29b-41d4-a716-446655440000 |
| previous_status | VARCHAR(30) | Previous appointment status | PROGRAMADA |
| new_status | VARCHAR(30) | New appointment status | CONFIRMADA |
| changed_by | VARCHAR(255) | User who changed the appointment status | admin@example.com |
| changed_at | TIMESTAMPTZ | Date and time when the status changed | 2026-09-14T10:30:00Z |

---

## SQL Schema

### Table: dentists_schedule

```sql
CREATE TABLE dentists_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dentist_id UUID NOT NULL,
    day_of_week VARCHAR(15) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Table: availability_slots

```sql
CREATE TABLE availability_slots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_id UUID NOT NULL
        REFERENCES dentists_schedule(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (
        status IN (
            'DISPONIBLE',
            'RESERVADO',
            'BLOQUEADO'
        )
    ),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Table: appointments

> ⚠️ **Corrección aplicada:** `slot_id` incluye la restricción `UNIQUE` para
> impedir que un mismo `availability_slot` sea asignado a más de una cita
> (doble reserva).

```sql
CREATE TABLE appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    dentist_id UUID NOT NULL,
    slot_id UUID NOT NULL UNIQUE
        REFERENCES availability_slots(id) ON DELETE RESTRICT,
    appointment_date DATE NOT NULL,
    reason TEXT,
    status VARCHAR(30) NOT NULL CHECK (
        status IN (
            'PROGRAMADA',
            'CONFIRMADA',
            'EN_ATENCION',
            'FINALIZADA',
            'CANCELADA',
            'NO_ASISTIO'
        )
    ),
    notes TEXT,
    confirmed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Table: appointment_status_history

```sql
CREATE TABLE appointment_status_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id UUID NOT NULL
        REFERENCES appointments(id) ON DELETE CASCADE,
    previous_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(255),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## Indexes

Indexes are used to improve the performance of the most common
appointment and schedule queries.

| Name | Fields | Type | Justification |
|---|---|---|---|
| idx_appointments_patient | appointments(patient_id) | B-tree | Finds appointments belonging to a patient |
| idx_appointments_dentist_date | appointments(dentist_id, appointment_date) | B-tree | Retrieves a dentist's daily agenda |
| idx_appointments_slot | appointments(slot_id) | B-tree | Finds the appointment associated with a slot |
| idx_schedule_dentist | dentists_schedule(dentist_id) | B-tree | Finds schedules belonging to a dentist |
| idx_slots_schedule_date | availability_slots(schedule_id, date) | B-tree | Finds available slots for a schedule and date |
| idx_slots_status | availability_slots(status) | B-tree | Finds slots by availability status |
| idx_history_appointment | appointment_status_history(appointment_id) | B-tree | Retrieves the status history of an appointment |

### SQL Indexes

```sql
CREATE INDEX idx_appointments_patient
ON appointments(patient_id);

CREATE INDEX idx_appointments_dentist_date
ON appointments(dentist_id, appointment_date);

CREATE INDEX idx_appointments_slot
ON appointments(slot_id);

CREATE INDEX idx_schedule_dentist
ON dentists_schedule(dentist_id);

CREATE INDEX idx_slots_schedule_date
ON availability_slots(schedule_id, date);

CREATE INDEX idx_slots_status
ON availability_slots(status);

CREATE INDEX idx_history_appointment
ON appointment_status_history(appointment_id);
```

---

## Modeling Decisions

- `appointment-service` is the only source of truth for dental appointments,
  schedules, availability slots, and appointment status history.
- `patient_id` and `dentist_id` reference entities owned by other
  microservices. They are stored as UUID values and do not use foreign keys
  to tables in this database.
- `appointments` stores the current status of each appointment.
- `appointment_status_history` stores every status change to provide
  traceability and auditability.
- `dentists_schedule` stores the regular working schedule of each dentist.
- `availability_slots` represents the individual time slots that can be
  available, reserved, or blocked.
- PostgreSQL constraints are used to prevent invalid appointment and
  availability status values.
- Each appointment references one availability slot, and `slot_id` is
  `UNIQUE` to guarantee that a slot cannot be reserved by more than one
  appointment (prevents double booking).
- The database is independent from the databases of the other microservices.
  No microservice accesses these tables directly.

---

## Migration Strategy

**Tool:** Flyway

The database schema is managed using versioned migrations.

### File naming convention

```text
db/migration/
├── V1__create_dentists_schedule.sql
├── V2__create_availability_slots.sql
├── V3__create_appointments.sql
└── V4__create_appointment_status_history.sql
```

### Migration rules

✓ Migrations are always forward-only.

✓ One migration should represent one logical schema change.

✓ Migrations must be reviewed before being applied to shared or production environments.

✓ Seed data must be handled separately from structural schema migrations.

✗ Never modify a migration that has already been executed.

✗ Never manually modify production tables outside the migration process.

✗ Destructive schema changes must be performed using a controlled migration strategy.

---

## DB Engine Selection

| Engine | Use When | Avoid When |
|---|---|---|
| PostgreSQL | ACID transactions, relational data, foreign keys, constraints, and consistent appointment data | Highly unstructured document-oriented data |
| MongoDB | Flexible document-oriented data models | Strong relational consistency is required |
| Redis | Temporary state, caching, counters, and short-lived data | Source of truth or critical persistent business data |

### appointment-service Engine Decision

`appointment-service` uses PostgreSQL as the source of truth because
appointment scheduling requires transactional consistency, relational
integrity, constraints, and reliable persistence.

PostgreSQL allows the service to maintain relationships between schedules,
availability slots, appointments, and appointment status history.

The service owns its database and communicates with other microservices
through APIs or events instead of sharing database tables.

---

## Relationship Diagram

The following entity-relationship diagram represents the persistent data
model owned by `appointment-service`.

### Relationships

```text
dentists_schedule 1 ──────── N availability_slots

availability_slots 1 ──────── 0..1 appointments

appointments 1 ──────── N appointment_status_history
```

### Relationship descriptions

**dentists_schedule → availability_slots**

`1 : N`

One dentist schedule can contain many availability slots.

```text
One schedule
     │
     ├── Slot 1
     ├── Slot 2
     ├── Slot 3
     └── Slot N
```

**availability_slots → appointments**

`1 : 0..1`

One availability slot can have zero or one appointment.

- `0` means that the slot is available.
- `1` means that the slot has been reserved by an appointment.

A slot should not be assigned to multiple appointments because this would
allow double booking (enforced by the `UNIQUE` constraint on `slot_id`).

**appointments → appointment_status_history**

`1 : N`

One appointment can have many status history records.

For example:

```text
PROGRAMADA
     ↓
CONFIRMADA
     ↓
EN_ATENCION
     ↓
FINALIZADA
```

Each change is stored in `appointment_status_history`.

---

## External References

The following identifiers belong to other microservices:

| Field | External Entity | Purpose |
|---|---|---|
| patient_id | Patient | Identifies the patient who owns the appointment |
| dentist_id | Dentist | Identifies the dentist assigned to the appointment |

These fields are not foreign keys because the corresponding tables belong
to other microservices and databases.

---

## Correlations

- Domain entities and business rules → `02-domain/entities-and-rules.md`
- Appointment bounded context → `02-domain/domain-map.md`
- Appointment domain events → `02-domain/domain-events.md`
- Distributed consistency and Saga patterns → `05-architecture/pattern-guide.md`
- API contracts → `07-api/contracts/openapi/`
- Detailed technical data model → `09-microservices/services/appointment-service/data-model.md`

---

## Relationship Summary

```text
                    dentists_schedule
                           │
                           │ 1 : N
                           ▼
                  availability_slots
                           │
                           │ 1 : 0..1
                           ▼
                      appointments
                           │
                           │ 1 : N
                           ▼
              appointment_status_history
```

---

## Database Ownership

```text
appointment-service
        │
        ▼
    PostgreSQL
        │
        ├── dentists_schedule
        │
        ├── availability_slots
        │
        ├── appointments
        │
        └── appointment_status_history
```

The `appointment-service` is the sole owner of these tables.

Other microservices must not directly query or modify this database.
Communication between services must occur through APIs or asynchronous
domain events.
