package com.dilucca.appointments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class AppointmentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentsApplication.class, args);
    }

    @PostMapping("/api/v1/appointments")
    public String scheduleAppointment() {
        return "Appointment scheduling request received";
    }
}
