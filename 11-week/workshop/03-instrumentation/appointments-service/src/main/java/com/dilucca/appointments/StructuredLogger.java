package com.dilucca.appointments;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class StructuredLogger {

    private static final ObjectMapper JSON = new ObjectMapper();

    public static void log(String traceId, String spanId,
                           String event, long durationMs) {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("timestamp", Instant.now().toString());
        record.put("level", "INFO");
        record.put("service", "appointments-service");
        record.put("version", "0.1.0-SNAPSHOT");
        record.put("environment", "local");
        record.put("trace_id", traceId);
        record.put("span_id", spanId);
        record.put("correlation_id", traceId);
        record.put("event", event);
        record.put("message", "Appointment request processed");
        record.put("duration_ms", durationMs);

        try {
            System.out.println(JSON.writeValueAsString(record));
        } catch (Exception exception) {
            throw new IllegalStateException("Log serialization failed", exception);
        }
    }
}
