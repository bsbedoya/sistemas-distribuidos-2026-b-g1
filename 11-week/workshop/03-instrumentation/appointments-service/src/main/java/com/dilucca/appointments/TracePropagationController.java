package com.dilucca.appointments;

import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
public class TracePropagationController {

    private final RestClient client = RestClient.create();
    private final WebServerApplicationContext server;

    public TracePropagationController(WebServerApplicationContext server) {
        this.server = server;
    }

    @PostMapping("/demo/forward")
    public ResponseEntity<String> forward(
            @RequestAttribute("traceparent") String traceparent) {

        int port = server.getWebServer().getPort();

        String downstreamTraceparent = client.post()
                .uri("http://localhost:" + port + "/demo/downstream")
                .header("traceparent", traceparent)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(downstreamTraceparent);
    }

    @PostMapping("/demo/downstream")
    public String downstream(
            @RequestHeader("traceparent") String traceparent) {

        return traceparent;
    }
}
