package com.odontosys.odontosys_api.infrastructure.adapter.in.web.controller;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odontosys.odontosys_api.application.auth.response.UserResponse;
import com.odontosys.odontosys_api.domain.port.in.schedule.CopyDentistScheduleUseCase;
import com.odontosys.odontosys_api.application.schedule.response.AvailabilitySlotResponse;
import com.odontosys.odontosys_api.application.schedule.response.DentistScheduleResponse;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.SlotStatus;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.in.schedule.GenerateSlotsUseCase;
import com.odontosys.odontosys_api.domain.port.in.schedule.GetAvailableSlotsUseCase;
import com.odontosys.odontosys_api.domain.port.in.schedule.GetDentistScheduleUseCase;
import com.odontosys.odontosys_api.domain.port.in.schedule.SetDentistScheduleUseCase;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.GenerateSlotsRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.SetDentistScheduleItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SetDentistScheduleUseCase setDentistScheduleUseCase;

    @MockBean
    private GetDentistScheduleUseCase getDentistScheduleUseCase;

    @MockBean
    private GenerateSlotsUseCase generateSlotsUseCase;

    @MockBean
    private GetAvailableSlotsUseCase getAvailableSlotsUseCase;

    @MockBean
    private UserRepositoryPort userRepository;

    @MockBean
    private CopyDentistScheduleUseCase copyDentistScheduleUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;

    @Test
    @WithMockUser
    void getDentists_returns200() throws Exception {
        User user = User.create("test@example.com", "pass", "John", "Doe", "123", "DOC", Set.of(Role.DENTIST));
        java.lang.reflect.Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, UUID.randomUUID());

        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/schedules/dentists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser
    void copySchedule_returns200() throws Exception {
        ScheduleController.CopyScheduleRequestDto request = new ScheduleController.CopyScheduleRequestDto(
                UUID.randomUUID(), UUID.randomUUID(), false
        );

        DentistScheduleResponse response = new DentistScheduleResponse(
                UUID.randomUUID(), request.targetDentistId(), DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(18, 0), 30, false, null, null, true, Instant.now(), Instant.now()
        );

        when(copyDentistScheduleUseCase.copySchedule(any(), any(), anyBoolean())).thenReturn(List.of(response));

        mockMvc.perform(post("/api/schedules/copy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void setSchedule_returns200() throws Exception {
        UUID dentistId = UUID.randomUUID();
        SetDentistScheduleItemRequestDto request = new SetDentistScheduleItemRequestDto(
                DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, false, null, null
        );

        DentistScheduleResponse response = new DentistScheduleResponse(
                UUID.randomUUID(), dentistId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, false, null, null, true, Instant.now(), Instant.now()
        );

        when(setDentistScheduleUseCase.execute(eq(dentistId), any())).thenReturn(List.of(response));

        mockMvc.perform(post("/api/schedules/dentists/{dentistId}", dentistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));
    }

    @Test
    @WithMockUser
    void getSchedule_returns200() throws Exception {
        UUID dentistId = UUID.randomUUID();
        DentistScheduleResponse response = new DentistScheduleResponse(
                UUID.randomUUID(), dentistId, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0), 30, false, null, null, true, Instant.now(), Instant.now()
        );

        when(getDentistScheduleUseCase.execute(dentistId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/schedules/dentists/{dentistId}", dentistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));
    }

    @Test
    @WithMockUser
    void generateSlots_returns200() throws Exception {
        UUID dentistId = UUID.randomUUID();
        GenerateSlotsRequestDto request = new GenerateSlotsRequestDto(dentistId, LocalDate.now(), LocalDate.now().plusDays(1));
        AvailabilitySlotResponse response = new AvailabilitySlotResponse(
                UUID.randomUUID(), dentistId, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(9, 30), SlotStatus.AVAILABLE, Instant.now()
        );

        when(generateSlotsUseCase.execute(eq(dentistId), any(), any())).thenReturn(List.of(response));

        mockMvc.perform(post("/api/schedules/dentists/{dentistId}/generate-slots", dentistId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"));
    }

    @Test
    @WithMockUser
    void getAvailableSlots_returns200() throws Exception {
        UUID dentistId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        AvailabilitySlotResponse response = new AvailabilitySlotResponse(
                UUID.randomUUID(), dentistId, today, LocalTime.of(9, 0), LocalTime.of(9, 30), SlotStatus.AVAILABLE, Instant.now()
        );

        when(getAvailableSlotsUseCase.execute(eq(dentistId), any(), any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/schedules/dentists/{dentistId}/slots", dentistId)
                .param("startDate", today.toString())
                .param("endDate", today.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startTime").value("09:00:00"));
    }
}
