\# CP9 — Incident Drill and Blameless Postmortem



\## 1. Incident Context



\- Project: DILUCCA

\- Domain: Appointments

\- Journey: Dental Appointment Scheduling

\- Endpoint: POST /api/v1/appointments

\- Dependency: PostgreSQL

\- Duration: 40 minutes

\- Severity: Simulated critical incident



This is a hypothetical exercise, not a real production incident.



\## 2. Incident Scenario



During 40 minutes, PostgreSQL responds in 3 seconds for 25% of appointment scheduling requests.



For this exercise, assume a 2-second database timeout. Affected requests therefore fail with a server error instead of scheduling an appointment.



The remaining 75% complete normally.



\## 3. Slow Request Trace



Example simulated trace:



\- trace\_id: 4bf92f3577b34da6a3ce929d0e0e4736



```text

POST /api/v1/appointments       2050 ms

|

+-- API Gateway                  15 ms

|

+-- appointments-service       2035 ms

&#x20;   |

&#x20;   +-- Validate appointment     20 ms

&#x20;   |

&#x20;   +-- PostgreSQL query       2000 ms

&#x20;   |   Result: TIMEOUT

&#x20;   |

&#x20;   +-- Error handling           15 ms

```



The PostgreSQL operation is the main contributor to the failed request.



\## 4. Structured Logs



The following JSON logs are simulated examples based on the CP2 logging contract.



```json

{"timestamp":"2026-10-20T15:00:00Z","level":"INFO","service":"appointments-service","version":"0.1.0-SNAPSHOT","environment":"simulation","trace\_id":"4bf92f3577b34da6a3ce929d0e0e4736","span\_id":"1111111111111111","correlation\_id":"4bf92f3577b34da6a3ce929d0e0e4736","event":"appointment.request.started","message":"Scheduling request received","duration\_ms":0}

{"timestamp":"2026-10-20T15:00:00Z","level":"INFO","service":"appointments-service","version":"0.1.0-SNAPSHOT","environment":"simulation","trace\_id":"4bf92f3577b34da6a3ce929d0e0e4736","span\_id":"2222222222222222","correlation\_id":"4bf92f3577b34da6a3ce929d0e0e4736","event":"appointment.validation.completed","message":"Appointment request validated","duration\_ms":20}

{"timestamp":"2026-10-20T15:00:02Z","level":"ERROR","service":"appointments-service","version":"0.1.0-SNAPSHOT","environment":"simulation","trace\_id":"4bf92f3577b34da6a3ce929d0e0e4736","span\_id":"3333333333333333","correlation\_id":"4bf92f3577b34da6a3ce929d0e0e4736","event":"appointment.database.timeout","message":"PostgreSQL operation timed out","duration\_ms":2000,"error\_code":"SERVICE\_UNAVAILABLE"}

{"timestamp":"2026-10-20T15:00:02Z","level":"ERROR","service":"appointments-service","version":"0.1.0-SNAPSHOT","environment":"simulation","trace\_id":"4bf92f3577b34da6a3ce929d0e0e4736","span\_id":"1111111111111111","correlation\_id":"4bf92f3577b34da6a3ce929d0e0e4736","event":"appointment.request.failed","message":"Appointment scheduling failed","duration\_ms":2050,"error\_code":"SERVICE\_UNAVAILABLE"}

```



\## 5. SLI Impact



Assumptions from CP6:



\- Daily traffic: 1,000 requests

\- Monthly traffic: 30,000 requests

\- Availability SLO: 99.5%

\- Error budget: 150 requests or 216 minutes



During the simulated incident:



\- Error ratio: 25%

\- Availability SLI: 75%

\- Latency SLI: approximately 75% within 1 second

\- Memory stability SLI: not determined



The latency estimate assumes unaffected requests finish within 1 second and failed requests take approximately 2 seconds.



Traffic in 40 minutes:



1000 x 40 / 1440 = 27.78 requests



Expected failed requests:



