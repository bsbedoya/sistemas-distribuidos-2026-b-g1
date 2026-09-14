<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       06-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 06

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Brayan Smith Bedoya Montealegre
- GITHUB_USER: bsbedoya
- TEAM: Di Lucca Dental Care & Technology
- SPRINT_GOAL: Close Corte 1 based on the MVP and mockup presentation results, analyze the professor's feedback, identify documentation and architectural improvement areas, and prepare the Di Lucca project for Corte 2 by defining the next documentation, configuration, environment, and orchestration activities.
<!-- CONFIG-END -->

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-W06-001 | Corte 1 final evaluation and feedback analysis | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/05-week/hu-status/HU-03 |
| HU-W06-002 | Backend testing and QA evidence review | done | https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/blob/main/05-week/hu-status/BACKEND_TEST_DOCUMENTATION.md |
| HU-W06-003 | Review of MVP and mockup delivery results | done | https://github.com/code-corhuila/di-lucca.git |
| HU-W06-004 | UX/UI documentation improvement planning | doing | https://github.com/code-corhuila/dlc-docs/tree/main/12-ux-ui |
| HU-W06-005 | Corte 2 environment and orchestration planning | doing | https://github.com/code-corhuila/dlc-docs |

## 2. My individual contribution

- Participated in the formal closure of Corte 1 after the MVP and mockup presentations were completed and evaluated.

- Reviewed the professor's final feedback for the Di Lucca project and analyzed the partial project results in order to identify the weakest areas that require improvement during Corte 2.

- Reviewed the relationship between the final project evaluation and the seven technical pillars considered in the assessment: DDD, TDD, SDD, SOLID, Clean Code, Design Patterns, and Hexagonal Architecture.

- Analyzed the final Di Lucca evaluation results, where the project obtained a documentation score of approximately `2.79`, a presentation score of `4.2`, and a partial project result of approximately `3.26`.

- Identified that the main improvement opportunity is not the oral presentation itself, but the quality, consistency, traceability, and technical depth of the documentation repository.

- Reviewed the documentation produced during Corte 1, especially the `12-ux-ui` section, where the MVP evidence, mockup, Figma material, navigation information, and UX/UI artifacts were organized.

- Identified communication and delivery problems related to the required language, interpretation of academic requirements, expected repository structure, and the exact format requested for some documentation artifacts.

- Reviewed the backend testing documentation generated during Week 05 as technical evidence of the QA work carried out before the Corte 1 delivery.

- Verified that the backend QA report documented 112 executed tests with 112 successful results, no failures, no errors, no skipped tests, and a successful Maven build.

- Reviewed the documented backend quality findings, including limited branch coverage, the absence of PostgreSQL integration testing, limited role-based authorization coverage, and the need for additional concurrency and transaction tests.

- Used the Week 05 deliverables as the evidence baseline for Week 06 instead of duplicating technical evidence that had already been submitted.

- Reviewed the previous weekly report and confirmed that the final Corte 1 MVP validation, QA process, documentation audits, Pull Request reviews, merge validations, branch workflow, Docker checks, and UX/UI organization were completed during the previous sprint.

- Analyzed the professor's feedback as input for the new Corte 2 planning phase.

- Began planning the improvement of the documentation repository, with priority on `12-ux-ui` and the remaining documentation folders that will be required as the distributed architecture evolves.

- Studied the Week 6 topics related to Docker Compose, orchestration, environments, configuration strategy, health checks, environment variables, secrets management, and branch-to-environment mapping.

- Reviewed how these Week 6 concepts should be incorporated into the Di Lucca project during Corte 2.

- Defined that future project documentation should clearly distinguish the `develop`, `qa`, and `production` environments.

- Identified the need to maintain the same deployable artifact between environments while changing only the corresponding configuration.

- Identified the need to document a configuration matrix containing common environment variable names and environment-specific values.

- Reinforced the requirement that secrets must not be stored in the repository and that configuration should be injected through environment variables or a secure secrets mechanism.

- Reviewed the need for `.env.example` files and startup validation for required environment variables to reduce configuration drift.

- Reviewed Docker Compose orchestration concepts, especially shared networks, service-name DNS, named volumes, `healthcheck`, and `depends_on` with `condition: service_healthy`.

- Identified that service startup order alone is not sufficient and that dependent services must also implement retry or backoff mechanisms when required.

- Began translating the professor's feedback into actionable documentation and technical improvement tasks for Corte 2 instead of treating the Corte 1 evaluation only as a final grade.

## 3. Blockers and risks

- During the Corte 1 delivery, one of the main difficulties was the interpretation of the exact documentation requirements requested by the professor.

- There were communication problems related to the language expected in some deliverables and how the evidence had to be organized inside the repository.

- The `12-ux-ui` section needs additional improvement so that the mockup, Figma evidence, navigation documentation, MVP screens, and UX/UI decisions are clearly connected and traceable.

- The professor's evaluation showed that the documentation component was considerably weaker than the oral presentation component.

- This creates a risk for Corte 2 because the next evaluation will include deeper code-level and distributed-system requirements.

- The documentation repository may contain technically correct information that is not yet organized with enough traceability between requirements, architecture, implementation, tests, and evidence.

- There is a risk of configuration drift if different services or environments use inconsistent environment variable names.

- There is a risk of unstable Docker Compose startup if service dependencies are controlled only through `depends_on` without readiness health checks.

- There is a risk of exposing sensitive information if secrets are placed directly inside Compose files, Dockerfiles, repository files, or committed `.env` files.

- Current backend automated testing is positive, but some important quality gaps remain, including limited branch coverage, missing PostgreSQL integration tests, limited authorization matrix testing, and limited concurrency validation.

