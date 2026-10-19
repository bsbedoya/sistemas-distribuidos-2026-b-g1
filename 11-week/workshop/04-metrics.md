\# CP4 — Golden Signals and Metric Catalog



\## Project Context



\- Project: DILUCCA

\- Domain: Appointments

\- Critical journey: Dental Appointment Scheduling

\- Entry point: POST /api/v1/appointments

\- Components: API Gateway, appointments-service, PostgreSQL

\- Event associated with the journey: AppointmentScheduled



This catalog defines metrics for monitoring the appointment scheduling journey. The appointments-service has basic Micrometer instrumentation from CP3. Gateway metrics are proposed for future integration.



\## 1. Golden Signals — Appointments Service



| Signal | Metric | Type | Labels |

|---|---|---|---|

| Latency | appointments\_request\_duration\_seconds | Histogram | None currently |

| Traffic | appointments\_requests\_total | Counter | outcome |

| Errors | appointments\_requests\_total | Counter | outcome |

| Saturation | jvm\_memory\_used\_bytes / jvm\_memory\_max\_bytes | Gauge ratio | area=heap |



\### Latency — P95



```promql

histogram\_quantile(

&#x20; 0.95,

&#x20; sum by (le) (

&#x20;   rate(appointments\_request\_duration\_seconds\_bucket\[5m])

&#x20; )

)

```



This query estimates the 95th percentile of request duration over five minutes.



\### Traffic — Requests per second



```promql

sum(rate(appointments\_requests\_total\[5m]))

```



This query measures the incoming request rate.



\### Errors — Error ratio



```promql

sum(rate(appointments\_requests\_total{outcome="error"}\[5m]))

/

clamp\_min(sum(rate(appointments\_requests\_total\[5m])), 0.001)

```



This measures the proportion of requests classified as errors.



\### Saturation — JVM Heap Usage



```promql

sum(jvm\_memory\_used\_bytes{area="heap"})

/

sum(jvm\_memory\_max\_bytes{area="heap"} > 0)

```



This estimates the fraction of configured JVM heap memory currently used.



\## 2. Golden Signals — API Gateway



The following metrics are proposed. They have not been implemented or verified in CP3.



| Signal | Metric | Type | Labels |

|---|---|---|---|

| Latency | gateway\_request\_duration\_seconds | Histogram | route, method |

| Traffic | gateway\_requests\_total | Counter | route, method |

| Errors | gateway\_requests\_total | Counter | route, outcome |

| Saturation | gateway\_active\_requests | Gauge | route |



\### Proposed Gateway Dashboard Queries



Latency P95:



```promql

histogram\_quantile(0.95,

&#x20; sum by (le) (rate(gateway\_request\_duration\_seconds\_bucket\[5m]))

)

```



Traffic:



```promql

sum(rate(gateway\_requests\_total\[5m]))

```



Errors:



```promql

sum(rate(gateway\_requests\_total{outcome="error"}\[5m]))

```



Saturation:



```promql

sum(gateway\_active\_requests)

```



\## 3. Label Cardinality



Cardinality estimates are based on planned instrumentation, not measured production traffic.



| Label | Estimated values | Cardinality |

|---|---:|---|

| outcome | 2: success, error | Low |

| method | 4: GET, POST, PUT, DELETE | Low |

| route | Up to 10 normalized routes | Low |

| area | 2: heap, nonheap | Low |



The estimated combination of route, method and outcome is at most 10 x 4 x 2 = 80 series per relevant metric family, before histogram buckets and other infrastructure labels.



\### Forbidden Labels



Do not use these as Prometheus metric labels:



\- patient\_id or dentist\_id

\- appointment\_id

\- trace\_id or correlation\_id

\- email or document number

\- Raw URLs containing dynamic identifiers



Instead, use normalized route templates such as /api/v1/appointments, a fixed outcome classification and HTTP method.



Identifiers remain in tracing or controlled structured logs when appropriate, without exposing sensitive patient information.



\## 4. Latency Histogram Buckets



Proposed application-level buckets in seconds:



0.05, 0.10, 0.25, 0.50, 1.00, 2.00, 3.00, 5.00



For dental appointment scheduling, the user should receive feedback quickly. The proposed target is to complete most scheduling requests in less than one second. Buckets at 2, 3 and 5 seconds help identify slow operations and possible downstream delays.



CP3 currently uses Micrometer's publishPercentileHistogram(), which creates framework-defined histogram buckets. The explicit bucket boundaries above are a proposal for future configuration and must not be reported as already implemented.



\## 5. Verification and Limitations



CP3 provides a request counter tagged by outcome and a request-duration timer with histogram publishing.



The current demonstration measures HTTP requests handled by the local Spring Boot application, including test endpoints. It does not yet isolate the scheduling route or confirm a successful database transaction.



The API Gateway metrics remain a design proposal.



Before using these metrics for production SLOs, verify the actual Prometheus series, add normalized route labels, distinguish business rejections from server failures, and measure the complete appointment scheduling journey.



