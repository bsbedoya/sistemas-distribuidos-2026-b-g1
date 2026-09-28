
# Weekly Status - Week 08

- FULL_NAME: Brayan Smith Bedoya Montealegre
- GITHUB_USER: bsbedoya
- TEAM: Di Lucca Dental Care & Technology
- SPRINT_GOAL: Establish, validate, and harden the Appointments backend repository foundation under Hexagonal Architecture, prepare CI/CD and local infrastructure for future HU implementation, and align Clinical persistence, care-closing, and cross-domain documentation with the current Di Lucca architecture.

## 1. User stories worked this week

| **HU ID** | **Title** | **Status (todo/doing/done)** | **Evidence (PR or commit URL)** |
| ---------- | --------- | ---------------------------- | ------------------------------- |
| HU-APT-001 | Schedule an available dentist | doing | https://github.com/code-corhuila/dlc-appointments-api/commit/3a1e86c0aff69fc5241dd28b6e0b1db12d55de56 |
| HU-APT-003 | View the dentist calendar and appointment information | doing | https://github.com/code-corhuila/dlc-appointments-api/commit/a718b9e49de883657e43e5b04f4034da09155282 |
| HU-CLN-002 | Document clinical care and preserve longitudinal history | doing | https://github.com/code-corhuila/dlc-docs/commit/51aa32a2690b3c76e67e65c1d081ac3fb6f2ce5b |
| HU-CLN-003 | Record materials and justified unforeseen clinical needs | doing | https://github.com/code-corhuila/dlc-docs/pull/26 |
| HU-XCT-001 | Coordinate reliable clinical care closure across services | doing | https://github.com/code-corhuila/dlc-docs/commit/8372b09e90696ee27fd92d4e4bad186c555c0500 |

The Appointments stories remain in `doing` because this week focused mainly on creating and hardening the executable service foundation required before implementing scheduling, availability, overlap, calendar, lifecycle, and persistence business rules.

The Clinical and cross-context stories also remain in `doing` because this week's contribution focused on persistence rules, ownership, contracts, schema evolution, care-closing coordination, and OpenAPI alignment rather than full executable Clinical implementation.

## 2. My individual contribution

- I performed most of the repository foundation and configuration work for `dlc-appointments-api`.

- I created the initial Maven multi-module structure for the Appointments service using:
  - Java 21.
  - Spring Boot 3.5.
  - Maven.
  - `appointments-core`.
  - `appointments-adapters`.
  - `appointments-app`.

- I configured the root Maven reactor and the dependency relationship between modules.

- I kept `appointments-core` independent from Spring and infrastructure dependencies according to the Hexagonal Architecture rules defined for the project.

- I created the initial Spring Boot application entry point in `appointments-app`.

- I created the initial application configuration/composition root.

- I created the first HTTP adapter in `appointments-adapters`.

- I implemented the technical health endpoints:
  - `GET /health`
  - `GET /health/ready`

- I locally validated both endpoints and confirmed responses equivalent to:
  - `/health` -> service status `UP`.
  - `/health/ready` -> service status `READY`.

- I successfully validated the complete Maven reactor using:
  - `mvn clean verify`.

- I also validated the executable Spring Boot JAR locally before containerization.

### Repository configuration

- I replaced the original generic LMS Library README template with documentation specific to `dlc-appointments-api`.

- I documented:
  - the purpose of the Appointments bounded context;
  - the Java/Spring Boot/Maven technology stack;
  - the three-module Hexagonal Architecture structure;
  - build instructions;
  - local execution instructions;
  - health endpoints;
  - environment configuration;
  - database ownership;
  - branch and promotion rules.

- I added `.gitignore` rules for:
  - Maven build directories;
  - IDE files;
  - local environment files;
  - private keys/certificates;
  - logs;
  - operating-system-generated files;
  - Java-generated files.

- I explicitly allowed `.env.example` while ensuring the real `.env` file remains ignored.

