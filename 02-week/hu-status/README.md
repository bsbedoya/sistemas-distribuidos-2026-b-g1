<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       02-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 02

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Brayan Smith Bedoya
- GITHUB_USER: bsbedoya
- TEAM: OdontoSys
- SPRINT_GOAL: Study and document distributed architecture concepts, architecture decision criteria, bounded contexts, ADRs, agile methodologies, Scrum, Kanban, and user stories to support the planning and architectural definition of OdontoSys.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-00 | Document Distributed Systems Session 1 concepts through a summary diagram | done | YOUR_COMMIT_URL |
| HU-00 | Document Distributed Systems Session 2 concepts through a summary diagram | done | YOUR_COMMIT_URL |
| HU-00 | Study and document Agile, Scrum, Kanban, and User Stories concepts | done | YOUR_COMMIT_URL |

## 2. My individual contribution

During Week 02, I worked on the study and documentation of distributed systems architecture and agile planning concepts that will support the development of **OdontoSys**.

My contributions included:

- Documented the concepts studied during **Distributed Systems Session 1** through a structured visual summary.
- Reviewed different distributed architecture styles, including client-server, peer-to-peer, layered/N-tier, SOA, microservices, and event-driven architectures.
- Studied the importance of defining architectural boundaries according to business capabilities and bounded contexts instead of technical layers.
- Documented the principle that each microservice should own its data and communicate with other services through explicit contracts instead of sharing databases.
- Reviewed the advantages and trade-offs of microservices, including independent deployment and scaling, fault isolation, operational complexity, network failures, distributed tracing, and versioned contracts.
- Studied the distributed monolith anti-pattern and the risks of splitting a system into services while keeping a shared database or tightly coupled deployment.
- Documented the recommendation to begin with a well-modularized architecture and extract services only when a real business boundary and an independent scaling or deployment need exist.
- Reviewed common architecture mistakes such as splitting services by technical layers, sharing databases between services, and introducing messaging infrastructure without a real asynchronous communication requirement.

During **Distributed Systems Session 2**, I continued working on architecture planning and decision-making:

- Studied how to identify and map **bounded contexts** based on business language, responsibilities, and data ownership.
- Reviewed the architecture decision path used to determine whether a capability should remain inside a modular monolith or be extracted as an independent service.
- Studied how architectural decisions should be documented using an **Architecture Decision Record (ADR)**.
- Reviewed the main sections of an ADR: Context, Decision, Alternatives, and Consequences.
- Studied how architectural decisions can be transformed into sprint backlog items and user stories with testable acceptance criteria.
- Reviewed the importance of defining service extraction based on real architectural evidence rather than choosing microservices only because of system size.

I also worked on agile planning concepts:

- Studied **Agile principles**, including frequent delivery, teamwork, continuous communication, adaptation to change, customer focus, and continuous improvement.
- Reviewed **Scrum** as an agile framework and the responsibilities of the Product Owner, Scrum Master, and Developers.
- Studied how work is organized into short iterations called **Sprints**.
- Reviewed the structure of a **User Story** using the format: `As a <role>, I want <goal>, so that <benefit>`.
- Studied the **3 Cs of User Stories**: Card, Conversation, and Confirmation.
- Reviewed the **INVEST** criteria for evaluating user stories: Independent, Negotiable, Valuable, Estimable, Small, and Testable.
- Studied how acceptance criteria provide testable conditions to determine when a user story is complete.
- Reviewed the User Story workflow from an initial idea to the Product Backlog, Sprint, development, and completion.
- Studied **Kanban boards** as a visual mechanism for tracking work through stages such as To Do, In Progress, Review, and Done.

### Main work delivered

- **Distributed Systems Session 1:** Created a visual summary covering distributed architecture styles, microservice boundaries, data ownership, trade-offs, and common architecture mistakes.
- **Distributed Systems Session 2:** Created a visual summary covering bounded contexts, architecture decision criteria, ADRs, and the transformation of architectural decisions into sprint backlog items.
- **Agile, Scrum, Kanban and User Stories:** Studied and documented agile planning concepts that can be applied to the organization of the OdontoSys backlog and future development work.

## 3. Blockers and risks

- The concepts studied during the week still need to be applied to concrete architectural decisions in **OdontoSys**.
- The bounded contexts and service boundaries of the project must continue to be validated against real business responsibilities.
- There is a risk of creating unnecessary microservices if services are divided by technical layers instead of business capabilities.
- Communication contracts between future services still need to be formally defined.
- Architectural decisions should be documented through ADRs so that the reasons, alternatives, and consequences remain traceable.
- User stories for future implementation must include clear and testable acceptance criteria.
- The project documentation must remain synchronized with the architecture and backlog as they evolve.

## 4. Plan for next week

- Apply the distributed architecture concepts studied during Week 02 to **OdontoSys**.
- Refine the project's bounded contexts according to business responsibilities and data ownership.
- Continue validating which capabilities should remain together and which could become independent microservices.
- Document relevant architectural decisions using ADRs.
- Define communication contracts between the main OdontoSys components.
- Apply the User Story format and INVEST criteria when refining the project backlog.
- Define testable acceptance criteria for the user stories selected for implementation.
- Use the project board to track user stories through the corresponding workflow.
- Keep the architecture, backlog, and technical documentation synchronized with the team's decisions.

## 5. Compliance self-check

- [x] Conventional Commits used for documentation changes.
- [ ] Per-environment HU branch + PR to the corresponding environment.
- [x] User Story and acceptance criteria concepts studied and documented.
- [ ] Tests added/updated (unit / integration).
- [x] DDD / bounded context principles studied and considered.
- [x] Microservice data ownership and isolation principles studied.
- [x] ADR structure and architecture decision process studied.
- [x] No secrets added to the documentation.

### Notes on the unchecked items

- This week's work focused mainly on class documentation, architecture concepts, and agile planning.
- No production implementation was completed as part of these activities, so unit and integration tests were not required.
- Per-environment HU branches and pull requests were not part of the documented work for this week.
- The architecture concepts studied this week will be applied progressively to the OdontoSys implementation and documentation.

## 6. Evidence links

- Distributed Systems Session 1 diagram: `Distributed Systems session 1.png`
- Distributed Systems Session 2 diagram: `Distributed Systems session 2.png`
- Agile, Scrum, Kanban and User Stories presentation: `Agile, Scrum, Kanban and User Stories presentation.pdf`
- Distributed Systems Session 1 commit: 9774de487b186637632fbd74e41bfd2275c253d2
- Distributed Systems Session 2 commit: 9774de487b186637632fbd74e41bfd2275c253d2
- Agile presentation commit: 9774de487b186637632fbd74e41bfd2275c253d2