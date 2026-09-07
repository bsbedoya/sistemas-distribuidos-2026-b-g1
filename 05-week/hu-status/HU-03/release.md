# Delivery — Cut 1
## State
**Not yet validated for release.** The monolithic code of Cut 1 exists, but evidence of comprehensive execution and acceptance of its flows is missing.
| Element | Status |
|---|---|
| Version | Backend `0.1.0-SNAPSHOT`; there is no final tag for Cut 1. |
| Scope | Proposed in [HU-02](../HU-02/mvp-scope.md), pending ratification. |
| Features included/excluded | Documented for the monolith; Comprehensive demonstration pending. |
| Tests | Static audit only; see [qa.md](./qa.md). |
| Execution | Documented using Docker Compose in the README of the monolith. |
| Current Docker | Frontend, backend and PostgreSQL configured on the monolith. |
| Release GitHub/ZIP | Not created or affirmed. |
## Criteria to complete the delivery
- Scope of the monolith approved by the team.
- Compose executed from a clean environment with correct health checks.
- Verified PostgreSQL initialization and persistence.
- Cut 1 Histories with complete Given/When/Then criteria and traceability.
- Evidence of unitary, integration, contracts, security and E2E.
- Controlled promotion `develop → qa → main`, without skipping approvals.
- Version/tag and release notes only after a reproducible build.
- Strangler Fig plan registered as later work, without blocking monolithic delivery.
The requested folder `docs/12-ux-ui/` was not created because this repository already has a different numbering convention and new documentation should remain grouped in `docs/hu/`. This file serves the purpose of release within that exclusive folder.
## Content of a valid delivery
Deliverable must include source code, `.env.example` without secrets, Dockerfiles, Compose, startup instructions, backend/frontend version, schema or migrations, API documentation, and QA results. Actual patient data should not be included in repositories, captures, or packages.
## Recommended procedure
1. Confirm that the integration branch contains only reviewed changes.
2. Run build and tests from a clean environment.
3. Build Docker images without depending on undocumented local files.
4. Raise the stack and run smoke tests.
5. Go through the integral flow defined in HU-02.
6. Record known defects and decide if any are blocking delivery.
7. Promote `qa` for acceptance.
8. Integrate into `main` only with approval.
9. Create version/tag and notes only after checking the artifact.
## Release checklist
| Area | Verification |
|---|---|
| Settings | Documented variables and excluded secrets. |
| Build | Angular and Spring Boot compile. |
| Database | Initializes and preserves data on volume. |
| Health | Backend and dependencies report correct status. |
| Security | Login, 401, 403 and tested roles. |
| Functionality | Patient journey – appointment – ​​history – invoice – approved payment. |
| Documentation | README, Swagger and HU match the application. |
| Traceability | Version and results associated with a commit. |
| Earrings | Known defects, responsible and acceptance decision. |
## Expected release notes
Notes should indicate version, date, included scope, relevant changes, update instructions, limitations, and known issues. They should not claim that there is migration to microservices. They can mention that the monolith is organized for a future strategy Strangler Fig.
## Go/No-Go Criterion
**Go** is recommended when the stack boots reproducibly, there are no open critical/high defects, the main traversal has passed, and the documentation corresponds to the behavior. **No-Go** is recommended in the event of data loss, improper access, inability to start, financial inconsistency or lack of evidence about the main flow.