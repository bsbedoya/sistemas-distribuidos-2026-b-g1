<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 04

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Brayan Smith Bedoya
- GITHUB_USER: bsbedoya
- TEAM: Di Lucca Dental Care & Technology
- SPRINT_GOAL: Align and consolidate the Di Lucca project documentation, define a coherent product and target architecture baseline, document the Week 04 Distributed Systems sessions, and record the relationship between the documentation, Figma mockup, and functional monolithic MVP.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-DLC-001 | Align the architecture and project documentation with the Di Lucca product | done | [Commit — docs: align Di Lucca architecture and project documentation](https://github.com/code-corhuila/dlc-docs/commit/67b2765cba284a2b4c52486ab9ee8d8dbc434688) |
| DOC-DLC-002 | Clean and focus the active Di Lucca documentation | done | [Commit — docs: update project documentation](https://github.com/code-corhuila/dlc-docs/commit/83f5cab89c28f0bcf6e3342f23dd8ec5dd6f1e11) |
| DOC-CLASS-001 | Document Week 04 Session 1 through a distributed-architecture diagram | done | [Commit — docs: Distributed Systems session 1](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/e4cbaaf7358c1691bfce6984f071b3cdb7b3919f) |
| DOC-CLASS-002 | Document Week 04 Session 2 through an architecture-planning diagram | done | [Commit — docs: Distributed Systems session 2](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/377d25dd2fe8c5bb34a8b3a6ff5c27ada3004c28) |
| DOC-REPORT-001 | Consolidate the weekly progress, commits, MVP, Figma, and contribution evidence | done | [Commit — docs(report): document weekly progress, commits, MVP, Figma, and individual contributions](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/3923b1e82bdfa252ace8f4b0dcb5677f8ce2920d) |

## 2. My individual contribution

During Week 04, I focused on consolidating the documentation of **Di Lucca Dental Care & Technology** and making the project artifacts describe the same dental-care product, business rules, service boundaries, and architectural direction.

### Project identity and documentation alignment

- I replaced the remaining generic or inconsistent project framing with the official **Di Lucca Dental Care & Technology** identity.
- I aligned **39 files** across governance, context, domain, product, requirements, architecture, data, UML, microservices, and UX/UI.
- I helped establish one coherent product baseline instead of maintaining disconnected descriptions in different repository folders.
- I aligned the documented internal actors with the approved operational roles:
  - `ADMINISTRATOR`
  - `DENTIST`
  - `SECRETARY_ASSISTANT`
- I clarified that patients are domain entities and recipients of dental services, but they are not authenticated platform users in the current scope.
- I reinforced the correlation rule requiring changes to roles, service boundaries, patient ownership, appointment policies, and domain events to be updated consistently across the affected artifacts.

### Governance and documentation standards

- I updated the governance baseline so that the rules apply specifically to Di Lucca.
- I aligned the Definition of Ready and Definition of Done with the project's actors, appointment rules, service contracts, data models, and domain events.
- I improved the documentation rules to identify canonical sources for scope, vocabulary, domain boundaries, invariants, and events.
- I refined the Git conventions for documentation-sensitive changes and Conventional Commit usage.
- I aligned security documentation with the approved roles and the internal-clinic operating model.
- I updated the microservice documentation standard to prevent contradictions between global domain definitions and service-specific transport details.

### Context, domain, and business rules

- I aligned the project overview, scope, glossary, domain map, entities, rules, and domain-event catalog.
- I clarified the principal business areas represented by the product:
  - Identity and Access.
  - Appointment Scheduling.
  - Clinical Care and Patient Records.
  - Billing and Payments.
- I aligned the ownership of patient information with the clinical context and removed the unsupported idea of an independent patient business microservice.
- I reviewed appointment scheduling rules, service responsibilities, and cross-context relationships so they remained consistent throughout the documentation.
- I aligned the documented business events and invariants with the product scope rather than treating the database structure as the domain model.

### Product, requirements, and roadmap

- I created or aligned the product discovery brief, problem framing, product vision, product backlog, and roadmap.
- I connected the product definition with the needs of an internal dental-clinic management platform.
- I updated the requirements and traceability material so that user stories could be related to business needs, architecture, data, APIs, and UX/UI evidence.
- I helped distinguish the validated monolithic MVP from the future target architecture, avoiding the claim that all planned microservices were already implemented.

### Architecture and microservice baseline

- I aligned the architecture overview with the current Di Lucca scope.
- I documented a target architecture based on clear bounded contexts, explicit contracts, and independent data ownership.
- I defined the approved baseline of four business microservices:
  - `auth-service` — Identity and Access.
  - `appointments-service` — Appointment Scheduling.
  - `clinical-service` — Clinical Care and Patient Records.
  - `billing-service` — Billing and Payments.
- I clarified that the API Gateway and analytics are supporting architectural components rather than additional business microservices.
- I added **ADR-002: Polyglot Persistence** to record the rationale and consequences of selecting persistence technology according to each service's needs.
- I aligned the service catalog, architecture diagrams, and data documentation with the same service boundaries.
- I reinforced the rule that one service must not directly access another service's database; integration must occur through APIs or events.

### Data, UML, and UX/UI

- I aligned the data documentation with the approved service ownership model.
- I updated the UML diagram index and supporting architecture-diagram source.
- I aligned the UX/UI documentation with the product's actual workflows and roles.
- I updated the design system and navigation map.
- I added a wireframe catalog covering the main operational flows of the platform.
- I connected the documentation with the Figma mockup used as the visual and interaction reference for Di Lucca.

### Documentation cleanup and consistency review

- I completed a second documentation pass covering **22 files**.
- I removed obsolete, duplicated, or oversized content that no longer reflected the approved scope.
- I reduced contradictions in context, domain, requirements, architecture, data, UML, and service documentation.
- I kept the useful architectural decisions while focusing the active documents on the product baseline that the team had approved.

### Week 04 class documentation

- I created the **Distributed Systems Session 1** diagram about distributed architecture styles, boundaries, communication, data ownership, microservice trade-offs, and common mistakes.
- I documented why microservices should be divided by business capability or bounded context rather than by technical layers.
- I represented the distributed-monolith anti-pattern caused by shared databases and coupled deployments.
- I documented the recommendation to begin with a well-modularized monolith and extract a service only when a real boundary and an independent scaling or deployment need coincide.
- I created the **Distributed Systems Session 2** diagram about the architecture-planning loop: refine, decide, record, and slice.
- I documented how to map bounded contexts, select between a modular monolith and service extraction, record the decision in an ADR, and transform it into sprint backlog items.
- I included the essential ADR sections: Context, Decision, Alternatives, and Consequences.
- I emphasized that sprint stories need testable acceptance criteria and must fit the sprint scope.

### Weekly evidence report

- I prepared a **473-line bilingual weekly progress report** covering the August 23–30 reporting period.
- I documented the relationship among three complementary deliverables:
  1. Project documentation, which defines business intent, rules, requirements, traceability, and target architecture.
  2. The Figma mockup, which represents the principal visual and interaction flows.
  3. The monolithic MVP, which validates the main workflows before migration toward the target distributed architecture.
- I consolidated commit evidence from the documentation and MVP repositories while distinguishing individual authorship from team results.
- I recorded that the MVP uses an Angular frontend, a Spring Boot backend, and PostgreSQL, while the documentation describes the intended future architectural evolution.

### Main work delivered

- **Di Lucca documentation alignment:** 39 files updated, with 2,446 additions and 2,191 deletions.
- **Documentation cleanup:** 22 files reviewed, with obsolete or duplicated material removed.
- **Session 1 diagram:** distributed architectures, boundaries, microservices, data ownership, and trade-offs.
- **Session 2 diagram:** bounded contexts, architectural decision path, ADRs, and sprint-backlog slicing.
- **Weekly progress report:** consolidated documentation, MVP, Figma, commit, and contribution evidence in English and Spanish.

## 3. Blockers and risks

- Some legacy references to **OdontoSys** may still exist in repository names, technical paths, historical artifacts, or visual assets and must not be confused with the current official product name, **Di Lucca Dental Care & Technology**.
- The current implementation is a monolithic MVP, while the documentation also describes a future microservice architecture; this distinction must remain explicit.
- Architecture, requirements, data models, service contracts, UX/UI, and implementation can become inconsistent if they are updated independently.
- The REST and event contracts between future services still require formal implementation and verification evidence.
- The service boundaries and data-ownership rules must be preserved when the monolith begins to evolve toward independently deployable services.
- The traceability matrix requires continuous updates as implementation and testing evidence becomes available.
- The documentation changes were committed directly as documentation work; no per-environment HU branch or pull-request evidence is included in this weekly status.
- The week focused mainly on documentation, diagrams, and project consolidation, so no personal unit or integration test evidence is claimed here.

## 4. Plan for next week

- Replace remaining active references to the previous project name with **Di Lucca Dental Care & Technology**, without rewriting historical evidence unnecessarily.
- Validate that governance, scope, domain, requirements, architecture, data, APIs, microservices, and UX/UI use the same roles, rules, and service boundaries.
- Keep the monolithic MVP and the target distributed architecture clearly differentiated in all project documentation.
- Refine the REST API contracts for the principal business capabilities.
- Define and version the integration events required for communication between future services.
- Extend the requirements traceability matrix with implementation, contract, and test evidence.
- Validate the documented business rules against the functional MVP flows.
- Begin translating the approved architectural boundaries into implementation tasks with testable acceptance criteria.
- Add unit, integration, and end-to-end evidence as the selected user stories are implemented or refined.
- Keep the Week 04 README and evidence links synchronized with the repository history.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (`hu-xxx-dev -> develop`, ...)
- [ ] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

### Compliance notes

- The five reported contributions have direct commit evidence and use documentation-oriented Conventional Commit messages.
- The work focused on documentation alignment, architecture decisions, visual summaries, and evidence consolidation.
- No pull request or per-environment branch is claimed because the provided evidence consists of commits.
- Acceptance criteria were studied and referenced in the architecture-planning material, but this report does not claim implementation-level verification for each criterion.
- No personal test implementation is claimed for this reporting period.
- Service boundaries, data ownership, and dependency rules were documented according to DDD and hexagonal-architecture principles.
- No secrets were introduced by the documented contributions.

## 6. Evidence links

### Di Lucca documentation

- [Commit — align Di Lucca architecture and project documentation](https://github.com/code-corhuila/dlc-docs/commit/67b2765cba284a2b4c52486ab9ee8d8dbc434688)
- [Commit — update and clean project documentation](https://github.com/code-corhuila/dlc-docs/commit/83f5cab89c28f0bcf6e3342f23dd8ec5dd6f1e11)
- [Di Lucca documentation repository](https://github.com/code-corhuila/dlc-docs)

### Week 04 class documentation

- [Commit — Distributed Systems Session 1](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/e4cbaaf7358c1691bfce6984f071b3cdb7b3919f)
- [Commit — Distributed Systems Session 2](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/377d25dd2fe8c5bb34a8b3a6ff5c27ada3004c28)
- [Week 04 evidence folder](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/04-week/hu-status)

### Weekly report and supporting project evidence

- [Commit — weekly progress, commits, MVP, Figma, and individual contributions](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/3923b1e82bdfa252ace8f4b0dcb5677f8ce2920d)
- [Monolithic MVP repository](https://github.com/DanielPerez1822/di-lucca-mvp)
- [Di Lucca Figma mockup](https://www.figma.com/design/HB2cfqrV1HFzARClICuitz/OdontoSys?node-id=0-1&t=B9PUkD44i8l2u72o-1)

### Evidence-scope note

- The documentation commits, diagrams, and weekly report listed above are my direct contributions.
- MVP commits created by other team members are referenced only as team context and are not presented as my individual implementation work.
