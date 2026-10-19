<!--
Your weekly grade is read AUTOMATICALLY from this file:
11-week/hu-status/README.md (inside YOUR fork).
English.
Do not remove section headings or table headers.
-->

# Weekly Status - Week 11

- FULL_NAME: Brayan Smith Bedoya Montealegre
- GITHUB_USER: bsbedoya
- TEAM: DILUCCA
- SPRINT_GOAL: Make the dental appointment scheduling journey observable through trace propagation, structured logging, metrics, and automated testing.

## 1. User stories worked this week

| **HU ID** | **Title** | **Status (todo/doing/done)** | **Evidence (PR or commit URL)** |
| --- | --- | --- | --- |
| HU-APT-001 | Dental appointment scheduling: observability, trace propagation, structured logs, instrumentation, and metric catalog (W11 workshop, CP0-CP4) | doing | [CP3 instrumentation](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/20a18df), [CP3 test](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0fa575d), [CP4 metrics](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0ffb957) |

> **Scope note:** The observability implementation is a workshop prototype based on the Appointments domain. It does not replace the production implementation of the scheduling service.

## 2. My individual contribution

- **Critical journey mapping (CP1):** Documented the dental appointment scheduling flow through the Appointments Portal, API Gateway, appointments-service, and PostgreSQL. Defined the W3C traceparent propagation strategy and identified the AppointmentScheduled event.

- **Structured logging (CP2):** Created a JSON logging contract with trace_id, span_id, correlation_id, timestamp, service, event, duration_ms, and appointment-related fields. Established rules to avoid exposing sensitive patient information.

- **Instrumentation (CP3):** Developed a Spring Boot observability prototype using Java, Micrometer, and Prometheus. Implemented an HTTP tracing filter, structured JSON logging, request counters, a latency histogram, and simulated downstream HTTP propagation.

- **Automated testing (CP3):** Created a JUnit test to verify that the trace_id remains consistent between the incoming request and the simulated downstream endpoint. The test passed with 1 test, 0 failures, and 0 errors.

- **Golden signals (CP4):** Defined the metric catalog for appointments-service and the API Gateway, covering latency, traffic, errors, and saturation. Documented PromQL queries, label cardinality, restricted labels, and latency histogram buckets.

## 3. Blockers and risks

- The instrumentation currently runs as an independent workshop prototype and is not integrated into the production DILUCCA appointments API.

- Downstream HTTP propagation was tested using a simulated endpoint rather than a separate deployed microservice.

- The proposed API Gateway metrics have not yet been implemented or verified.

- The current metrics do not fully distinguish scheduling operations from other HTTP endpoints.

- A complete production observability implementation will require integration with the real Appointments API, gateway, and related services.

## 4. Plan for next week

- Define service-level indicators and service-level objectives for dental appointment scheduling.

- Calculate the availability error budget based on an estimated request volume.

- Design fast and slow burn-rate alerts using Prometheus.

- Create an incident response runbook for the Appointments service.

- Simulate a downstream performance incident and document the postmortem.

- Evaluate how the workshop instrumentation could be integrated into the real DILUCCA architecture.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment
- [x] Testable acceptance criteria for trace propagation
- [x] Automated test added and executed successfully
- [ ] Production DDD / hexagonal integration verified
- [x] No secrets or sensitive patient data included in the workshop code

## 6. Evidence links

### DILUCCA Documentation

- [DILUCCA Docs](https://github.com/code-corhuila/dlc-docs)
- [Appointments API](https://github.com/code-corhuila/dlc-appointments-api)
- [Appointments Portal](https://github.com/code-corhuila/dlc-appointments-portal)

### W11 Observability Workshop — Appointments (Personal Fork)

- CP0: [Workshop configuration](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop)
- CP1: [Critical journey and trace mapping](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/9bc5a1e)
- CP2: [Structured log contract](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0541454)
- CP3: [Trace-context middleware and structured logger](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/2cfcb9c)
- CP3: [Observability module configuration](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/208d155)
- CP3: [HTTP propagation and metrics](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/20a18df)
- CP3: [Automated trace propagation test](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0fa575d)
- CP4: [Golden signals and metric catalog](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0ffb957)

### Workshop Resources

- [Week 11 Workshop Folder](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop)
- [Reflection Document](https://docs.google.com/document/d/17tkciAmjerht8xmr-4mnyyEsozMKCwL_pex1hB6inVw/edit?usp=sharing)

