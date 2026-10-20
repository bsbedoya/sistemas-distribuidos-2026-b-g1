# CP6 — SLIs, SLOs and Error Budget

## Project Context

- Project: DILUCCA
- Domain: Appointments
- Journey: Dental Appointment Scheduling
- Endpoint: POST /api/v1/appointments
- Service: appointments-service
- Dependencies: API Gateway and PostgreSQL
- Measurement window: Rolling 30 days

## 1. Service Level Objectives

The following SLOs are proposed targets for the DILUCCA appointment scheduling journey. They are not verified production performance results.

### SLO 1 — Availability

**SLI formula:**

Availability = successful valid requests / total valid requests

- Target: 99.5%
- Window: 30 days
- Scope: POST /api/v1/appointments

Proposed PromQL:

```promql
sum(increase(appointments_requests_total{
  route="/api/v1/appointments",outcome="success"
}[30d]))
/
clamp_min(sum(increase(appointments_requests_total{
  route="/api/v1/appointments"
}[30d])),1)
```

A successful request is one that the scheduling service processes without an infrastructure or server error.

Expected business rejections should be classified separately rather than counted as service outages. The current prototype only distinguishes success and error, so this classification requires further instrumentation.

### SLO 2 — Latency

**SLI formula:**

Latency SLI = requests completed within 1 second / valid requests

- Target: 95% within 1 second
- Window: 30 days
- Scope: POST /api/v1/appointments

Proposed PromQL:

```promql
sum(increase(appointments_request_duration_seconds_bucket{
  route="/api/v1/appointments",le="1.0"
}[30d]))
/
clamp_min(sum(increase(appointments_request_duration_seconds_count{
  route="/api/v1/appointments"
}[30d])),1)
```

Fast responses help patients schedule dental appointments without unnecessary waiting.

A bucket with `le="1.0"` must exist in the configured histogram. The route label is also required before this query can be used.

### SLO 3 — JVM Memory Stability

**SLI formula:**

Memory stability = healthy memory samples / total valid samples

- Target: 95% of samples below 85% heap utilization
- Window: 30 days
- Sampling interval: 5 minutes

Proposed PromQL:

```promql
avg_over_time(
  (
    (
      sum(jvm_memory_used_bytes{area="heap"})
      /
      sum(jvm_memory_max_bytes{area="heap"})
    ) < bool 0.85
  )[30d:5m]
)
```

This is a supporting infrastructure SLO. It helps detect memory pressure that may affect appointment scheduling.

It does not prove that an appointment was stored successfully. A future product-specific SLO could measure successfully persisted appointments without duplicate bookings.

## 2. Traffic Estimate

The following values are an illustrative planning scenario, not measured DILUCCA production traffic.

- Estimated requests per day: 1,000
- Measurement period: 30 days
- Estimated monthly requests: 30,000

The estimate assumes a small deployment with repeated appointment scheduling activity throughout the day.

This assumption must be reviewed against actual clinic traffic before adopting the SLO.

## 3. Availability Error Budget

**Availability target:** 99.5%

**Allowed error percentage:**

100% - 99.5% = 0.5%

**Allowed failed requests:**

30,000 × 0.005 = 150 requests

**Equivalent full-outage time:**

30 days × 24 hours × 60 minutes × 0.005 = 216 minutes

This equals **3 hours and 36 minutes**.

The minute estimate assumes continuous service availability is approximated by time and that a complete outage affects all requests uniformly. Partial failures are measured more accurately using requests.

## 4. Error Budget Consumption Policy

| Budget consumed | Failed requests | Team action |
|---|---:|---|
| 50% | 75 | Review traces, error metrics, and recent changes. |
| 75% | 113 | Prioritize reliability fixes and restrict risky releases. |
| 100% | 150 | Pause nonessential releases and focus on recovery. |

At 75% consumption, the mathematical threshold is 112.5 requests, so it is crossed once at least 113 failures have occurred.

The team should review PostgreSQL connectivity, scheduling conflicts, API Gateway errors, and appointments-service performance.

### Policy Details

**At 50% consumed:**
Investigate recurring errors, review trace propagation, and identify the most frequent failure patterns.

**At 75% consumed:**
Prioritize system stabilization, review recent deployments, and avoid changes that could increase the failure rate.

**At 100% consumed:**
Stop nonessential releases, focus on restoring reliability, and verify the service before resuming normal deployment activities.

## 5. Connection to CP3 and CP4

CP3 implemented a request counter and a latency histogram using Micrometer.

CP4 documented these metrics and proposed additional gateway measurements.

### Current Prototype Limitations

- The request counter currently labels outcome but not route.
- Current outcome classification does not distinguish expected business rejections.
- Histogram bucket boundaries require verification.
- The demonstration does not perform real appointment persistence.
- The SLO queries are proposed for production instrumentation.

Before using these queries in production, add bounded route labels, validate Prometheus series, and integrate metrics with the actual scheduling journey.

## 6. Conclusion

The three proposed SLOs help establish reliability expectations for the DILUCCA Appointments service.

Availability measures whether valid requests are processed correctly, latency evaluates response times, and memory stability helps identify resource pressure.

The calculated error budget provides a practical limit for failures and supports decisions about incident response and release stability.

These objectives are a starting point and must be validated using real traffic and production metrics.