- I created `.env.example` for local application, PostgreSQL, and RabbitMQ configuration.

### Pull Request governance

- I created the repository Pull Request template.

- The template includes sections for:
  - User Story reference.
  - Description of changes.
  - Reason for the change.
  - Testing evidence.
  - Promotion trace.
  - Compliance checklist.

- The checklist verifies important project rules such as:
  - changes belonging to the Appointments bounded context;
  - no committed secrets;
  - no database migration ownership inside the API repository;
  - `appointments-core` remaining independent from Spring/JPA/infrastructure;
  - contracts remaining aligned with `dlc-docs`;
  - tests/verification being executed;
  - Maven verification passing;
  - the PR referencing the corresponding story or task.

### GitHub Actions / CI

- I created `.github/workflows/ci.yml`.

- The CI workflow was configured for Pull Requests targeting:
  - `develop`;
  - `qa`;
  - `main`.

- The workflow configures Java 21 using Temurin.

- The workflow executes:
  - `mvn -B verify`.

- Repository permissions were limited to:
  - `contents: read`.

- A timeout was configured for the CI job.

- Maven dependency caching was enabled through `actions/setup-java`.

### Local infrastructure

- I created `deploy/compose.yml`.

- The local stack provisions an Appointments-owned PostgreSQL instance.

- The local stack provisions RabbitMQ with the management interface.

- PostgreSQL local port:
  - `5432`.

- RabbitMQ AMQP local port:
  - `5672`.

- RabbitMQ management local port:
  - `15672`.

- I configured persistent Docker volumes for:
  - PostgreSQL.
  - RabbitMQ.

- I added a PostgreSQL healthcheck based on `pg_isready`.

- I later added a RabbitMQ healthcheck using `rabbitmq-diagnostics`.

- I locally validated the Compose stack and confirmed:
  - PostgreSQL -> `healthy`.
  - RabbitMQ -> `healthy`.

### Docker image

- I created a multi-stage Dockerfile.

- The build stage uses Maven with Java 21.

- The runtime stage uses Eclipse Temurin Java 21 JRE.

- I validated the Docker image locally using:
  - `docker build -f deploy/Dockerfile -t dlc-appointments-api .`

- The image was successfully built.

- I ran the application from the Docker image.

- I validated `/health` and `/health/ready` from the running container.

### Security hardening applied after automated review

The automated course review identified that the first runtime Docker image executed the Java process as root.

I accepted this finding because it represented a concrete security improvement that belonged directly to the Dockerfile introduced by the PR.

I changed the runtime image to:

- create a dedicated `appointments` system group;
- create a dedicated `appointments` system user;
- copy the application JAR with ownership assigned to that user;
- execute the application with `USER appointments`.

I validated the correction locally using:

`docker exec dlc-appointments-api-test id`

and confirmed that the process no longer ran as `uid=0(root)`.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/9ece35d96cb4d5c66553247829d21159682cc876

### RabbitMQ readiness improvement

The automated review also identified that PostgreSQL had a healthcheck while RabbitMQ did not.

I accepted this recommendation because a container being `started` does not necessarily mean RabbitMQ is already accepting AMQP connections.

I added:

`rabbitmq-diagnostics -q ping`

as the RabbitMQ healthcheck.

I then started the stack again and validated that both services reached:

- PostgreSQL -> `healthy`.
- RabbitMQ -> `healthy`.

This prevented local startup from relying only on the container process being started.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/9ece35d96cb4d5c66553247829d21159682cc876

### Pull Request template correction

A later automated review identified that the PR template had malformed Markdown.

The original template contained escaped syntax such as:

- `\## User Story`
- `\## What changes and why`
- `\---`
- escaped list markers.

It also contained an incorrectly closed Markdown code block.

I accepted this finding because the PR template is an actual governance mechanism and must render correctly on GitHub.

I corrected:

- headings;
- separators;
- code blocks;
- bullet formatting;
- GitHub checklist syntax.

