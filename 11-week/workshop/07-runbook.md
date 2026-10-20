\# CP8 — Incident Response Runbook



\## 1. Incident Information



\- Project: DILUCCA

\- Domain: Appointments

\- Service: appointments-service

\- Journey: Dental Appointment Scheduling

\- Endpoint: POST /api/v1/appointments

\- Alert: AppointmentsFastBurn

\- Severity: critical

\- Team: appointments

\- Availability SLO: 99.5% over 30 days



This runbook is a proposed operational procedure. The observability prototype is not deployed in production.



\## 2. Symptoms



The fast-burn alert indicates that appointment scheduling requests are failing too frequently.



Possible symptoms:

\- Error rate above 7.2%.

\- Patients cannot schedule appointments.

\- More failed HTTP requests.

\- Increased response time.

\- PostgreSQL connectivity problems.

\- Unexpected API Gateway or service errors.



Expected appointment conflicts must not be classified as infrastructure failures.



\## 3. Confirm the Incident



\### Check the error ratio



```promql

sum(rate(appointments\_requests\_total{

&#x20; route="/api/v1/appointments",outcome="error"

}\[5m]))

/

clamp\_min(sum(rate(appointments\_requests\_total{

&#x20; route="/api/v1/appointments"

}\[5m])),0.000001)

```



Confirm the same condition over the 1-hour window.



\### Check P95 latency



```promql

histogram\_quantile(0.95,

&#x20; sum by (le) (

&#x20;   rate(appointments\_request\_duration\_seconds\_bucket\[5m])

&#x20; )

)

```



\### Find related logs



In the local demonstration, search console logs for a trace ID:



```text

"trace\_id":"4bf92f3577b34da6a3ce929d0e0e4736"

```



Example log search after collecting console output:



```cmd

findstr /C:"4bf92f3577b34da6a3ce929d0e0e4736" appointments.log

```



The log file must already exist. The current demo writes JSON to standard output.



Follow the same trace\_id across incoming and downstream requests. Review event, duration\_ms, service and available error details.



\## 4. First Mitigations — Ordered by Risk



\### 1. Reduce Impact — Lowest Risk



Pause additional nonessential releases and inspect the affected scheduling flow.



If a verified feature flag or controlled traffic-reduction mechanism exists, use it according to the team's deployment procedure.



Do not disable scheduling without notifying the responsible team.



\### 2. Roll Back a Recent Change — Medium Risk



If the incident started after a deployment, request approval to restore the last verified stable version.



Check the deployment history and confirm database migration compatibility before rollback.



Do not roll back database changes without reviewing data safety.



\### 3. Restore Dependency Capacity — Higher Risk



If PostgreSQL or another dependency is overloaded, involve the infrastructure owner.



Consider scaling the affected service only after verifying that the deployment environment supports it.



Do not assume scaling fixes database connection failures.



\## 5. Escalation



| Role | Responsibility |

|---|---|

| Appointments owner | Confirm impact and investigate scheduling errors. |

| API Gateway / integration owner | Review routing and downstream communication. |

| Database owner | Investigate PostgreSQL health and connection errors. |

| Release coordinator | Approve rollback and communicate incident status. |



Escalate immediately for sustained fast-burn conditions or when patients cannot schedule appointments.



Specific on-call contacts and production escalation channels must be defined by the team.



\## 6. Verify Recovery



1\. Confirm that the affected endpoint responds correctly.

2\. Check that the error ratio falls below 7.2%.

3\. Verify that the fast-burn alert condition has cleared.

4\. Review P95 latency and confirm improvement.

5\. Inspect recent JSON logs for recurring failures.

6\. Verify that appointment persistence works in the real system.

7\. Monitor the service before resuming normal releases.



The alert threshold is not the recovery target. Continue stabilization until the error rate is compatible with the 99.5% availability SLO.



\## 7. Limitations and Follow-up



\- The CP3 demonstration has no real PostgreSQL persistence.

\- The counter currently lacks the route label required by the alert query.

\- Production deployment and alert evaluation remain unverified.

\- Feature flags, rollback automation and scaling must be checked before use.

\- Formal Prometheus rule validation is still pending.



After recovery, document the timeline, affected requests, root cause, actions taken and preventive improvements in the CP9 postmortem.



