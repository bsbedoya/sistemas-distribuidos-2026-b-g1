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
- SPRINT_GOAL: Improve observability and incident response for the dental appointment scheduling journey.

## 1. User stories worked this week

| **HU ID** | **Title** | **Status (todo/doing/done)** | **Evidence (PR or commit URL)** |
| --- | --- | --- | --- |
| HU-APT-001 | Dental appointment scheduling observability: tracing, structured logs, instrumentation and metrics (CP0-CP4) | doing | [CP3](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0fa575d), [CP4](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0ffb957) |
| TECH-OBS-001 | Reliability planning: SLOs, error budget, burn-rate alerts, runbook and incident simulation (CP6-CP9) | done | [CP6](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/220166a), [CP7](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/3ee75e0), [CP9](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/dec7639) |

> **Scope note:** TECH-OBS-001 is an individual workshop task, not an officially approved product HU. The observability implementation remains a prototype.

## 2. My individual contribution

- **CP1 - Critical journey:** Mapped dental appointment scheduling through the Appointments Portal, API Gateway, appointments-service and PostgreSQL, including W3C traceparent propagation.
- **CP2 - Structured logging:** Defined JSON logs with trace_id, span_id, correlation_id, event, service, timestamp and duration_ms.
- **CP3 - Instrumentation:** Created a Spring Boot prototype with HTTP trace propagation, structured logs, Micrometer metrics and a JUnit test that passed with zero failures.
- **CP4 - Golden signals:** Documented latency, traffic, errors and saturation, including PromQL queries and bounded metric labels.
- **CP6 - SLOs:** Proposed three reliability objectives and calculated a 0.5% error budget, equivalent to 150 failed requests or 216 minutes in the estimated scenario.
- **CP7 - Alerts:** Defined fast-burn and slow-burn Prometheus alerts with 7.2% and 3% error thresholds.
- **CP8 - Runbook:** Documented incident symptoms, confirmation queries, first mitigations, escalation and recovery checks.
- **CP9 - Incident drill:** Simulated a 40-minute PostgreSQL slowdown affecting 25% of requests and documented the trace, impact, timeline and preventive user stories.
- **CP10 - Planning:** Prepared an implementation plan for bringing these improvements into the real DILUCCA architecture.

## 3. Blockers and risks

- Observability instrumentation has not been integrated into the production appointments-service.
- Downstream trace propagation was tested using a simulated local endpoint.
- Gateway metrics and production Prometheus alert deployment remain unverified.
- The current counter requires a normalized route label and improved error classification.
- PostgreSQL failure handling and recovery procedures need testing against the real service.
- Formal promtool validation of the alert rules is still pending.

## 4. Plan for next week

- Integrate trace propagation and structured logging into the real Appointments API.
- Add route-specific Prometheus metrics for POST /api/v1/appointments.
- Distinguish expected scheduling conflicts from infrastructure failures.
- Validate SLO targets using realistic traffic and measured service behavior.
- Test PostgreSQL timeout handling and appointment recovery.
- Review alert rules, deployment procedures and escalation responsibilities with the team.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch and PR for production integration
- [x] Testable acceptance criteria defined
- [x] Automated trace propagation test passed
- [ ] Production DDD / hexagonal integration verified
- [x] No secrets or sensitive patient data included in the workshop code

## 6. Evidence links

### DILUCCA Documentation

- [DILUCCA Docs](https://github.com/code-corhuila/dlc-docs)
- [Appointments API](https://github.com/code-corhuila/dlc-appointments-api)
- [Appointments Portal](https://github.com/code-corhuila/dlc-appointments-portal)

### W11 Observability Workshop - Appointments

- CP0: [Workshop configuration](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop)
- CP1: [Journey and trace map](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/9bc5a1e)
- CP2: [Structured logs](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0541454)
- CP3: [Trace filter and logger](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/2cfcb9c)
- CP3: [Metrics instrumentation](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/20a18df)
- CP3: [Automated test](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0fa575d)
- CP4: [Metric catalog](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/0ffb957)
- CP5: [Delivery 1 status](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/a2ee071)
- CP6: [SLO definitions](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/630b4f3)
- CP6: [Traffic estimates](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/8937104)
- CP6: [Error budget policy](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/220166a)
- CP7: [Burn-rate alerts](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/3ee75e0)
- CP8: [Incident response runbook](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/07-runbook.md)
- CP9: [Incident drill and postmortem](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/commit/dec7639)

### Workshop Resources

- [Week 11 Workshop Folder](https://github.com/bsbedoya/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop)
- [Reflection Document](https://docs.google.com/document/d/17tkciAmjerht8xmr-4mnyyEsozMKCwL_pex1hB6inVw/edit?usp=sharing)
