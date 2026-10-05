# Weekly Status - Week 09

- FULL_NAME: Brayan Smith Bedoya
- GITHUB_USER: bsbedoya
- TEAM: Di Lucca Dental Care & Technology
- SPRINT_GOAL: Establish the technical foundation of the Appointments portal, including Angular Native Federation scaffolding, repository hygiene, CI/CD validation, Docker/nginx deployment configuration, and prepare the portal for the upcoming TDD-based functional implementation.

## 1. User stories worked this week

| **HU ID**  | **Title** | **Status (todo/doing/done)** | **Evidence (PR or commit URL)** |
| ---------- | --------- | ---------------------------- | ------------------------------- |
| N/A | Appointments portal technical foundation and Native Federation scaffolding | done | https://github.com/code-corhuila/dlc-appointments-portal/commit/138c240e06e05a71db6bef94017627d6942481a7 |
| N/A | Appointments portal repository hygiene, CI and deployment foundation | done | https://github.com/code-corhuila/dlc-appointments-portal/commit/c92a37bb76345f57c7b414520ebad66e19dd695e |

## 2. My individual contribution

- Created the initial Angular 21 foundation for `dlc-appointments-portal` using Native Federation.
- Configured the portal as an Appointments domain micro-frontend and exposed its route boundary through Native Federation.
- Established the `main.ts` and `bootstrap.ts` application startup structure.
- Configured strict TypeScript and preserved the zoneless Angular architecture.
- Kept the portal aligned with the distributed frontend architecture by avoiding a portal-owned `HttpClient`, authentication interceptor, JWT handling, session storage, or gateway URL configuration.
- Added and refined the provisional `shell-contract.ts` boundary while keeping authentication, session management, shared HTTP communication, correlation IDs, and common error handling as responsibilities of `dlc-front`.
- Corrected review findings related to the Angular root selector, Native Federation configuration, Node.js runtime definition, Angular test generation defaults, shell contract boundaries, and repository scaffolding.
- Replaced the inherited LMS/library repository README content with documentation specific to the Di Lucca Appointments portal.
- Added repository hygiene and common project files:
  - `.gitignore`
  - `.dockerignore`
  - `.env.example`
  - `.github/pull_request_template.md`
  - `.github/workflows/ci.yml`
- Configured GitHub Actions CI for Pull Requests targeting `develop`, `qa`, and `main`.
- Configured the Angular CI workflow with Node.js 22, `npm ci`, and `npm run build`.
- Added the deployment baseline under `deploy/`:
  - `Dockerfile`
  - `compose.yml`
  - `nginx.conf`
- Implemented a multi-stage Docker build using Node.js 22 for the Angular build stage and nginx for runtime delivery.
- Configured nginx SPA fallback for Angular routes.
- Configured Native Federation metadata so `remoteEntry.json` and `federation.manifest.json` are served without cache.
- Added nginx security headers:
  - `X-Content-Type-Options`
  - `X-Frame-Options`
  - `Strict-Transport-Security`
  - `Content-Security-Policy`
- Validated the Docker image locally and confirmed:
  - the Angular application builds successfully
  - nginx configuration is valid
  - `/` returns HTTP 200
  - `/remoteEntry.json` returns HTTP 200
  - `remoteEntry.json` is valid JSON
  - Native Federation metadata keeps the required no-cache policy
  - Angular SPA fallback works correctly
- Reduced the Docker build context by adding `.dockerignore`.
- Normalized text-file endings after automated review feedback.
- Kept the Pull Requests within the course change-size rules while separating repository foundation work from future business functionality.
- Reviewed automated course feedback and documented which findings were applied, deferred, or intentionally not applied.
- The first portal scaffolding Pull Request was merged into `develop`.
- The repository hygiene and deployment Pull Request was also completed and merged into `develop`.
- A BPMN diagram for the Appointments/core business flow was also prepared during the week. It has been reviewed as part of the project work, but it has not yet been uploaded to the `dlc-docs` repository.
- The `dlc-appointments-api` GitHub Actions workflow, which had configuration-related failures during the previous week, was reviewed and corrected. The current CI workflow executions are completing successfully.

## 3. Blockers and risks

- The final typed integration contract provided by `dlc-front` is still required to replace the provisional portal shell contract without duplicating authentication or HTTP infrastructure.
- The BPMN diagram has already been prepared but is still pending publication in the `dlc-docs` repository, so the documentation repository does not yet contain that evidence.
- Functional Appointments features have not started yet; the current work establishes the technical foundation required before implementing the calendar, scheduling, availability, lifecycle actions, and clinical assignments.
- TDD must be applied from the beginning of the functional implementation using synthetic test data and keeping each Pull Request below the 400-line course limit.

## 4. Plan for next week

- Start the functional implementation of `dlc-appointments-portal` using Test-Driven Development.
- Begin with the Appointments domain models and shared utilities before implementing screens.
- Create unit tests first using synthetic appointment, dentist, patient-reference, status, date/time, and error data.
- Implement and test the `America/Bogota` clinic-time helpers.
- Implement and test the idempotency-key generation behavior.
- Add the contract-aligned Appointments models, including pagination and API error structures.
- Continue with the Appointments API client using test doubles / Angular HTTP testing instead of depending on a complete backend implementation.
- Keep tests in the same Pull Request as the production code they validate.
- Measure every Pull Request before publication and keep production-code changes below the 400-line limit.
- Upload the completed BPMN diagram and its documentation to `dlc-docs`.
- Continue monitoring the `dlc-appointments-api` GitHub Actions workflow to ensure the corrected CI configuration remains green.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [x] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links

- Portal initial scaffolding:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/2fde1097df0fdaadbe3055182cb48bc093524651
- First automated-review corrections:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/aaffd5fc502e911e9f09c8bd3700166c861d2ae3
- Follow-up scaffolding corrections:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/77908810c1812cc6b3fff7fb91db07067d7006ed
- Portal scaffolding PR merged into `develop`:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/138c240e06e05a71db6bef94017627d6942481a7
- Repository hygiene and deployment setup:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/1f0a6e604bcf1ce8317902906d3a9c090c122851
- Text-file hygiene normalization:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/4bd78e739f84688ff5e8057fa28e91060cb46db5
- nginx security hardening:
  - https://github.com/code-corhuila/dlc-appointments-portal/commit/c92a37bb76345f57c7b414520ebad66e19dd695e
- Appointments portal Pull Request #1:
  - https://github.com/code-corhuila/dlc-appointments-portal/pull/1
- Appointments portal Pull Request #2:
  - https://github.com/code-corhuila/dlc-appointments-portal/pull/2
- Appointments API GitHub Actions:
  - https://github.com/code-corhuila/dlc-appointments-api/actions
- BPMN diagram:
  - Prepared locally/project-side; pending upload to `dlc-docs`.