package com.odontosys.odontosys_api.infrastructure.adapter.in.web.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odontosys.odontosys_api.application.appointment.command.CreateAppointmentCommand;
import com.odontosys.odontosys_api.application.appointment.command.RescheduleAppointmentCommand;
import com.odontosys.odontosys_api.application.appointment.response.AppointmentResponse;
import com.odontosys.odontosys_api.domain.model.AppointmentStatus;
import com.odontosys.odontosys_api.domain.port.in.appointment.CancelAppointmentUseCase;
import com.odontosys.odontosys_api.domain.port.in.appointment.CreateAppointmentUseCase;
import com.odontosys.odontosys_api.domain.port.in.appointment.GetAppointmentByIdUseCase;
import com.odontosys.odontosys_api.domain.port.in.appointment.ListAppointmentsUseCase;
import com.odontosys.odontosys_api.domain.port.in.appointment.RescheduleAppointmentUseCase;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.CancelAppointmentRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.CreateAppointmentRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.RescheduleAppointmentRequestDto;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller testing
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateAppointmentUseCase createAppointmentUseCase;
    @MockBean
    private RescheduleAppointmentUseCase rescheduleAppointmentUseCase;
    @MockBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;
    @MockBean
    private GetAppointmentByIdUseCase getAppointmentByIdUseCase;
    @MockBean
    private ListAppointmentsUseCase listAppointmentsUseCase;

    // Mocking beans required by JwtAuthenticationFilter even if filters are disabled
    @MockBean
    private TokenProviderPort jwtTokenProvider;
    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    @WithMockUser
    void createAppointment_returns201() throws Exception {
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CreateAppointmentRequestDto request = new CreateAppointmentRequestDto(patientId, dentistId, slotId, "Checkup");

        AppointmentResponse response = new AppointmentResponse(UUID.randomUUID(), patientId, dentistId, slotId, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup", AppointmentStatus.SCHEDULED, null, Instant.now(), Instant.now());
        when(createAppointmentUseCase.execute(any(CreateAppointmentCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.dentistId").value(dentistId.toString()));
    }

    @Test
    @WithMockUser
    void rescheduleAppointment_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UUID newSlotId = UUID.randomUUID();
        RescheduleAppointmentRequestDto request = new RescheduleAppointmentRequestDto(newSlotId);

        AppointmentResponse response = new AppointmentResponse(id, UUID.randomUUID(), UUID.randomUUID(), newSlotId, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup", AppointmentStatus.CONFIRMED, null, Instant.now(), Instant.now());
        when(rescheduleAppointmentUseCase.execute(eq(id), any(RescheduleAppointmentCommand.class))).thenReturn(response);

        mockMvc.perform(put("/api/appointments/{id}/reschedule", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slotId").value(newSlotId.toString()));
    }

    @Test
    @WithMockUser
    void cancelAppointment_returns204() throws Exception {
        UUID id = UUID.randomUUID();
        CancelAppointmentRequestDto request = new CancelAppointmentRequestDto("Not needed anymore");

        mockMvc.perform(delete("/api/appointments/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void getAppointmentById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        AppointmentResponse response = new AppointmentResponse(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup", AppointmentStatus.SCHEDULED, null, Instant.now(), Instant.now());
        when(getAppointmentByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/api/appointments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    void getAppointments_returns200() throws Exception {
        UUID dentistId = UUID.randomUUID();
        AppointmentResponse response = new AppointmentResponse(UUID.randomUUID(), UUID.randomUUID(), dentistId, UUID.randomUUID(), LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup", AppointmentStatus.SCHEDULED, null, Instant.now(), Instant.now());
        when(listAppointmentsUseCase.execute(eq(dentistId), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/appointments")
                .param("dentistId", dentistId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dentistId").value(dentistId.toString()));
    }
}
