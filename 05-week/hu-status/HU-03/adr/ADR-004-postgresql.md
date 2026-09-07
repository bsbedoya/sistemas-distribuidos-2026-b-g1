# ADR-004: Retain PostgreSQL while recording schema and integrity drift

- Status: Recorded existing database decision; runtime integrity unverified.
- Date: 2026-09-06 (audit date).
- Scope: Shared persistence for MVP 1 / Cut 1 at `7a44eb8`.

## Context

The application uses relational identities, clinical detail rows and invoice payments. [data-model.md](../../HU-02/data-model.md) describes 15 SQL tables, but the SQL file is not the only schema source. Hibernate executes first by default, and several entity definitions differ from the SQL.

## Decision

Retain PostgreSQL as the sole implemented persistent database. Distinguish **SQL-declared**, **JPA-mapped** and **runtime-observed** structures. Do not claim that every documented foreign key exists in the live database.

Versioned migrations and tested integrity constraints remain necessary delivery work, not existing capabilities.

## Evidence in the project

- [Compose](../../docker-compose.yml) selects PostgreSQL 16 Alpine and a named `postgres_data` volume; no host database port is published.
- [application.properties](../../backend/odontosys-api/src/main/resources/application.properties) builds a JDBC URL from `DB_HOST/DB_PORT/DB_NAME`. Local defaults are `localhost:5434/odontosys`; Compose supplies `postgres:5432`.
- Spring Data JPA/Hibernate and the PostgreSQL driver are in [pom.xml](../../backend/odontosys-api/pom.xml).
- Defaults are `ddl-auto=update`, deferred SQL initialization, `spring.sql.init.mode=always`, and `continue-on-error=true`. No Flyway/Liquibase dependency or ordered migration directory was found.
- [schema.sql](../../backend/odontosys-api/src/main/resources/schema.sql) declares these 15 tables:

| Area | Tables and SQL keys/relationships |
|---|---|
| Identity | `roles`, `users`, `user_roles`, `refresh_tokens`, `password_reset_tokens`. UUID primary keys except the composite user/role key; role name, user email/document and refresh hash are unique. Token/user and user/role foreign keys use cascading deletion. |
| Patient and catalog | `patients`, `procedures`. UUID primary keys; unique patient document and procedure name; active flags and timestamps. |
| Scheduling | `dentist_schedules`, `availability_slots`, `appointments`. SQL references users, schedules, patients and slots. Deleting a schedule sets the slot's `schedule_id` to null. No unique active reservation rule is declared. |
| Clinical | `medical_records`, `medical_record_procedures`. Patient/dentist/optional appointment references and cascading record details; details preserve applied price and procedure name. |
| Financial | `invoices`, `invoice_details`, `payments`. Unique invoice number; patient/optional clinical record references; invoice details/payments cascade on physical invoice deletion. Monetary columns use `NUMERIC(12,2)`. |

### Differences between SQL, JPA and the documented model

Entity and mapper paths are under [persistence](../../backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/infrastructure/adapter/out/persistence).

| Difference | Actual evidence and consequence |
|---|---|
| More than the documented 15 tables | 16 JPA entity tables plus the `user_roles` join table describe 17 distinct tables. JPA adds `holidays` and `invoice_items`. This is a mapping inventory, not a query of a live database. |
| Two invoice detail models | `InvoiceJpaEntity.items` and `InvoicePersistenceMapper` use `InvoiceItemJpaEntity` / `invoice_items`. `InvoiceDetailJpaEntity` maps `invoice_details`, but is not used by that invoice collection/mapper. |
| Schedule fields | JPA includes `has_break`, `break_start_time` and `break_end_time`; the SQL schedule definition omits them. |
| Slot origin | SQL declares `schedule_id`; slot domain/JPA/mapper omit it, so generated slots do not record that origin. |
| Password-reset owner | SQL requires `user_id NOT NULL`; JPA allows null and `JpaPasswordResetTokenRepositoryAdapter.toJpa` never sets it. A SQL-first database can reject writes that a Hibernate-created database permits. |
| Foreign keys | Many associations are scalar UUID columns, not JPA relationships: appointment patient/dentist/slot, payment invoice, clinical references and others. Hibernate does not infer those SQL FKs from scalar IDs. |
| Initialization order | Hibernate creates/updates tables before deferred `CREATE TABLE IF NOT EXISTS`. Existing tables are not altered by those CREATE statements to gain their SQL-only FKs or columns. Actual constraints depend on initialization history. |
| Amount/state constraints | SQL has no CHECK constraints for positive amounts, valid time ranges or allowed status values; enum mappings and selected domain/DTO checks provide only part of the protection. |
| Uniqueness/concurrency | No reservation lock/version or unique slot reservation rule was found; invoice numbering is `count() + 1`, with no concurrency-safe sequence allocation in the service. |

### Initial data and durability

[data.sql](../../backend/odontosys-api/src/main/resources/data.sql) inserts three roles, three staff accounts, three example patients and six procedures using conflict-handling clauses. [DataInitializer.java](../../backend/odontosys-api/src/main/java/com/odontosys/odontosys_api/infrastructure/config/DataInitializer.java) creates missing demo accounts, **overwrites the passwords of existing demo accounts on every startup**, and supplies nine procedures only when the catalog is empty. These mechanisms differ; they are not one fully idempotent seed operation.

Domain/JPA mappings contain active flags, timestamps, historical procedure prices and decimal totals. A named volume is configured, but clean initialization, actual FKs, data retention after restart and backups are **NO VERIFICADO**.

## Alternatives considered

- **MongoDB:** [comparison-database.md](../../HU-01/comparison-database.md) discusses document flexibility; no MongoDB dependency, container or adapter exists.
- **Separate databases by service:** future extraction option; current cross-capability references would need an explicit migration.
- **Versioned SQL migrations:** a way to control PostgreSQL evolution, not an alternative database engine; absent in this implementation.

## Consequences

### Positive

- Fits the implemented relational data and BigDecimal-based money model.
- Supports foreign keys and local ACID transactions where explicitly defined.
- Reuses existing JPA adapters and database configuration.

### Negative

- Two drifting schema sources make the effective schema environment-dependent.
- Ignoring SQL initialization errors can conceal missing constraints or seed failures.
- Current use cases do not consistently use a single transaction for related writes.
- Overpayments are accepted; direct invoice edits can change `paid_amount` without payment ledger rows.
- Shared tables complicate later extraction and independent ownership.

## Implementation status

**PostgreSQL integration implemented in code/configuration; schema convergence partial; runtime NO VERIFICADO.** No PostgreSQL integration tests or Testcontainers suite were found. The H2 test properties are not PostgreSQL evidence; the POM lacks H2 and the context test does not activate the test profile.

Before release, reconcile SQL/JPA, inspect actual database constraints, validate reset-token persistence, test atomicity/concurrency, and verify restart durability. Any reservation constraint must preserve valid reuse after cancellation; blindly adding an unconditional unique slot key may prevent that lifecycle.

## Scope

This ADR records the selected database and observed persistence behavior. It does not execute migrations, change existing data, assert production durability or add a credit-account subsystem.

