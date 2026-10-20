# CP7 — Burn-Rate Alert Policy



## Project Context



- Project: DILUCCA

- Domain: Appointments

- Service: appointments-service

- Journey: Dental Appointment Scheduling

- Endpoint: POST /api/v1/appointments

- Availability SLO: 99.5% over 30 days



## 1. Error Budget



The availability error budget defined in CP6 is 0.5%.



Planning assumptions:



- 1,000 appointment requests per day

- 30,000 requests over 30 days

- 150 allowed failed requests

- 216 minutes of equivalent complete downtime



These values are academic estimates, not production measurements.



## 2. Fast Burn Alert



- Name: AppointmentsFastBurn

- Severity: critical

- Burn rate: 14.4

- Main window: 1 hour

- Short window: 5 minutes

- Additional confirmation: for 5 minutes



Error-rate threshold:



14.4 x 0.5% = 7.2%



Budget consumption at this burn rate:



14.4 x (1 / 720) x 100 = 2%



A sustained fast burn consumes approximately 2% of

the 30-day error budget in one hour.



The team should investigate immediately, inspect

recent deployments, review traces and check dependencies.



## 3. Slow Burn Alert



- Name: AppointmentsSlowBurn

- Severity: warning

- Burn rate: 6

- Main window: 6 hours

- Short window: 30 minutes

- Additional confirmation: for 30 minutes



Error-rate threshold:



6 x 0.5% = 3%



Budget consumption at this burn rate:



6 x (6 / 720) x 100 = 5%



A sustained slow burn consumes approximately 5% of

the 30-day error budget in six hours.



The team should investigate recurring failures and

prioritize reliability improvements.



## 4. Alert Response Policy



| Alert | Priority | Action |

|---|---|---|

| Fast burn | Critical | Investigate immediately and mitigate customer impact. |

| Slow burn | Warning | Investigate persistent failures and review recent changes. |



Check the appointments-service JSON logs using trace\_id.

Inspect the API Gateway and PostgreSQL dependency.

Review request counters and latency histograms.



## 5. Alerts We Will Not Create



### High CPU usage alone



CPU utilization is a possible cause, not proof of a

failed appointment scheduling journey.



A high CPU alert alone could generate unnecessary noise.



### PostgreSQL latency alone



Database latency is useful diagnostic information,

but it does not always mean appointments are failing.



We will monitor the user-facing error budget instead

and use database metrics during investigation.



## 6. Implementation Limitations



These Prometheus alert rules have not been deployed.



The CP3 prototype currently exposes an outcome label,

but does not expose the required route label.



Expected business rejections are not yet distinguished

from service failures.



The proposed runbook URL targets the CP8 deliverable.

The runbook must be created before operational use.



Prometheus rule validation and production integration

remain pending.



