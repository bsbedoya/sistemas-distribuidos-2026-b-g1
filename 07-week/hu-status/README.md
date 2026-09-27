# Weekly Status - Week 07

- FULL_NAME: Brayan Smith Bedoya Montealegre
- GITHUB_USER: bsbedoya
- TEAM: Di Lucca Dental Care & Technology
- SPRINT_GOAL: Strengthen the consistency and traceability of the project documentation, adopt the new child-branch and Pull Request review workflow, and continue preparing the documentation and service contracts for MVP 2 integration.

## 1. User stories worked this week

| **HU ID** | **Title** | **Status (todo/doing/done)** | **Evidence (PR or commit URL)** |
| ---------- | --------- | ---------------------------- | ------------------------------- |
| N/A | Project context documentation alignment | done | [Commit 8ce7b89](https://github.com/code-corhuila/dlc-docs/commit/8ce7b893d02907fbd2f872a00007a84676c80071) |
| N/A | Child branch and Pull Request review workflow | done | [Pull Request #9](https://github.com/code-corhuila/dlc-docs/pull/9) |
| N/A | Reviewed documentation integration into main | done | [Merge commit 2f35310](https://github.com/code-corhuila/dlc-docs/commit/2f35310a23197d626d7f1aad1b34f022b81b1b78) |

## 2. My individual contribution

- I adapted my documentation workflow to the new contribution rules established for the `dlc-docs` repository. Documentation changes are no longer applied directly to the parent branch. Instead, the work is developed in a dedicated child branch and submitted through a Pull Request before being integrated into `main`.

- I worked through the `docs/context` branch and submitted the changes through Pull Request #9, following the `docs/context` → `main` workflow. The Pull Request was reviewed and approved before its integration into the main documentation branch.

- One of the main activities of the week was learning and applying this controlled Git workflow. This required managing the child branch, creating Conventional Commits, opening the Pull Request, receiving review feedback, and waiting for approval before merging the documentation.

- I updated and aligned the project context documentation, especially `01-context/glossary.md`, `01-context/overview.md`, and `01-context/scope.md`.

- The documentation was improved to clarify the responsibilities and boundaries of the project's services. The administrative patient profile responsibility was aligned with `patients-service`, while clinical records remain under `clinical-service`.

- The role of `auth-service` was also clarified as a transversal IAM service, while the four business services remain Patients, Appointments, Clinical, and Billing.

- Service ownership, permissions, persistence responsibilities, and integration rules were reviewed to reduce contradictions between documents and improve correlation across the repository.

- The documentation was aligned with the current persistence strategy: Auth, Patients, Appointments, and Billing use PostgreSQL with logically separated schemas, while Clinical uses MongoDB.

- Additional restrictions and responsibilities were documented for patient administration and access to clinical information, helping maintain separation between administrative and clinical data.

- I continued reviewing other project documentation folders to detect inconsistencies, missing information, duplicated responsibilities, and terminology that could generate contradictions as the repository grows.

- During the week, additional documentation and missing sections continued to be incorporated into the project. The objective is to keep the repository structured, sequential, consistent, and aligned with the architecture and requirements of Di Lucca Dental Care & Technology.

- The Pull Request also generated written review feedback. The review identified improvements related to traceability of the new `patients-service` boundary, consistency between the PR description and changed files, verification of glossary definitions, and service naming conventions. These observations remain important for future corrections and for the project checkpoint defense.

- Week 7 classes also introduced inter-service communication using REST, gRPC, and asynchronous messaging. The sessions covered synchronous versus asynchronous communication, RabbitMQ-style messaging, delivery semantics, idempotent consumers, resilience, and the risks of long synchronous dependency chains.

- The planning session covered versioned contracts and contract testing, including OpenAPI, `.proto` files, event schemas, backward compatibility, API versioning, deprecation, and consumer-driven contract testing with Pact.

- These concepts are directly related to the next stage of the project because the microservices must begin interacting through explicit and versioned contracts instead of relying only on documentation assumptions.

- Two user-manual deliverables were also communicated during the week. They have not yet been completed/submitted and remain part of the pending documentation work.

## 3. Blockers and risks

- The new child-branch and Pull Request workflow requires additional coordination because changes must now pass through review before they can be integrated into `main`.

- Documentation developed by different team members can introduce inconsistencies if service ownership, terminology, architectural decisions, or domain boundaries are modified independently.

- The review of Pull Request #9 identified that changes to service boundaries should have stronger traceability to an ADR or user story. This must be considered in future documentation updates.

- The review also detected an additional empty `git` entry in the changed files that was not described in the Pull Request scope. Similar unrelated files should be avoided in future commits.

- Service naming conventions still require review to ensure that logical service names and actual repository/component names follow the standards defined for the course.

- Some documentation sections are still under development, so changes in one folder may require updates in related context, domain, architecture, data, API, and diagram documents.

- The two user-manual deliverables remain pending.

- Future service integration may generate breaking changes if REST endpoints, event schemas, or service responsibilities are implemented without versioned contracts.

## 4. Plan for next week

- Continue using dedicated child branches for documentation work and submit every relevant change through a Pull Request before merging into `main`.

- Review and address the recommendations received in Pull Request #9.

- Improve traceability between domain-boundary changes, ADRs, requirements, and user stories.

- Continue auditing the documentation for consistency between context, domain, requirements, architecture, data, API contracts, diagrams, and microservice-specific documentation.

- Review service naming conventions and distinguish clearly between logical business-service names and actual repository/component names.

- Complete or advance the missing project documentation and the pending user manuals.

- Begin formalizing and reviewing versioned REST and event contracts using OpenAPI and event schemas.

- Determine which service interactions require synchronous REST communication and which ones should use asynchronous messaging.

- Ensure RabbitMQ consumers are designed as idempotent consumers because the documented delivery model is at-least-once.

- Define backward-compatibility rules so existing consumers are not silently broken by contract changes.

- Prepare the documentation, contracts, tests, and integration evidence required for MVP 2.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-change child branch + PR before integration into `main`
- [ ] Testable acceptance criteria - to be strengthened for MVP 2 integration stories
- [ ] Tests added/updated (unit / integration) - this week's main contribution focused on documentation and planning
- [x] DDD / hexagonal boundaries respected and reviewed in the documentation
- [x] No secrets; config via environment variables
- [x] Documentation changes reviewed before merge
- [x] Instructor/reviewer feedback recorded and considered
- [ ] Consumer-driven contract testing - planned for the integration stage

## 6. Evidence links

- Documentation repository: [code-corhuila/dlc-docs](https://github.com/code-corhuila/dlc-docs)
- Documentation update commit: [8ce7b89 - docs(context): update project overview, scope and glossary](https://github.com/code-corhuila/dlc-docs/commit/8ce7b893d02907fbd2f872a00007a84676c80071)
- Reviewed Pull Request: [PR #9 - docs(context): update project overview, scope and glossary](https://github.com/code-corhuila/dlc-docs/pull/9)
- Merge into `main`: [2f35310 - Merge pull request #9 from code-corhuila/docs/context](https://github.com/code-corhuila/dlc-docs/commit/2f35310a23197d626d7f1aad1b34f022b81b1b78)