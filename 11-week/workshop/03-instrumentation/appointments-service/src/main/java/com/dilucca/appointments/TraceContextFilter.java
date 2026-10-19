package com.dilucca.appointments;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

@Component
public class TraceContextFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final MeterRegistry registry;

    public TraceContextFilter(MeterRegistry registry) {
        this.registry = registry;
    }

    private String randomHex(int bytes) {
        byte[] value = new byte[bytes];
        RANDOM.nextBytes(value);
        return HexFormat.of().formatHex(value);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        HttpServletResponse result = (HttpServletResponse) response;

        String incoming = http.getHeader("traceparent");
        String traceId = randomHex(16);
        String flags = "01";

        if (incoming != null &&
            incoming.matches("00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}") &&
            !incoming.substring(3, 35).equals("0".repeat(32)) &&
            !incoming.substring(36, 52).equals("0".repeat(16))) {
            traceId = incoming.substring(3, 35);
            flags = incoming.substring(53, 55);
        }

        String spanId = randomHex(8);
        String context = "00-" + traceId + "-" + spanId + "-" + flags;

        result.setHeader("traceparent", context);
        http.setAttribute("traceparent", context);

        long start = System.nanoTime();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.nanoTime() - start;
            long durationMs = TimeUnit.NANOSECONDS.toMillis(duration);

            String outcome = result.getStatus() < 400
                    ? "success" : "error";

            registry.counter(
                    "appointments.requests",
                    "outcome", outcome
            ).increment();

            Timer.builder("appointments.request.duration")
                    .publishPercentileHistogram()
                    .register(registry)
                    .record(duration, TimeUnit.NANOSECONDS);

            StructuredLogger.log(
                    traceId,
                    spanId,
                    "appointment.request.completed",
                    durationMs
            );
        }
    }
}
