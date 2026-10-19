package com.dilucca.appointments;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TracePropagationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldPreserveTraceIdInDownstreamCall() {

        String traceId = "4bf92f3577b34da6a3ce929d0e0e4736";
        String incoming = "00-" + traceId
                + "-00f067aa0ba902b7-01";

        RestClient client = RestClient.create();

        ResponseEntity<String> response = client.post()
                .uri("http://localhost:" + port + "/demo/forward")
                .header("traceparent", incoming)
                .retrieve()
                .toEntity(String.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        String receivedTraceId = response.getBody().split("-")[1];

        assertEquals(traceId, receivedTraceId);
    }
}