The checklist was converted to actual GitHub checkboxes using:

`- [ ]`

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/cbeed595b7c789380f814009f80e597656244fd1

### `.env.example` review and evolution

The automated review identified that placeholder credentials such as:

`<appointments_db_password>`

and:

`<rabbitmq_password>`

could be interpreted literally if a developer copied `.env.example` directly to `.env`.

I accepted the finding.

The configuration was first changed to empty password values.

A later review correctly noted that empty PostgreSQL and RabbitMQ passwords could cause local startup failures.

I therefore refined the decision and changed `.env.example` to safe development-only values.

Examples:

- `appointments_local_password`
- `rabbitmq_local_password`

These are not production secrets; they are explicitly disposable local-development values.

I then validated the stack using `.env.example` directly:

`docker compose --env-file .env.example -f deploy\compose.yml up -d`

Both services reached:

- PostgreSQL -> `healthy`.
- RabbitMQ -> `healthy`.

This confirmed that a new contributor can use the reference configuration without requiring hidden credentials and without committing real secrets.

### File formatting corrections

The automated review detected missing trailing newlines in several newly created repository files.

I accepted this finding because it was a low-cost repository-quality improvement.

I corrected the affected files and validated them using:

`git diff --check`

The command completed without formatting errors.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/cbeed595b7c789380f814009f80e597656244fd1

### Architectural boundary enforcement

One of the most important repeated review findings was that the README and PR template stated:

`appointments-core` must not depend on Spring, JPA, or infrastructure frameworks.

Initially, this was only a documented/manual review rule.

The first time this was raised, I considered the finding but deferred automation because the repository was still at the initial scaffolding stage.

When the automated review raised the concern again and emphasized that executable domain development was about to begin, I reconsidered the decision and accepted the recommendation.

I added the Maven Enforcer Plugin directly to:

`appointments-core/pom.xml`

The Enforcer rule now rejects prohibited framework/infrastructure dependencies including relevant Spring, Spring Boot, Spring Data, Spring Security, Spring AMQP, Spring Integration, JPA, Hibernate, PostgreSQL, and RabbitMQ dependencies.

The rule uses transitive dependency inspection.

I validated the rule using:

`mvn -B -pl appointments-core verify`

The output confirmed:

`Rule 0: org.apache.maven.enforcer.rules.dependency.BannedDependencies passed`

and:

`BUILD SUCCESS`

This changed the architecture boundary from a documentation-only expectation into a build-breaking technical rule.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/2b6edf88dfce5458f051c7e530126fd1cbcd8c94

### Promotion trace automation

The project governance requires promotion between permanent environments through commit re-application with:

`git cherry-pick -x`

rather than merging permanent branches directly.

The automated review noted that the PR template documented this rule, but CI did not enforce it.

I accepted this recommendation.

I updated CI so that Pull Requests targeting `qa` or `main`:

- use a full Git history checkout (`fetch-depth: 0`);
- reject merge commits in the promotion range;
- inspect promoted commits;
- require the `(cherry picked from commit ...)` trace produced by `git cherry-pick -x`.

Normal Pull Requests into `develop` are not subject to this promotion-trace rule.

This provides automatic evidence that promotion commits preserve their original source commit identity.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/2b6edf88dfce5458f051c7e530126fd1cbcd8c94

### Docker verification improvement

An automated review identified that the first Dockerfile used:

`mvn -B clean package -DskipTests`

The review correctly noted that this meant the Docker image build path could package an artifact without executing Maven verification.

I initially considered this recommendation as a future CI-hardening improvement.

When the finding was repeated, I accepted it and changed the Docker build to:

`mvn -B clean verify`

I also extended CI to build the Docker image after Maven verification.

This means CI can detect:

- invalid Dockerfile syntax;
- incorrect multi-stage COPY paths;
- missing JAR artifacts;
- Docker build failures.

The updated Dockerfile was validated locally and the image built successfully.

