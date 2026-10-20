\# CP6 â€” SLIs, SLOs and Error Budget



\## Project Context



\- Project: DILUCCA

\- Domain: Appointments

\- Journey: Dental Appointment Scheduling

\- Endpoint: POST /api/v1/appointments

\- Service: appointments-service

\- Dependencies: API Gateway and PostgreSQL

\- Measurement window: Rolling 30 days



\## 1. Service Level Objectives



The following SLOs are proposed targets for the DILUCCA appointment scheduling journey. They are not verified production performance results.



\### SLO 1 â€” Availability



\*\*SLI formula:\*\*



Availability = successful valid requests / total valid requests



\- Target: 99.5%

\- Window: 30 days

\- Scope: POST /api/v1/appointments



Proposed PromQL:



```promql

sum(increase(appointments\_requests\_total{

&#x20; route="/api/v1/appointments",outcome="success"

}\[30d]))

/

clamp\_min(sum(increase(appointments\_requests\_total{

&#x20; route="/api/v1/appointments"

}\[30d])),1)

```



A successful request is one that the scheduling service processes without an infrastructure or server error.



Expected business rejections should be classified separately rather than counted as service outages.



\### SLO 2 â€” Latency



\*\*SLI formula:\*\*



Latency SLI = requests completed within 1 second / valid requests



\- Target: 95% within 1 second

\- Window: 30 days

\- Scope: POST /api/v1/appointments



Proposed PromQL:



