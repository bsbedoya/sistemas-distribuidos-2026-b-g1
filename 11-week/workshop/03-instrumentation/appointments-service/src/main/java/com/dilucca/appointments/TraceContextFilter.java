package com.dilucca.appointments;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Component
public class TraceContextFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();

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

        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long duration = (System.nanoTime() - start) / 1_000_000;
            StructuredLogger.log(traceId, spanId,
                    "appointment.request.completed", duration);
        }
    }
}