Evidence:

https://github.com/code-corhuila/dlc-appointments-api/commit/2b6edf88dfce5458f051c7e530126fd1cbcd8c94

### Automated review findings intentionally not applied immediately

Not every automated recommendation was applied without evaluation.

The course review explicitly allows a reasoned decision not to apply a recommendation, as long as the finding is considered and the decision can be defended.

The following findings were reviewed and intentionally deferred or left unchanged:

- Adding PostgreSQL and RabbitMQ directly as CI services was initially deferred because no persistence or messaging integration tests exist yet.
- Running infrastructure services in CI without any integration test consuming them would add complexity without testing meaningful application behavior.
- The plan is to introduce those CI dependencies together with the first real persistence or messaging integration tests.

- The README documentation for `/health` and `/health/ready` was not removed.
- The automated reviewer could not see their implementation in the repository-configuration PR diff because those endpoints were introduced in the previous scaffolding PR.
- They already existed in `develop`, were locally executed, and had been validated before the configuration branch was created.

- A later review noted that Maven verification is executed once directly in CI and again inside the Docker build.
- This duplication was reviewed but intentionally retained at the current repository size.
- The first execution validates the Maven reactor directly.
- The second validates the complete containerized build path.
- The current project is still small enough that both executions remain well below the configured timeout.
- Artifact reuse can be introduced later when a more complete CI/CD pipeline exists.

- The automated review suggested expanding the Maven Enforcer blacklist to cover additional hypothetical libraries such as further framework families.
- The current rule was considered sufficient for the technologies currently used by the service.
- The decision was to evaluate additional dependencies when they are actually introduced rather than maintain an unlimited speculative blacklist.

### Post-merge findings recorded for future work

After the repository-configuration PR had already been merged, another automated review raised additional possible improvements.

I reviewed these findings instead of rewriting the already merged history.

The review mentioned that `TEST_DATABASE_URL` could create ambiguity because `.env.example` described integration-test behavior although no real integration suite currently consumes that variable.

This was recorded for future integration-test work.

No history rewrite or force push was performed after merge.

The review also suggested adding CI checks for:

- source branch naming conventions;
- target-environment branch rules;
- Conventional Commits PR title validation;
- the 400-line PR size limit.

These recommendations were considered valid but were not added retroactively to the already merged PR.

They are appropriate for a dedicated future repository-governance PR.

The review also noted that the module structure used by the Dockerfile originated in the previous PR.

This did not require a code correction because:

- `appointments-core`;
- `appointments-adapters`;
- `appointments-app`;

already existed in the `develop` base branch from the initial scaffolding PR.

### Pull Request size compliance

The course standard limits a Pull Request to 400 changed lines.

The initial scaffolding work became too large when repository documentation and configuration were included together.

Instead of ignoring the rule, I split the work into two logical Pull Requests.

The first scaffolding commit contained 266 additions:

https://github.com/code-corhuila/dlc-appointments-api/commit/3a1e86c0aff69fc5241dd28b6e0b1db12d55de56

The repository-configuration PR was repeatedly checked with:

`git diff origin/develop --stat`

and:

`git diff origin/develop --numstat`

After all accepted automated-review corrections, the PR remained within the course limit at approximately:

- 385 insertions / 12 deletions at an intermediate validation;
- 397 total changed lines according to the final local PR-size verification before the last update.

This was intentionally monitored so that review corrections did not violate the course's maximum PR size.

### GitHub Actions external blocker

The GitHub Actions check initially reported:

`The job was not started because recent account payments have failed or your spending limit needs to be increased.`

I verified that this was not a Maven, Java, Docker, or application-code failure.

The workflow did not start because of a GitHub organization/account billing or spending-limit restriction.

Local verification was therefore used as technical evidence while the external GitHub Actions issue remained outside the service repository.

### Conventional Commits used

The Appointments repository work was committed using Conventional Commit naming.

Main commits include:

