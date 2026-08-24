# Appointment Service — Documentation Work Summary
# Resumen del Trabajo de Documentación — Servicio de Citas

> **Bounded Context / Contexto delimitado:** Appointment Scheduling
> **Service / Servicio:** `appointment-service`
> **Responsible / Responsable:** Brayan Smith Bedoya Montealegre
> **Team / Equipo:**
> - Barrera Giraldo Harold Camilo — Auth Service (Tech Lead)
> - Bonilla Delgado Luis Ignacio — Clinical Service / Analytics
> - Pérez Lozada Daniel — Patients Service (DevOps)
> - Juan Diego Mora — Billing Service

---

## 1. Introduction / Introducción

**EN:** This document summarizes the documentation work carried out for the **Appointment Scheduling** bounded context within the OdontoSys distributed system. As other team members had already documented their respective bounded contexts (Identity and Access — IAM, and Patient Management) in the shared `02-domain/` folder, this work closes the gap for the appointments module: data model, domain events, and shared glossary terms. The goal is to keep all cross-cutting documents (`domain-events.md`, `glossary.md`) consistent and complete across every microservice, while keeping the appointment-service-specific data model in its own file.

**ES:** Este documento resume el trabajo de documentación realizado para el contexto delimitado de **Agendamiento de Citas (Appointment Scheduling)** dentro del sistema distribuido OdontoSys. Como mis compañeros ya habían documentado sus respectivos contextos (Identidad y Acceso — IAM, y Gestión de Pacientes) en la carpeta compartida `02-domain/`, este trabajo cierra el vacío correspondiente al módulo de citas: modelo de datos, eventos de dominio y términos del glosario compartido. El objetivo es mantener todos los documentos transversales (`domain-events.md`, `glossary.md`) consistentes y completos para cada microservicio, dejando el modelo de datos propio del servicio de citas en su propio archivo.

---

## 2. What was found / Qué se encontró

**EN:** Before making changes, the repository already contained:
- `data-model.md` — a **complete** data model for `appointment-service` (tables `appointments`, `dentists_schedule`, `availability_slots`, `appointment_status_history`), already following the same structure used by the other services.
- `domain-events.md` — documented only the **IAM** and **Patient Management** events; the appointments bounded context was missing entirely.
- `glossary.md` — documented terms for IAM and Patient Management, but no terms for appointments, schedules, or availability slots.
- `overview.md` — already correctly listed `appointment-service` (Spring Boot + PostgreSQL) and Brayan Smith Bedoya Montealegre as its responsible; no changes were needed here.

**ES:** Antes de hacer cambios, el repositorio ya contenía:
- `data-model.md` — un modelo de datos **completo** para `appointment-service` (tablas `appointments`, `dentists_schedule`, `availability_slots`, `appointment_status_history`), ya siguiendo la misma estructura usada por los demás servicios.
- `domain-events.md` — documentaba solo los eventos de **IAM** y **Gestión de Pacientes**; faltaba por completo el contexto de citas.
- `glossary.md` — documentaba términos de IAM y Gestión de Pacientes, pero ningún término de citas, horarios o disponibilidad.
- `overview.md` — ya listaba correctamente a `appointment-service` (Spring Boot + PostgreSQL) y a Brayan Smith Bedoya Montealegre como responsable; no se requirieron cambios aquí.

---

## 3. Work performed / Trabajo realizado

### 3.1 `domain-events.md` — Added the Appointment Scheduling event catalog

**EN:** Added a full set of domain events for the `Appointment` aggregate, following the exact same table format and past-tense naming convention already used for IAM (`UserRegistered`, `UserLocked`, etc.):

| Event | Trigger | Resulting status |
|---|---|---|
| `AppointmentScheduled` | A valid appointment is created and a slot is reserved | `PROGRAMADA` |
| `AppointmentConfirmed` | Patient confirms attendance | `CONFIRMADA` |
| `AppointmentRescheduled` | Appointment is moved to a different slot | slot changes |
| `AppointmentAttentionStarted` | Dentist begins the attention | `EN_ATENCION` |
| `AppointmentCompleted` | Dentist finishes the attention | `FINALIZADA` |
| `AppointmentCancelled` | Patient/administrator cancels the appointment | `CANCELADA` |
| `AppointmentNoShow` | Patient does not attend within the attendance window | `NO_ASISTIO` |

Also added the corresponding **event flows** (ASCII diagrams) and a new **Appointment Scheduling** row in the `Event summary table`.

**ES:** Se agregó un catálogo completo de eventos de dominio para el agregado `Appointment`, siguiendo el mismo formato de tabla y la misma convención de nombres en pasado ya usada para IAM (`UserRegistered`, `UserLocked`, etc.). Ver la tabla anterior. También se agregaron los **flujos de eventos** correspondientes (diagramas ASCII) y una nueva fila de **Appointment Scheduling** en la tabla resumen (`Event summary table`).

### 3.2 `glossary.md` — Added Appointment Scheduling terms

**EN:** Added domain terms (`Appointment`, `Appointment Status`, `Dentist Schedule`, `Availability Slot`, `Double Booking`, `No-Show`) and technical terms (`Appointment Service`, `Appointment Scheduling`) to the glossary, following the existing table structure.

Also flagged an inconsistency for the team: the glossary listed `Prisma Migrate` as *"the migration tool selected by the project"*, while `appointment-service` (Spring Boot) uses **Flyway**. Proposed splitting the glossary entry to reflect that migration tooling differs by service stack (Node.js services → Prisma Migrate, Spring Boot services → Flyway) instead of naming a single project-wide tool.

