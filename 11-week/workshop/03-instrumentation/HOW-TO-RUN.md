\# CP3 — Instrumentation: How to Run



\## Project Context



Project: DILUCCA  

Domain: Appointments  

Service: appointments-service  

Technology: Spring Boot 3.5.0, Java 21, Micrometer and Prometheus.



This workshop module demonstrates HTTP trace context propagation, structured JSON logging, request counters and latency measurements.



The downstream endpoint is simulated locally for testing purposes.



\## Build



From the appointments-service directory:



```cmd

mvn clean compile

```



\## Run the Service



```cmd

mvn spring-boot:run

```



The application starts on port 8080 by default.



\## Test Trace Propagation



Open another terminal and execute:



```cmd

curl.exe -i -X POST http://localhost:8080/demo/forward -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01"

```



The downstream endpoint should receive the same trace ID.



\## Verify Metrics



Generate a request:



```cmd

curl.exe -i -X POST http://localhost:8080/api/v1/appointments

```



Inspect Prometheus metrics:



```cmd

curl.exe -s http://localhost:8080/actuator/prometheus | findstr appointments\_

```



\## Automated Test



Stop the running application if necessary and execute:



```cmd

mvn clean test

```



Observed test result on October 19, 2026:



```text

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS

```



The automated test verifies that the received trace ID matches the ID propagated to the simulated downstream endpoint.



\## Prometheus Evidence



Paste the actual Prometheus metric output here after running the verification command.



