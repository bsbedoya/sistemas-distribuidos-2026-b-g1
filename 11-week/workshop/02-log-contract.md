
# CP2 - Structured Log Contract

## 1. Overview

**Project:** DILUCCA  
**Service:** appointments-service  
**Journey:** Dental Appointment Scheduling  
**Endpoint:** POST /api/v1/appointments

This document defines a structured JSON logging contract
for the dental appointment scheduling process.

The objective is to correlate operations across services,
identify failures, and improve troubleshooting without
exposing confidential information.

## 2. Required Log Fields

| Field | Type | Required | Example | Purpose |
|---|---|---|---|---|
| timestamp | string | Yes | 2026-10-19T14:00:00Z | UTC event time |
| level | string | Yes | INFO | Log severity |
| service | string | Yes | appointments-service | Source service |
| version | string | Yes | 2.0.1 | Application version |
| environment | string | Yes | qa | Deployment environment |
| trace_id | string | Yes | 4bf92f3577b34da6a3ce929d0e0e4736 | Distributed trace |
| span_id | string | Yes | 00f067aa0ba902b7 | Current span |
| correlation_id | string | Yes | apt-request-001 | Request correlation |
| event | string | Yes | appointment.requested | Event name |
| message | string | Yes | Request received | Event description |
| duration_ms | number | Yes | 120 | Operation duration |

## 3. Appointment Domain Fields

| Field | Type | Example | Purpose |
|---|---|---|---|
| appointment_id | string | apt-001 | Appointment reference |
| dentist_id | string | dentist-001 | Dentist reference |
| appointment_status | string | PROGRAMADA | Appointment state |
| error_code | string | APPOINTMENT_CONFLICT | Business error |

These fields provide domain context for troubleshooting.
Identifiers must not contain personal information.

## 4. Never Log Policy

The following information must not appear in logs:

| Sensitive data | Protection |
|---|---|
| Patient names | Omit completely |
| Identity documents | Mask or omit |
| Authentication tokens | Omit completely |
| Passwords | Never record |
| Clinical information | Omit completely |
| Payment information | Mask or omit |
| HTTP request bodies | Record only approved fields |

Logs must use safe technical identifiers and predefined
event names instead of personal or clinical content.

## 5. JSON Log Examples

The following seven entries simulate one failed
scheduling request. All entries share the same trace_id.

### Log 1 - Request received

```json
{"timestamp":"2026-10-19T14:00:00.000Z","level":"INFO","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000001","correlation_id":"apt-request-001","event":"appointment.requested","message":"Scheduling request received","duration_ms":0,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"NONE"}
```

### Log 2 - Validation started

```json
{"timestamp":"2026-10-19T14:00:00.010Z","level":"INFO","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000002","correlation_id":"apt-request-001","event":"appointment.validation.started","message":"Appointment validation started","duration_ms":10,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"NONE"}
```

### Log 3 - Availability checked

```json
{"timestamp":"2026-10-19T14:00:00.040Z","level":"DEBUG","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000003","correlation_id":"apt-request-001","event":"appointment.availability.checked","message":"Availability check completed","duration_ms":30,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"NONE"}
```

### Log 4 - Downstream request

```json
{"timestamp":"2026-10-19T14:00:00.050Z","level":"INFO","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000004","correlation_id":"apt-request-001","event":"patient.validation.requested","message":"Patient validation requested","duration_ms":0,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"NONE"}
```

### Log 5 - Downstream timeout

```json
{"timestamp":"2026-10-19T14:00:03.050Z","level":"ERROR","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000004","correlation_id":"apt-request-001","event":"patient.validation.timeout","message":"Patient validation dependency timed out","duration_ms":3000,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"DEPENDENCY_TIMEOUT"}
```

### Log 6 - Business rule rejection

```json
{"timestamp":"2026-10-19T14:00:03.060Z","level":"WARN","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000005","correlation_id":"apt-request-001","event":"appointment.business_rule.rejected","message":"Scheduling cannot continue without patient validation","duration_ms":3010,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"BUSINESS_RULE_VIOLATION"}
```

### Log 7 - Request completed

```json
{"timestamp":"2026-10-19T14:00:03.070Z","level":"INFO","service":"appointments-service","version":"2.0.1","environment":"qa","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"0000000000000001","correlation_id":"apt-request-001","event":"appointment.request.completed","message":"Scheduling request rejected","duration_ms":3070,"appointment_id":"pending","dentist_id":"dentist-001","appointment_status":"PENDING","error_code":"BUSINESS_RULE_VIOLATION"}
```

These examples describe a simulated failure scenario.
The downstream timeout, rejection behavior, and event
names require validation against the actual implementation.

## 6. Logging Levels

| Level | Usage | DILUCCA example |
|---|---|---|
| ERROR | Technical failures requiring investigation | Patient service timeout |
| WARN | Rejected operations or recoverable problems | Appointment conflict |
| INFO | Important business operations | Appointment scheduling request |
| DEBUG | Technical diagnostic details | Availability validation completed |

## 7. Validation

- All seven entries use the same trace_id.
- Timestamps are ordered chronologically.
- Each entry contains all required fields.
- Sensitive information is excluded.
- Technical failures and business rejections are identifiable.
- The JSON format can be filtered by trace_id.