**ES:** Se agregaron términos de dominio (`Appointment`, `Appointment Status`, `Dentist Schedule`, `Availability Slot`, `Double Booking`, `No-Show`) y términos técnicos (`Appointment Service`, `Appointment Scheduling`) al glosario, siguiendo la estructura de tabla ya existente.

También se reportó al equipo una inconsistencia: el glosario listaba `Prisma Migrate` como *"la herramienta de migración seleccionada por el proyecto"*, mientras que `appointment-service` (Spring Boot) usa **Flyway**. Se propuso dividir esa entrada para reflejar que la herramienta de migraciones depende del stack de cada servicio (servicios Node.js → Prisma Migrate, servicios Spring Boot → Flyway), en lugar de nombrar una sola herramienta para todo el proyecto.

### 3.3 `data-model.md` — Reviewed, no changes needed

**EN:** This file was already complete and correctly attributed to me (Brayan Smith Bedoya Montealegre) as the responsible for `appointment-service`. It documents 4 tables, their SQL schema, indexes, modeling decisions, migration strategy (Flyway), DB engine justification, and the entity-relationship diagram. No structural changes were required — it served as the reference pattern for the additions made to `domain-events.md`.

**ES:** Este archivo ya estaba completo y correctamente atribuido a mí (Brayan Smith Bedoya Montealegre) como responsable de `appointment-service`. Documenta 4 tablas, su esquema SQL, índices, decisiones de modelado, estrategia de migración (Flyway), justificación del motor de base de datos y el diagrama entidad-relación. No se requirieron cambios estructurales — sirvió como el patrón de referencia para las adiciones hechas a `domain-events.md`.

### 3.4 `overview.md` — Reviewed, no changes needed on my side

**EN:** Reviewed the system overview; `appointment-service` was already correctly listed under technology stack (Spring Boot, PostgreSQL) and project contacts. I did flag a cross-document inconsistency to the team regarding the Prisma Migrate reference described above, since `overview.md` states Auth, Patients, Appointments, and Billing all use Spring Boot, which conflicts with the glossary implying a Node.js-based migration tool is project-wide.

**ES:** Se revisó el resumen del sistema; `appointment-service` ya estaba correctamente listado en el stack tecnológico (Spring Boot, PostgreSQL) y en los contactos del proyecto. Se reportó al equipo una inconsistencia entre documentos respecto a la referencia de Prisma Migrate descrita arriba, ya que `overview.md` indica que Auth, Patients, Appointments y Billing usan Spring Boot, lo cual entra en conflicto con que el glosario sugiera que una herramienta de migración basada en Node.js aplica a todo el proyecto.

---

## 4. Files touched / Archivos modificados

| File / Archivo | Change / Cambio |
|---|---|
| `02-domain/domain-events.md` | Added Appointment Scheduling event catalog, flows, and summary table row / Se agregó el catálogo de eventos, flujos y fila resumen de Appointment Scheduling |
| `02-domain/glossary.md` | Added Appointment Scheduling domain and technical terms; flagged Prisma/Flyway inconsistency / Se agregaron términos de dominio y técnicos de Appointment Scheduling; se reportó inconsistencia Prisma/Flyway |
| `06-data/` (or equivalent) `data-model.md` | Reviewed only, already complete / Solo se revisó, ya estaba completo |
| `overview.md` | Reviewed only, already correct / Solo se revisó, ya estaba correcto |

**EN:** No folders were created, deleted, or moved as part of this work — the changes were additive edits to existing shared documentation files (`domain-events.md`, `glossary.md`) plus a review pass over `data-model.md` and `overview.md`.

**ES:** No se crearon, eliminaron ni movieron carpetas como parte de este trabajo — los cambios fueron adiciones a archivos de documentación compartida ya existentes (`domain-events.md`, `glossary.md`), además de una revisión de `data-model.md` y `overview.md`.

---

## 5. Suggested commit / Commit sugerido

```
docs: update domain models with Appointment Scheduling context

- Add appointment lifecycle events to domain-events.md (Scheduled,
  Confirmed, Rescheduled, AttentionStarted, Completed, Cancelled, NoShow)
- Add Appointment Scheduling terms to glossary.md
- Review data-model.md and overview.md (no changes required)
```

---

## 6. Open items for the team / Pendientes para el equipo

**EN:**
1. Resolve the `Prisma Migrate` vs `Flyway` inconsistency in `glossary.md` / `overview.md` — clarify whether Auth/Patients services use Node.js (justifying Prisma Migrate) or if the glossary entry is outdated.
2. Confirm whether other services need to subscribe to the new appointment events (`AppointmentCancelled`, `AppointmentNoShow`, etc.) for cross-context policies (e.g., Billing or Patient Management reacting to cancellations).

**ES:**
1. Resolver la inconsistencia entre `Prisma Migrate` y `Flyway` en `glossary.md` / `overview.md` — aclarar si los servicios de Auth/Patients usan Node.js (lo que justificaría Prisma Migrate) o si esa entrada del glosario está desactualizada.
2. Confirmar si otros servicios necesitan suscribirse a los nuevos eventos de citas (`AppointmentCancelled`, `AppointmentNoShow`, etc.) para políticas entre contextos (ej. que Facturación o Gestión de Pacientes reaccionen ante cancelaciones).

---

## 7. Correlations / Correlaciones

- Data model → `06-data/data-model.md` (appointment-service)
- Domain events → `02-domain/domain-events.md`
- Glossary → `02-domain/glossary.md`
- System overview → `overview.md`