27.78 x 0.25 = 6.94, approximately 7 requests



Consumed error budget:



6.94 / 150 x 100 = 4.63%



Equivalent full-outage minutes:



40 x 0.25 = 10 minutes



This represents approximately 10 of the 216 permitted minutes.



All calculations are estimates, not observed traffic.



\## 6. Which Alert Fires First?



Fast burn:

\- Threshold: 7.2%

\- Main window: 1 hour

\- Short window: 5 minutes

\- Additional persistence: 5 minutes



Assuming stable traffic and a complete healthy hour before the incident, the one-hour error ratio crosses 7.2% after approximately 17.3 minutes.



The fast-burn alert can fire around minute 23, assuming 30-second rule evaluations and no missing data.



Slow burn:

\- Threshold: 3%

\- Main window: 6 hours

\- Short window: 30 minutes

\- Additional persistence: 30 minutes



At minute 40, the six-hour error ratio is approximately 2.78%, below 3%.



Therefore, AppointmentsFastBurn fires first. AppointmentsSlowBurn does not fire in this scenario.



\## 7. Runbook Response



Follow 07-runbook.md:



1\. Confirm the fast-burn alert with PromQL and trace\_id logs.

2\. Pause risky releases and contact the Appointments owner.

3\. Escalate PostgreSQL performance to the database owner.

4\. Investigate the slow query and apply an approved mitigation.

5\. Verify recovery using error ratio, latency and request logs.



Runbook step 3, dependency recovery, addresses the simulated database problem.



Assume the database issue is resolved at minute 40. This is a simulated recovery, not an executed repair.



\## 8. Blameless Postmortem



\### Timeline



| Minute | Event |

|---|---|

| 0 | PostgreSQL slowdown begins. |

| 5 | Short-window error ratio increases. |

| 18 | One-hour error ratio exceeds 7.2%. |

| 23 | Fast-burn alert fires. |

| 25 | Team investigates traces and PostgreSQL. |

| 30 | Database owner begins mitigation. |

| 40 | Dependency performance returns to normal. |

| 45 | Team checks recovery and continued monitoring. |



\### Impact



Approximately 28 appointment scheduling requests occurred during the incident, including approximately 7 failures.



The incident consumed around 4.63% of the monthly error budget, equivalent to 10 minutes of complete unavailability.



\### Root Cause



Assumed root cause: PostgreSQL query degradation increased database response time beyond the configured timeout.



\### Contributing Factors



\- Dependency latency affects synchronous scheduling.

\- Database timeout errors reduce availability.

\- Current prototype instrumentation lacks route-specific metrics.

\- Production alert deployment has not been verified.



\## 9. Preventive User Stories



\### HU-OBS-001 — Route Metrics



As an Appointments owner, I want route-specific metrics so that scheduling failures can be measured accurately.



Acceptance criteria:

\- POST /api/v1/appointments has a normalized route label.

\- Prometheus exposes request totals by outcome.

\- Expected business conflicts are not counted as outages.



\### HU-OBS-002 — Database Timeout Monitoring



As an Appointments owner, I want database timeout visibility so that slow PostgreSQL operations can be investigated.



Acceptance criteria:

\- Database operations report duration and timeout outcomes.

\- Trace IDs connect incoming requests with dependency logs.

\- An automated test verifies timeout error classification.



\### HU-OBS-003 — Incident Recovery Verification



As a release coordinator, I want recovery checks so that scheduling is safe before normal releases resume.



Acceptance criteria:

\- An automated check verifies the scheduling endpoint.

\- Error ratio and P95 latency are reviewed.

\- Recovery evidence is recorded in the incident report.



\## 10. Conclusion



This drill demonstrates how a downstream slowdown can affect appointment scheduling and consume the availability error budget.



Trace context, structured logs, SLO metrics, burn-rate alerts and a documented runbook help the team identify problems and respond consistently.



The next step is to validate these mechanisms in the real DILUCCA architecture.



