# Di Lucca / OdontoSys — Weekly Progress Report

**Reporting period:** August 23–30, 2026  
**Prepared on:** September 1, 2026  
**Documentation repository:** [code-corhuila/ods-docs](https://github.com/code-corhuila/ods-docs)  
**Monolithic MVP repository:** [DanielPerez1822/di-lucca-mvp](https://github.com/DanielPerez1822/di-lucca-mvp)  
**Figma mockup:** [OdontoSys / Di Lucca](https://www.figma.com/design/HB2cfqrV1HFzARClICuitz/OdontoSys?node-id=0-1&t=B9PUkD44i8l2u72o-1)

---

## English Version

### 1. Executive summary

This week represented a major consolidation milestone for Di Lucca / OdontoSys. The team did not work only on isolated documents or screens: it connected the business definition, domain model, requirements, architecture, user experience, and a working monolithic MVP into a coherent product baseline.

The main outcomes were:

- The project scope and dental-care business rules were corrected, expanded, and aligned.
- The core domain was documented around identity and access, appointment scheduling, clinical care and patient records, billing, payments, and analytics.
- Product discovery, backlog, roadmap, governance, data, architecture, and UX/UI documentation were aligned with the Di Lucca context.
- The approved MVP user stories, critical acceptance scenarios, non-functional requirements, and traceability matrix were defined.
- Hexagonal architecture was documented in depth, including ports and adapters, bounded contexts, use cases, dependency rules, persistence, messaging, testing, and architectural decisions.
- A GitHub Actions workflow was added to synchronize issue activity with the project board status.
- An Analytics bounded context was introduced as a downstream, event-driven read model for operational and financial KPIs.
- A functional monolithic MVP was implemented with an Angular frontend and a Spring Boot backend connected to PostgreSQL.
- Authentication, authorization, patient management, procedures, schedules, appointments, clinical records, invoices, payments, user administration, and dashboard analytics were represented in the MVP.
- The authentication and account-recovery experience was redesigned, and the general visual style was made more consistent.
- A Figma mockup was completed to communicate the intended user experience across the principal product flows.

Across both repositories, the weekly record contains **17 commits**: **11 documentation and architecture commits** and **6 MVP commits**.

### 2. Workstream overview

| Workstream | Weekly result |
|---|---|
| Business and domain | Scope, entities, lifecycles, events, invariants, analytics projections, and business rules were expanded and aligned. |
| Product and requirements | Discovery artifacts, backlog, roadmap, user stories, NFRs, and traceability were established or improved. |
| Architecture | Hexagonal architecture and seven architecture decisions were documented as the target technical baseline. |
| UX/UI | Design system, navigation map, wireframes, required UI states, and a complete Figma mockup were consolidated. |
| Automation | Issue-to-project-board status synchronization was introduced through GitHub Actions. |
| MVP | A full-stack monolithic implementation was delivered using Angular, Spring Boot, and PostgreSQL. |
| Security and access | JWT authentication, refresh/logout flows, password recovery, route guards, role controls, and administrative access were implemented. |
| Operational modules | Patients, procedures, appointments, schedules, clinical records, invoices, payments, holidays, and users were implemented as connected modules. |

### 3. Documentation repository work

The documentation repository contains **11 commits** for the reporting period. Git records show **8,249 additions** and **4,908 deletions** across 79 commit-level file-change entries. These numbers reflect both new content and extensive restructuring or replacement of earlier documentation.

#### 3.1 Scope, domain, and business rules

The scope was clarified to establish what belongs to the MVP, the permissions associated with each role, the main appointment rules, exclusions, assumptions, constraints, external dependencies, and change-control correlations.

The domain documentation was strengthened around four major areas:

- **Identity and Access:** users, roles, credentials, account state, authentication, and access invariants.
- **Appointment Scheduling:** appointments, dentist schedules, availability, appointment lifecycle, cancellation, and rescheduling rules.
- **Clinical Care and Patient Records:** patients, clinical records, treatments, procedures, and clinical-history rules.
- **Billing and Payments:** prices, billable items, invoices, payments, financial states, and cross-context rules.
- **Analytics:** event-driven, read-only projections for appointment, patient, invoicing, and payment indicators.

Domain events, entity relationships, lifecycles, and invariants were reviewed so that the documentation expresses system behavior rather than only database structure.

The Analytics bounded context was added as a downstream context owned by `analytics-service`. It consumes events from Patient Management, Appointments, and Billing through RabbitMQ, builds query-optimized projections, and never accesses the transactional databases of other contexts directly. The documented indicators include appointment totals by status and date range, patient registrations, invoiced amounts, and paid amounts.

Its principal aggregate, `AnalyticsProjection`, records a reporting period, non-negative metrics, the latest applied-event timestamp, and a `FRESH` or `STALE` freshness state. The rules require idempotent processing by `eventId`, privacy protection, rebuildable projections, and explicit freshness information. The commit also added the `PaymentReceived` event and defined the analytics consumption contract for patient, appointment, invoice, and payment events.

#### 3.2 Governance, product, data, and UX alignment

A broad documentation-alignment commit adapted 39 files to the Di Lucca project. It covered governance conventions, Definition of Ready, Definition of Done, Git practices, security policies, system context, glossary, domain model, product vision, requirements, data models, service catalog, and UX/UI material.

New or substantially expanded artifacts included:

- Discovery brief.
- Product backlog.
- Product roadmap.
- Polyglot-persistence architecture decision.
- UX wireframes.
- Design system and navigation map.
- Microservices documentation and service catalog alignment.

The subsequent cleanup commit removed obsolete or duplicated material and reduced several oversized documents. This made the repository more focused and left the active documentation closer to the approved Di Lucca scope.

#### 3.3 Requirements and traceability

The requirements work established a reusable user-story template and a more explicit MVP baseline. The approved user stories were supplemented with:

- A shared Definition of Done.
- Critical Given/When/Then scenarios.
- MVP scenario coverage.
- Explicit MVP scope and correlations.

Eight non-functional requirement groups were documented:

1. Performance.
2. Availability.
3. Scalability.
4. Security.
5. Observability.
6. Maintainability.
7. Portability.
8. Disaster recovery and recovery.

The traceability matrix was updated to connect business needs, user stories, architecture, data, APIs, UX/UI, and implementation evidence. It also identifies coverage gaps that still require implementation or verification evidence.

#### 3.4 Architecture decisions

The architecture documentation adopted **hexagonal architecture (ports and adapters)** as the organizing model. It defines domain, application, and infrastructure layers; driving and driven ports; primary and secondary adapters; service boundaries; dependency inversion; and testing expectations.

The architecture material also documents target patterns for domain events, messaging, transactional outbox, idempotent consumers, care-closing coordination, persistence independence, and eventual migration from the MVP monolith toward service boundaries.

Seven Architecture Decision Records were consolidated:

- **ADR-001:** Hexagonal architecture for all microservices.
- **ADR-002:** Polyglot persistence and event-driven care closure.
- **ADR-003:** API Gateway as the external entry point.
- **ADR-004:** RabbitMQ for asynchronous integration events.
- **ADR-005:** JWT-based authentication for service access.
- **ADR-006:** Transactional outbox and idempotent consumers.
- **ADR-007:** Process Manager for care-closing coordination.

These decisions describe the target architecture. The MVP remains intentionally monolithic, allowing the product flows and domain assumptions to be validated before a distributed implementation is introduced.

#### 3.5 Project-board automation

A GitHub Actions workflow, `.github/workflows/board-sync.yml`, was added. Its purpose is to synchronize issue activity with the project board status, reducing manual board maintenance and improving consistency between engineering work and project tracking.

### 4. Documentation repository commit record

| Date | Commit | Author | Description and result |
|---|---|---|---|
| Aug 23 | [`fd17915`](https://github.com/code-corhuila/dlc-docs/commit/fd17915082db52668910f742bf69436b3052d970) | LuisBonilla2260 | Added the Analytics bounded context and updated the glossary, scope, domain-event catalog, domain map, entities, and business rules. Defined event-driven read models, KPIs, freshness, privacy, and idempotency rules. 5 files; +167 / -10. |
| Aug 25 | `efd31cc` | Harold Camilo Barrera Giraldo | Corrected and expanded scope, domain events, domain map, entities, and business rules. 4 files; +415 / -322. |
| Aug 25 | `ad338d0` | Harold Camilo Barrera Giraldo | Moved lifecycle images to the domain root and corrected their references. 3 files; +2 / -2, plus 2 image renames. |
| Aug 25 | `e563a0a` | Juan Diego Mora Alvarado | Added and expanded entities, lifecycles, and business rules. 1 file; +350 / -135. |
| Aug 26 | `67f7859` | Jesús Ariel González Bonilla | Added the issue-to-project-board status synchronization workflow. 1 new file; +74. |
| Aug 29 | `67b2765` | bsbedoya | Aligned governance, context, domain, product, requirements, architecture, data, UML, services, and UX/UI documentation. 39 files; +2,446 / -2,191. |
| Aug 29 | `83f5cab` | bsbedoya | Cleaned and focused project documentation by removing outdated or duplicated content. 22 files; +72 / -1,850. |
| Aug 30 | `c2db297` | Harold Camilo Barrera Giraldo | Expanded the reusable user-story template and its guidance. 1 file; +2,595 / -19. |
| Aug 30 | `a079407` | Harold Camilo Barrera Giraldo | Added and refined NFRs and the traceability matrix. 2 files; +232 / -115. |
| Aug 30 | `846babc` | Harold Camilo Barrera Giraldo | Defined the approved MVP user stories, critical scenarios, and MVP coverage. 1 file; +52 / -2. |
| Aug 30 | `929f953` | Harold Camilo Barrera Giraldo | Added the detailed hexagonal-architecture guide and consolidated ADRs. 2 files; +1,844 / -262. |

### 5. Monolithic MVP implementation

The MVP repository was created as a single full-stack codebase with two main areas:

- `backend/odontosys-api`: Java 21 and Spring Boot 3.4.5 REST API.
- `frontend/dilucca`: Angular 20 web application with server-side rendering support.

The repository contains **6 commits** during the reporting period. Its short-stat history reports **32,309 additions** and **664 deletions** across 416 commit-level file-change entries. The large initial implementation commit introduced 367 files and approximately 30,000 lines.

#### 5.1 Backend

The backend follows a ports-and-adapters-inspired package structure even though it is deployed as one monolith. It contains domain models, application services, input and output ports, web controllers, persistence adapters, JPA entities, mappers, repositories, configuration, and security infrastructure.

The implemented technical foundation includes:

- Java 21 and Spring Boot 3.4.5.
- Spring Web REST endpoints.
- Spring Data JPA and PostgreSQL persistence.
- Spring Security.
- JWT access and refresh-token handling with JJWT 0.12.6.
- BCrypt password encoding.
- Bean validation and centralized exception handling.
- Email support for recovery and invoice workflows.
- OpenAPI / Swagger documentation.
- CORS configuration, database initialization, schema, and seed data.
- Docker Compose configuration for PostgreSQL.

The API exposes controllers for authentication, users, patients, procedures, dentist schedules, availability slots, appointments, holidays, clinical records, invoices, and payments.

#### 5.2 Frontend

The Angular application implements public, authentication, operational, clinical, financial, and administrative views. The application uses route guards and role guards, JWT and error interceptors, domain-specific services, typed models, reusable layout components, forms, tables, modals, calendars, and dashboards.

The principal routes are:

- `/` — public welcome page.
- `/login` — authentication.
- `/forgot-password` and `/reset-password` — account recovery.
- `/dashboard` — operational and administrative indicators.
- `/patients` — patient management.
- `/procedures` — procedure catalog.
- `/calendar` and `/appointments` — calendar and appointment workflows.
- `/schedules` — dentist schedule and availability management.
- `/medical-records` — clinical history and medical-record workflows.
- `/invoices` — invoices and payments.
- `/user-management` — administrator-only user management.

#### 5.3 Functional coverage

The implemented MVP covers the following connected workflows:

- Login, current-user retrieval, token refresh, logout, forgotten password, and password reset.
- User creation, listing, update, disable/delete operations, and role-restricted administration.
- Patient creation, search/listing, retrieval, update, and deletion.
- Procedure catalog creation, listing, update, and deletion.
- Dentist schedule definition, schedule copying, slot generation, and available-slot consultation.
- Appointment creation, listing, retrieval, rescheduling, and cancellation.
- Holiday configuration affecting operational availability.
- Clinical-record creation, update, retrieval, and patient-history consultation.
- Invoice creation from a clinical record, invoice listing and update, payment registration, and invoice email delivery.
- Administrative dashboard analytics and operational summaries.

### 6. MVP commit record

| Date | Commit | Author | Description and result |
|---|---|---|---|
| Aug 30 | `a5c8a18` | DanielPerez1822 | Initialized the repository with its README. 1 file; +1. |
| Aug 30 | `df491e6` | DanielPerez1822 | Delivered the functional Di Lucca monolith: Angular frontend, Spring Boot backend, PostgreSQL schema, security, APIs, and principal business modules. 367 files; +30,074. |
| Aug 30 | `15c7226` | DanielPerez1822 | Added an invoice-detail modal to the clinical-record flow and expanded its integration and presentation. 6 files; +735 / -12. |
| Aug 30 | `29ff643` | DanielPerez1822 | Upgraded the administrator dashboard analytics, layout, indicators, and behavior. 3 files; +646 / -158. |
| Aug 30 | `fb414b6` | DanielPerez1822 | Harmonized styling across authentication, appointments, calendar, dashboard, invoices, clinical records, patients, procedures, schedules, users, layout, and global styles. 22 files; +250 / -235. |
| Aug 30 | `c127204` | Harold Camilo Barrera Giraldo | Redesigned login and recovery views, added a welcome page and brand assets, updated routing, layout, and global styling. 17 files; +603 / -259. |

### 7. Figma mockup

The Figma file established the visual and interaction reference for Di Lucca / OdontoSys. The reviewed canvas includes the following screens and workflows:

- Home and welcome experience.
- Login.
- Password recovery.
- Appointment calendar.
- New appointment creation.
- Patient list.
- Patient registration.
- Patient clinical record.
- Billing and payments.
- Invoice and payment detail.
- Administration.
- Dashboard and analytics.

The mockup provides a shared reference for information hierarchy, page composition, navigation, forms, tables, calendars, cards, status indicators, and administrative views. It also supports the documentation repository's design system, navigation map, and wireframe catalog. The final authentication redesign in the MVP introduced branded clinic imagery, Di Lucca logos, a public welcome page, and updated authentication/recovery layouts consistent with this design direction.

### 8. Relationship between documentation, mockup, and MVP

The week's work created three complementary deliverables:

1. **Documentation** defines the business intent, rules, requirements, quality expectations, traceability, and target architecture.
2. **Figma** translates the product flows into a shared visual and interaction model.
3. **The monolithic MVP** validates the principal workflows in an integrated application before the target microservice architecture is pursued.

This separation is intentional. The documentation describes the long-term architectural direction, while the monolith provides a lower-complexity vehicle for validating users, workflows, domain behavior, UI decisions, and data interactions. The MVP therefore acts as an implementation baseline and learning artifact, not as evidence that every target distributed-architecture decision has already been implemented.

### 9. Weekly result

By the end of the week, Di Lucca / OdontoSys had moved from a primarily conceptual initiative to a documented and demonstrable product baseline. The team completed the core functional MVP, established a visual mockup, formalized the approved MVP requirements, documented quality attributes and traceability, and defined the architecture needed for future evolution.

The most important achievement is alignment: business rules, screens, frontend routes, backend capabilities, data structures, and architectural direction now describe the same dental appointment and clinic-management product.

---

## Versión en Español

### 1. Resumen ejecutivo

Esta semana representó un hito importante de consolidación para Di Lucca / OdontoSys. El equipo no trabajó únicamente en documentos o pantallas aisladas: conectó la definición del negocio, el modelo de dominio, los requisitos, la arquitectura, la experiencia de usuario y un MVP monolítico funcional dentro de una base de producto coherente.

Los principales resultados fueron:

- Se corrigieron, ampliaron y alinearon el alcance del proyecto y las reglas del negocio odontológico.
- Se documentó el dominio central alrededor de identidad y acceso, agendamiento de citas, atención clínica e historias de pacientes, facturación, pagos y analítica.
- Se alinearon con el contexto de Di Lucca los documentos de descubrimiento de producto, backlog, roadmap, gobierno, datos, arquitectura y UX/UI.
- Se definieron las historias de usuario aprobadas para el MVP, los escenarios críticos de aceptación, los requisitos no funcionales y la matriz de trazabilidad.
- Se documentó detalladamente la arquitectura hexagonal, incluyendo puertos y adaptadores, contextos delimitados, casos de uso, reglas de dependencia, persistencia, mensajería, pruebas y decisiones arquitectónicas.
- Se agregó un flujo de GitHub Actions para sincronizar la actividad de los issues con el estado del tablero del proyecto.
- Se incorporó el contexto delimitado de Analytics como un modelo de lectura downstream y orientado a eventos para KPI operativos y financieros.
- Se implementó un MVP monolítico funcional con frontend en Angular y backend en Spring Boot conectado a PostgreSQL.
- El MVP incorporó autenticación, autorización, pacientes, procedimientos, horarios, citas, historias clínicas, facturas, pagos, administración de usuarios y analítica del dashboard.
- Se rediseñó la experiencia de autenticación y recuperación de cuenta, y se unificó el estilo visual general.
- Se completó un mockup en Figma para comunicar la experiencia esperada en los principales flujos del producto.

Entre ambos repositorios, el registro semanal contiene **17 commits**: **11 commits de documentación y arquitectura** y **6 commits del MVP**.

### 2. Resumen por frente de trabajo

| Frente | Resultado semanal |
|---|---|
| Negocio y dominio | Se ampliaron y alinearon el alcance, las entidades, los ciclos de vida, los eventos, las invariantes, las proyecciones analíticas y las reglas de negocio. |
| Producto y requisitos | Se establecieron o mejoraron descubrimiento, backlog, roadmap, historias de usuario, RNF y trazabilidad. |
| Arquitectura | Se documentaron la arquitectura hexagonal y siete decisiones arquitectónicas como base técnica objetivo. |
| UX/UI | Se consolidaron el sistema de diseño, el mapa de navegación, los wireframes, los estados requeridos y el mockup completo en Figma. |
| Automatización | Se incorporó la sincronización del estado de issues con el tablero mediante GitHub Actions. |
| MVP | Se entregó una implementación monolítica full stack con Angular, Spring Boot y PostgreSQL. |
| Seguridad y acceso | Se implementaron JWT, refresh/logout, recuperación de contraseña, guards de rutas, controles por rol y acceso administrativo. |
| Módulos operativos | Se conectaron pacientes, procedimientos, citas, horarios, historias clínicas, facturas, pagos, festivos y usuarios. |

### 3. Trabajo realizado en el repositorio de documentación

El repositorio de documentación contiene **11 commits** durante el periodo. El historial de Git registra **8.249 adiciones** y **4.908 eliminaciones** en 79 entradas de cambio de archivo a nivel de commit. Estas cifras incluyen contenido nuevo y una reestructuración extensa de documentación anterior.

#### 3.1 Alcance, dominio y reglas de negocio

Se aclaró el alcance para establecer qué pertenece al MVP, los permisos por rol, las reglas principales de citas, las exclusiones, las suposiciones, las restricciones, las dependencias externas y las correlaciones de control de cambios.

La documentación del dominio se fortaleció alrededor de cuatro áreas principales:

- **Identidad y acceso:** usuarios, roles, credenciales, estado de la cuenta, autenticación e invariantes de acceso.
- **Agendamiento de citas:** citas, horarios de odontólogos, disponibilidad, ciclo de vida, cancelación y reprogramación.
- **Atención clínica e historias de pacientes:** pacientes, historias clínicas, tratamientos, procedimientos y reglas clínicas.
- **Facturación y pagos:** precios, ítems facturables, facturas, pagos, estados financieros y reglas entre contextos.
- **Analytics:** proyecciones de solo lectura orientadas a eventos para indicadores de citas, pacientes, facturación y pagos.

Se revisaron los eventos de dominio, las relaciones entre entidades, los ciclos de vida y las invariantes para expresar el comportamiento del sistema y no solamente su estructura de datos.

Se agregó Analytics como contexto downstream, propiedad de `analytics-service`. Consume mediante RabbitMQ eventos de Patient Management, Appointments y Billing, construye proyecciones optimizadas para consulta y nunca accede directamente a las bases de datos transaccionales de otros contextos. Los indicadores documentados incluyen totales de citas por estado y periodo, registros de pacientes, montos facturados y montos pagados.

Su agregado principal, `AnalyticsProjection`, registra el periodo consultado, métricas no negativas, la fecha del último evento aplicado y un estado de frescura `FRESH` o `STALE`. Las reglas exigen procesamiento idempotente mediante `eventId`, protección de privacidad, proyecciones reconstruibles e información explícita de frescura. El commit también agregó el evento `PaymentReceived` y el contrato de consumo analítico para eventos de pacientes, citas, facturas y pagos.

#### 3.2 Alineación de gobierno, producto, datos y UX

Un commit de alineación general adaptó 39 archivos al proyecto Di Lucca. Incluyó convenciones de gobierno, Definition of Ready, Definition of Done, prácticas Git, políticas de seguridad, contexto, glosario, dominio, visión de producto, requisitos, modelos de datos, catálogo de servicios y material UX/UI.

Entre los artefactos nuevos o ampliados se encuentran:

- Brief de descubrimiento.
- Backlog del producto.
- Roadmap del producto.
- Decisión de persistencia políglota.
- Wireframes de UX.
- Sistema de diseño y mapa de navegación.
- Documentación y catálogo de microservicios.

El commit posterior de limpieza retiró contenido obsoleto o duplicado y redujo documentos sobredimensionados. El repositorio quedó más enfocado en el alcance aprobado de Di Lucca.

#### 3.3 Requisitos y trazabilidad

Se creó una plantilla reutilizable para historias de usuario y se hizo explícita la línea base del MVP. Las historias aprobadas se complementaron con una Definition of Done compartida, escenarios críticos Given/When/Then, cobertura de escenarios del MVP, alcance y correlaciones.

Se documentaron ocho grupos de requisitos no funcionales:

1. Rendimiento.
2. Disponibilidad.
3. Escalabilidad.
4. Seguridad.
5. Observabilidad.
6. Mantenibilidad.
7. Portabilidad.
8. Recuperación ante desastres.

La matriz de trazabilidad conecta necesidades de negocio, historias de usuario, arquitectura, datos, APIs, UX/UI y evidencia de implementación. Además, señala vacíos de cobertura que todavía requieren evidencia de implementación o verificación.

#### 3.4 Decisiones de arquitectura

La documentación adoptó la **arquitectura hexagonal o de puertos y adaptadores** como modelo de organización. Define las capas de dominio, aplicación e infraestructura; puertos de entrada y salida; adaptadores primarios y secundarios; límites de servicios; inversión de dependencias; y expectativas de pruebas.

También documenta patrones objetivo para eventos de dominio, mensajería, transactional outbox, consumidores idempotentes, coordinación del cierre de atención, independencia de persistencia y migración futura del monolito hacia límites de servicios.

Se consolidaron siete Architecture Decision Records:

- **ADR-001:** arquitectura hexagonal para todos los microservicios.
- **ADR-002:** persistencia políglota y cierre de atención orientado a eventos.
- **ADR-003:** API Gateway como punto de entrada externo.
- **ADR-004:** RabbitMQ para eventos de integración asíncronos.
- **ADR-005:** autenticación basada en JWT.
- **ADR-006:** transactional outbox y consumidores idempotentes.
- **ADR-007:** Process Manager para coordinar el cierre de atención.

Estas decisiones describen la arquitectura objetivo. El MVP continúa siendo intencionalmente monolítico para validar los flujos y las hipótesis del dominio antes de introducir una implementación distribuida.

#### 3.5 Automatización del tablero

Se agregó el workflow `.github/workflows/board-sync.yml`. Su propósito es sincronizar la actividad de los issues con el estado del tablero, reducir mantenimiento manual y mejorar la consistencia entre el trabajo técnico y el seguimiento del proyecto.

### 4. Registro de commits del repositorio de documentación

| Fecha | Commit | Autor | Descripción y resultado |
|---|---|---|---|
| 23 ago. | [`fd17915`](https://github.com/code-corhuila/dlc-docs/commit/fd17915082db52668910f742bf69436b3052d970) | LuisBonilla2260 | Agregó el contexto delimitado de Analytics y actualizó glosario, alcance, eventos, mapa de dominio, entidades y reglas. Definió modelos de lectura orientados a eventos, KPI, frescura, privacidad e idempotencia. 5 archivos; +167 / -10. |
| 25 ago. | `efd31cc` | Harold Camilo Barrera Giraldo | Corrigió y amplió alcance, eventos, mapa de dominio, entidades y reglas. 4 archivos; +415 / -322. |
| 25 ago. | `ad338d0` | Harold Camilo Barrera Giraldo | Movió las imágenes de ciclos de vida y corrigió sus referencias. 3 archivos; +2 / -2 y 2 imágenes renombradas. |
| 25 ago. | `e563a0a` | Juan Diego Mora Alvarado | Agregó y amplió entidades, ciclos de vida y reglas de negocio. 1 archivo; +350 / -135. |
| 26 ago. | `67f7859` | Jesús Ariel González Bonilla | Agregó la sincronización del estado de issues con el tablero. 1 archivo nuevo; +74. |
| 29 ago. | `67b2765` | bsbedoya | Alineó gobierno, contexto, dominio, producto, requisitos, arquitectura, datos, UML, servicios y UX/UI. 39 archivos; +2.446 / -2.191. |
| 29 ago. | `83f5cab` | bsbedoya | Limpió y enfocó la documentación, retirando contenido obsoleto o duplicado. 22 archivos; +72 / -1.850. |
| 30 ago. | `c2db297` | Harold Camilo Barrera Giraldo | Amplió la plantilla reutilizable de historias de usuario. 1 archivo; +2.595 / -19. |
| 30 ago. | `a079407` | Harold Camilo Barrera Giraldo | Agregó y refinó los RNF y la matriz de trazabilidad. 2 archivos; +232 / -115. |
| 30 ago. | `846babc` | Harold Camilo Barrera Giraldo | Definió las historias aprobadas del MVP, escenarios críticos y cobertura. 1 archivo; +52 / -2. |
| 30 ago. | `929f953` | Harold Camilo Barrera Giraldo | Agregó la guía detallada de arquitectura hexagonal y consolidó los ADR. 2 archivos; +1.844 / -262. |

### 5. Implementación del MVP monolítico

El repositorio del MVP se creó como una única base de código full stack con dos áreas:

- `backend/odontosys-api`: API REST en Java 21 y Spring Boot 3.4.5.
- `frontend/dilucca`: aplicación web Angular 20 con soporte para renderizado del lado del servidor.

El repositorio contiene **6 commits** durante el periodo. El historial short-stat registra **32.309 adiciones** y **664 eliminaciones** en 416 entradas de cambio de archivo. El gran commit inicial incorporó 367 archivos y cerca de 30.000 líneas.

#### 5.1 Backend

El backend emplea una estructura inspirada en puertos y adaptadores, aunque se despliega como un monolito. Contiene modelos de dominio, servicios de aplicación, puertos de entrada y salida, controladores web, adaptadores de persistencia, entidades JPA, mappers, repositorios, configuración y seguridad.

La base técnica implementada incluye Java 21, Spring Boot, REST, Spring Data JPA, PostgreSQL, Spring Security, JWT y refresh tokens, BCrypt, validación, manejo centralizado de excepciones, correo electrónico, OpenAPI/Swagger, CORS, inicialización de datos y Docker Compose para PostgreSQL.

La API expone controladores para autenticación, usuarios, pacientes, procedimientos, horarios, disponibilidad, citas, festivos, historias clínicas, facturas y pagos.

#### 5.2 Frontend

La aplicación Angular implementa vistas públicas, de autenticación, operativas, clínicas, financieras y administrativas. Utiliza guards de autenticación y rol, interceptores JWT y de errores, servicios por dominio, modelos tipados, layout reutilizable, formularios, tablas, modales, calendarios y dashboards.

Las rutas principales son:

- `/` — bienvenida pública.
- `/login` — autenticación.
- `/forgot-password` y `/reset-password` — recuperación de cuenta.
- `/dashboard` — indicadores operativos y administrativos.
- `/patients` — gestión de pacientes.
- `/procedures` — catálogo de procedimientos.
- `/calendar` y `/appointments` — calendario y citas.
- `/schedules` — horarios y disponibilidad de odontólogos.
- `/medical-records` — historias clínicas.
- `/invoices` — facturas y pagos.
- `/user-management` — gestión de usuarios exclusiva para administración.

#### 5.3 Cobertura funcional

El MVP implementa flujos conectados de autenticación, refresh, logout y recuperación; administración de usuarios y roles; CRUD de pacientes y procedimientos; definición de horarios y generación de slots; creación, consulta, reprogramación y cancelación de citas; festivos; historias clínicas e historial del paciente; facturación desde la historia clínica; registro de pagos; envío de facturas por correo; y analítica administrativa.

### 6. Registro de commits del MVP

| Fecha | Commit | Autor | Descripción y resultado |
|---|---|---|---|
| 30 ago. | `a5c8a18` | DanielPerez1822 | Inicializó el repositorio con su README. 1 archivo; +1. |
| 30 ago. | `df491e6` | DanielPerez1822 | Entregó el monolito funcional: Angular, Spring Boot, PostgreSQL, seguridad, APIs y módulos principales. 367 archivos; +30.074. |
| 30 ago. | `15c7226` | DanielPerez1822 | Agregó el modal de detalle de factura al flujo de historias clínicas y amplió su integración. 6 archivos; +735 / -12. |
| 30 ago. | `29ff643` | DanielPerez1822 | Mejoró la analítica, indicadores, comportamiento y presentación del dashboard administrativo. 3 archivos; +646 / -158. |
| 30 ago. | `fb414b6` | DanielPerez1822 | Unificó estilos de autenticación, citas, calendario, dashboard, facturas, historias, pacientes, procedimientos, horarios, usuarios y layout. 22 archivos; +250 / -235. |
| 30 ago. | `c127204` | Harold Camilo Barrera Giraldo | Rediseñó login y recuperación, agregó bienvenida y recursos de marca, y actualizó rutas, layout y estilos globales. 17 archivos; +603 / -259. |

### 7. Mockup en Figma

El archivo de Figma estableció la referencia visual y de interacción para Di Lucca / OdontoSys. El lienzo revisado contiene:

- Home y bienvenida.
- Login.
- Recuperación de contraseña.
- Calendario de citas.
- Creación de una nueva cita.
- Listado de pacientes.
- Registro de pacientes.
- Expediente o historia clínica del paciente.
- Facturación y pagos.
- Detalle de factura y pago.
- Administración.
- Dashboard y analítica.

El mockup aporta una referencia compartida para jerarquía de información, composición, navegación, formularios, tablas, calendarios, tarjetas, indicadores de estado y vistas administrativas. También respalda el sistema de diseño, el mapa de navegación y el catálogo de wireframes del repositorio documental. El rediseño final del MVP incorporó imágenes de clínica, logos de Di Lucca, una bienvenida pública y layouts actualizados para autenticación y recuperación, siguiendo esta dirección visual.

### 8. Relación entre documentación, mockup y MVP

El trabajo de la semana produjo tres entregables complementarios:

1. **La documentación** define la intención del negocio, reglas, requisitos, atributos de calidad, trazabilidad y arquitectura objetivo.
2. **Figma** convierte los flujos del producto en un modelo visual y de interacción compartido.
3. **El MVP monolítico** valida los principales flujos dentro de una aplicación integrada antes de avanzar hacia la arquitectura objetivo de microservicios.

La separación es intencional. La documentación describe la dirección arquitectónica de largo plazo; el monolito ofrece un medio de menor complejidad para validar usuarios, flujos, comportamiento del dominio, decisiones de interfaz e interacción de datos. Por eso, el MVP funciona como línea base de implementación y aprendizaje, no como evidencia de que toda la arquitectura distribuida objetivo ya esté implementada.

### 9. Resultado de la semana

Al terminar la semana, Di Lucca / OdontoSys pasó de ser una iniciativa principalmente conceptual a contar con una base de producto documentada y demostrable. El equipo completó el MVP funcional central, estableció un mockup visual, formalizó los requisitos aprobados, documentó los atributos de calidad y la trazabilidad, y definió la arquitectura para la evolución futura.

El logro principal es la alineación: las reglas de negocio, las pantallas, las rutas del frontend, las capacidades del backend, las estructuras de datos y la dirección arquitectónica ahora describen el mismo producto de gestión odontológica y agendamiento de citas.

---

## Evidence basis / Base de evidencia

This report was prepared from the Git history and file contents of both repositories for August 23–30, 2026, plus a visual review of the public Figma canvas linked above. Commit metrics are Git short-stat/numstat values and therefore count changes per commit rather than unique final lines or unique files. The report documents repository evidence; it does not claim that a full end-to-end runtime, load, security, or acceptance test suite was executed as part of this reporting activity.

Este informe se preparó a partir del historial Git y del contenido de ambos repositorios entre el 23 y el 30 de agosto de 2026, además de una revisión visual del lienzo público de Figma enlazado anteriormente. Las métricas corresponden a short-stat/numstat de Git y contabilizan cambios por commit, no líneas finales únicas ni archivos únicos. El informe documenta evidencia de repositorio; no afirma que durante esta actividad de documentación se haya ejecutado una suite completa de pruebas end-to-end, carga, seguridad o aceptación.