- Future migration from the monolithic MVP toward a distributed architecture may introduce additional complexity in service communication, persistence, environment configuration, observability, and deployment.

## 4. Plan for next week

- Improve the `12-ux-ui` documentation based on the professor's feedback and the partial Corte 1 evaluation results.

- Review the structure and content of the remaining documentation folders in `dlc-docs` and continue completing the documentation required for Corte 2.

- Strengthen traceability between the MVP implementation, user stories, acceptance criteria, architecture, API contracts, tests, data model, and UX/UI evidence.

- Document the three project environments:
  - `develop`
  - `qa`
  - `production`

- Create or update a configuration matrix describing the required environment variables, their purpose, and their differences between environments.

- Verify that all environment-specific configuration is externalized and that no environment-specific values are hard-coded in the application.

- Verify the use of `.env.example` files without exposing real secrets.

- Review the Docker Compose structure and confirm:
  - shared service networks;
  - service-name DNS;
  - named volumes;
  - database persistence;
  - health checks;
  - startup dependency conditions;
  - environment variable injection.

- Review the branch-to-environment strategy:
  - `hu-xxx-dev -> develop`
  - `hu-xxx-qa -> qa`
  - `hu-xxx-main -> main`

- Define MVP 2 orchestration stories with clear and testable acceptance criteria.

- Prepare the project so that all required services can eventually be started with a single `docker compose up`.

- Review service boundaries and determine which components will evolve from the current monolithic MVP toward the planned microservices architecture.

- Continue improving backend quality evidence, especially PostgreSQL integration testing, authorization coverage, concurrency scenarios, and branch coverage.

- Use the Corte 1 feedback as a measurable improvement baseline for Corte 2.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (`hu-xxx-dev -> develop`, ...)
- [x] Testable acceptance criteria
- [x] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- Week 05 HU-03 evidence:
  https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/05-week/hu-status/HU-03

- Backend QA and test documentation:
  https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/blob/main/05-week/hu-status/BACKEND_TEST_DOCUMENTATION.md

- Week 05 final weekly report:
  https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/blob/main/05-week/hu-status/WEEKLY_DI_LUCCA_2026-09-01.pdf

- Di Lucca MVP repository:
  https://github.com/code-corhuila/di-lucca.git

- Di Lucca documentation repository:
  https://github.com/code-corhuila/dlc-docs

- UX/UI documentation:
  https://github.com/code-corhuila/dlc-docs/tree/main/12-ux-ui

## 7. Corte 1 closing result

Corte 1 was formally completed this week after the MVP and mockup presentations were evaluated.

My final individual grade for **Distributed Systems G1 - Corte 1** was:

**4.9 / 5.0**

The grade was composed of the following evaluated components:

| Component | Result |
|---|---:|
| Project - release and presentation (60%) | 3.80 |
| Weekly sprint `c1-hu` (30%) | 5.00 |
| `c1-rf` (5%) | 5.00 |
| Self-assessment and peer-assessment (5%) | 5.00 |
| Appreciative bonus | 0.60 |
| **Final Corte 1 grade** | **4.9** |

The weekly sprint component received a strong result because the five weekly `hu-status` reports contained real and technically relevant individual contributions.

The project component showed an important difference between documentation quality and presentation performance. This result confirms that the main improvement focus for Corte 2 must be documentation quality, traceability, consistency, and alignment with the implemented system.

The professor also announced that Corte 2 will use more precise evaluation criteria and milestone-based delivery routes. Therefore, future work must be documented and implemented with stronger traceability between requirements, code, architecture, tests, environments, and evidence.

## 8. Week 06 technical learning

During Week 06, the main academic topics were Docker Compose orchestration and environment/configuration planning.

The most relevant concepts for the Di Lucca project are:

- A distributed system should be reproducible using a shared Docker Compose topology.
- Services should communicate using service names instead of hard-coded IP addresses.
- Database data should be stored in named volumes.
- `depends_on` only controls startup order unless readiness conditions are explicitly configured.
- Critical dependencies should expose health checks.
- Dependent services should use retry or backoff mechanisms when appropriate.
- The same application image should be promoted across `develop`, `qa`, and `production`.
- Only configuration should change between environments.
- Environment variables must use consistent names across environments.
- Secrets must never be committed to Git.
- `.env.example` should document required configuration without containing real credentials.
- Each environment should map to the corresponding repository branch and promotion flow.
- Corte 2 orchestration work should be divided into small user stories with testable acceptance criteria.

## 9. Week 06 final result

Week 06 represented the transition between the completed Corte 1 and the beginning of Corte 2.

The week was mainly focused on evaluation analysis, feedback interpretation, identification of documentation weaknesses, and preparation of the next distributed-system activities.

The Di Lucca MVP and mockup were already delivered during the previous sprint, so no attempt was made to duplicate that work. Instead, the existing Week 05 evidence was reused as the technical baseline for this week's evaluation and improvement planning.

The professor's feedback showed that the project has a stronger presentation result than documentation result. Therefore, documentation quality will become a central improvement objective during Corte 2.

The `12-ux-ui` section is one of the first documentation areas to be improved because it contains critical evidence related to the mockup, Figma design, navigation, user experience, and final MVP interface.

At the same time, Week 06 introduced the technical concepts required for the next phase: Docker Compose orchestration, health checks, environment configuration, secrets management, configuration consistency, branch-to-environment mapping, and artifact promotion.

These concepts will be incorporated into the next Di Lucca sprint so that the project can evolve from the Corte 1 MVP baseline toward a more reliable distributed-system architecture for Corte 2.