- `chore(appointments): scaffold maven modules and health endpoints`
- `chore(repo): add repository configuration and local infrastructure`
- `fix(deploy): harden container and add RabbitMQ healthcheck`
- `fix(repo): address automated review findings`
- `fix(repo): address remaining automated review findings`

### Clinical documentation contribution in `dlc-docs`

In addition to the Appointments service repository, I also worked on project documentation related to Clinical persistence and cross-domain consistency.

I modified:

- `06-data/data-dictionary.md`
- `06-data/models.md`
- `07-api/contract-reviews/care-and-payments.md`
- `07-api/contracts/openapi/clinical-service.yaml`

The Clinical persistence work included:

- clarification of Clinical-owned persistence responsibilities;
- schema-evolution rules;
- cross-domain data ownership;
- clinical care-closing consistency;
- event/outbox-related coordination;
- Billing ownership of monetary values;
- explicit appointment finalization behavior;
- Clinical readiness versus administrative appointment completion;
- auditing requirements;
- manual care-charge ownership;
- source uniqueness rules;
- relationship/session terminology.

### Clinical/Billing ownership clarification

The documentation was aligned so that Clinical does not become the owner of monetary values.

The refined rule establishes that:

- Clinical records clinical information.
- Billing owns care prices and monetary values.
- Manual care charges are submitted through Billing.
- Dentist/Administrator price entry requires justification.
- Assistant cannot submit manual prices.
- A unique care/source reference prevents duplicate billing.
- Historical financial snapshots remain Billing-owned.

### Clinical/Appointments care-closing clarification

The documentation was also aligned around completion semantics.

The previous wording could imply that Clinical completion automatically completed the Appointment.

The revised documentation clarifies that:

- Clinical completion records completion/readiness of clinical work.
- Appointments records clinical readiness.
- Assistant/Administrator separately finalizes the appointment.
- Waiting for the administrative completion action is a valid pending state.
- It is not treated as a RabbitMQ failure.
- It is not a reason to replay clinical completion.

This improves cross-domain ownership and prevents one service from performing another service's responsibility.

### Clinical OpenAPI alignment

I updated the Clinical OpenAPI contract to make the same care-closing behavior explicit.

The contract now clarifies that procedure completion:

- represents completion of clinical work;
- does not directly complete the Appointment;
- does not accept monetary fields;
- coordinates asynchronously with Appointments/Billing;
- treats administrative appointment completion as a separate responsibility.

### Data dictionary and model alignment

I updated the data dictionary and models to align:

- currency ownership;
- audit information;
- manual pricing identity;
- justification;
- recorded actor/time;
- source record uniqueness;
- appointment/clinical assignment relationships;
- session activity concepts;
- data ownership boundaries.

### Documentation review refinement

After the first Clinical documentation commit, I performed a follow-up refinement.

Initial Clinical persistence commit:

https://github.com/code-corhuila/dlc-docs/commit/51aa32a2690b3c76e67e65c1d081ac3fb6f2ce5b

Follow-up refinement:

https://github.com/code-corhuila/dlc-docs/commit/170949c3965239fa2f1eb1ff43988abea2243f1e

The changes were then integrated through:

PR #26 - Docs/clinical data persistence

https://github.com/code-corhuila/dlc-docs/pull/26

Merged evidence:

https://github.com/code-corhuila/dlc-docs/commit/8372b09e90696ee27fd92d4e4bad186c555c0500

## 3. Blockers and risks

- GitHub Actions was temporarily unable to start because of an organization/account billing or spending-limit restriction. This was external to the repository and not caused by the Java/Maven build.

- The current Appointments work is still repository/domain preparation. HU-APT-001 scheduling business rules are not fully implemented yet.

- Dentist calendar behavior for HU-APT-003 is not fully implemented yet.

- PostgreSQL persistence adapters have not been implemented yet.

- RabbitMQ producers/consumers have not been implemented yet.

- Business integration tests using PostgreSQL and RabbitMQ do not exist yet.

- `TEST_DATABASE_URL` and integration-test execution strategy require a future explicit decision when the first repository-adapter integration tests are added.

- CI currently verifies promotion traceability for `qa` and `main`, but additional governance checks such as branch-name validation, PR-title validation, and automatic 400-line-limit validation can be introduced in a dedicated future governance change.

- Maven verification currently occurs directly in CI and again during Docker image construction. This is intentionally accepted for now, but artifact reuse may be required as the test suite grows.

- The Maven Enforcer blacklist protects the current known architecture boundary, but new libraries introduced in future PRs must continue to be evaluated to ensure they do not leak infrastructure into `appointments-core`.

- Cross-domain implementation must continue to respect ownership:
  - Appointments owns appointment lifecycle/availability.
  - Clinical owns clinical records.
  - Billing owns prices and financial values.
  - Patients owns administrative patient information.
  - Services must not write directly into another service's persistence.

- Clinical completion and Appointment administrative completion must remain separate in implementation, matching the documentation corrected this week.

- The API repositories must not introduce database schema migrations that belong to their dedicated database repositories.

## 4. Plan for next week

- Start executable domain implementation for HU-APT-001.

- Define the Appointment aggregate/domain model in `appointments-core`.

- Define appointment lifecycle states and domain invariants.

- Implement conflict/overlap validation rules.

- Implement dentist availability rules.

- Implement timezone-aware scheduling behavior according to the documented Appointments contract.

- Define input ports for scheduling and querying appointments.

- Define output ports for persistence, messaging, and external-service interaction.

- Begin HU-APT-003 use cases for dentist calendar visibility.

- Ensure Dentist access remains scoped to the permitted appointment/calendar information.

- Add real unit tests for domain rules.

- Add integration tests only when PostgreSQL persistence adapters are introduced.

- Introduce PostgreSQL CI infrastructure together with real integration tests rather than running unused services in CI.

- Continue preserving the enforced `appointments-core` architecture boundary.

- Keep Appointments OpenAPI/contracts aligned with `dlc-docs`.

- Continue validating PR size before opening or updating Pull Requests.

- Consider a dedicated governance PR for:
  - source branch naming validation;
  - destination branch validation;
  - Conventional Commit PR-title validation;
  - automatic 400-line PR-size validation.

- Review whether CI should eventually reuse a previously verified Maven artifact during Docker image creation to avoid duplicate test execution.

- Continue alignment between Clinical care completion and Appointments administrative completion.

- Preserve Billing ownership of manual prices and financial data.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
  - This week's Appointments work was repository scaffolding/configuration performed through `chore/*` branches rather than executable HU implementation branches. Future HU implementation will use the corresponding HU/environment workflow.
- [x] Testable acceptance criteria
  - Acceptance criteria and traceability are defined in `dlc-docs`; executable HU acceptance tests remain pending with business implementation.
- [ ] Tests added/updated (unit / integration)
  - No business unit/integration test suite was added yet because business-domain and persistence implementation has not started. Maven verification, Maven Enforcer, Docker builds, health endpoints, Docker runtime, PostgreSQL health, and RabbitMQ health were validated manually/automatically.
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
  - `appointments-core` remains framework/infrastructure independent and is now protected with Maven Enforcer.
- [x] No secrets; config via environment variables
  - Real `.env` files remain ignored and only safe local-development values are present in `.env.example`.

## 6. Evidence links

### Appointments API - Initial scaffolding

- Maven multi-module foundation, application bootstrap, composition root, and health endpoints:
  https://github.com/code-corhuila/dlc-appointments-api/commit/3a1e86c0aff69fc5241dd28b6e0b1db12d55de56

Commit:
`chore(appointments): scaffold maven modules and health endpoints`

Main evidence:
- root Maven reactor;
- `appointments-core`;
- `appointments-adapters`;
- `appointments-app`;
- Spring Boot entry point;
- application configuration;
- `/health`;
- `/health/ready`.

### Appointments API - Repository configuration

- Repository configuration and local infrastructure:
  https://github.com/code-corhuila/dlc-appointments-api/commit/a718b9e49de883657e43e5b04f4034da09155282

Commit:
`chore(repo): add repository configuration and local infrastructure`

Main evidence:
- `.env.example`;
- `.gitignore`;
- PR template;
- Java 21 GitHub Actions CI;
- project README;
- Dockerfile;
- PostgreSQL/RabbitMQ Compose stack.

### Appointments API - Deployment hardening

- Non-root Docker runtime and RabbitMQ healthcheck:
  https://github.com/code-corhuila/dlc-appointments-api/commit/9ece35d96cb4d5c66553247829d21159682cc876

Commit:
`fix(deploy): harden container and add RabbitMQ healthcheck`

Main evidence:
- dedicated `appointments` runtime user;
- non-root container execution;
- RabbitMQ readiness healthcheck;
- local validation of healthy infrastructure.

### Appointments API - First automated-review correction round

- Repository-quality and automated-review fixes:
  https://github.com/code-corhuila/dlc-appointments-api/commit/cbeed595b7c789380f814009f80e597656244fd1

Commit:
`fix(repo): address automated review findings`

Main evidence:
- corrected PR-template Markdown;
- corrected checklist rendering;
- `.env.example` refinement;
- trailing newline/formatting corrections;
- repository-quality fixes requested by automated review.

### Appointments API - Architecture and CI review corrections

- Maven Enforcer, promotion trace, environment refinement, and Docker verification:
  https://github.com/code-corhuila/dlc-appointments-api/commit/2b6edf88dfce5458f051c7e530126fd1cbcd8c94

Commit:
`fix(repo): address remaining automated review findings`

Main evidence:
- Maven Enforcer in `appointments-core`;
- framework/infrastructure dependency protection;
- `cherry-pick -x` validation for `qa`/`main`;
- merge-commit rejection for environment promotion;
- safe local credentials;
- Docker build using Maven verification;
- CI Docker image validation.

### Clinical persistence documentation

- Main Clinical persistence and schema-evolution update:
  https://github.com/code-corhuila/dlc-docs/commit/51aa32a2690b3c76e67e65c1d081ac3fb6f2ce5b

Commit:
`docs: define clinical persistence and schema evolution rules`

Files affected:
- `06-data/data-dictionary.md`
- `06-data/models.md`
- `07-api/contract-reviews/care-and-payments.md`
- `07-api/contracts/openapi/clinical-service.yaml`

### Clinical documentation refinement

- Follow-up documentation alignment:
  https://github.com/code-corhuila/dlc-docs/commit/170949c3965239fa2f1eb1ff43988abea2243f1e

Commit:
`docs: define clinical persistence and schema evolution rules`

### Clinical documentation PR

- Pull Request #26:
  https://github.com/code-corhuila/dlc-docs/pull/26

PR:
`Docs/clinical data persistence`

### Clinical documentation merged evidence

- Merge commit:
  https://github.com/code-corhuila/dlc-docs/commit/8372b09e90696ee27fd92d4e4bad186c555c0500

Merge:
`Merge pull request #26 from code-corhuila/docs/clinical-data-persistence`

### Local technical validation performed during the week

- `mvn clean verify` -> successful.
- `mvn -B -pl appointments-core verify` -> Maven Enforcer rule passed.
- Spring Boot executable JAR -> started successfully.
- `GET /health` -> validated.
- `GET /health/ready` -> validated.
- Docker image build -> successful.
- Dockerized application -> started successfully.
- Docker process user -> validated as non-root.
- PostgreSQL Compose service -> validated as `healthy`.
- RabbitMQ Compose service -> validated as `healthy`.
- `.env.example` direct Compose configuration -> validated.
- `git diff --check` -> validated without formatting errors.
- PR size -> monitored to remain within the course 400-line change limit.