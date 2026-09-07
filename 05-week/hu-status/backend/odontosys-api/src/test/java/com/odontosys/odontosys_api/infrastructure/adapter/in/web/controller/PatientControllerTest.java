package com.odontosys.odontosys_api.infrastructure.adapter.in.web.controller;

import java.time.Instant;
import java.time.LocalDate;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odontosys.odontosys_api.application.patient.command.CreatePatientCommand;
import com.odontosys.odontosys_api.application.patient.command.UpdatePatientCommand;
import com.odontosys.odontosys_api.application.patient.response.PatientResponse;
import com.odontosys.odontosys_api.domain.port.in.patient.CreatePatientUseCase;
import com.odontosys.odontosys_api.domain.port.in.patient.DeletePatientUseCase;
import com.odontosys.odontosys_api.domain.port.in.patient.GetPatientByIdUseCase;
import com.odontosys.odontosys_api.domain.port.in.patient.ListPatientsUseCase;
import com.odontosys.odontosys_api.domain.port.in.patient.UpdatePatientUseCase;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.CreatePatientRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.UpdatePatientRequestDto;
import com.odontosys.odontosys_api.infrastructure.security.JwtTokenProvider;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreatePatientUseCase createPatientUseCase;
    @MockBean
    private UpdatePatientUseCase updatePatientUseCase;
    @MockBean
    private GetPatientByIdUseCase getPatientByIdUseCase;
    @MockBean
    private ListPatientsUseCase listPatientsUseCase;
    @MockBean
    private DeletePatientUseCase deletePatientUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;
    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    @WithMockUser
    void createPatient_returns201() throws Exception {
        CreatePatientRequestDto request = new CreatePatientRequestDto("Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", LocalDate.of(1990, 1, 1), "Addr");
        PatientResponse response = new PatientResponse(UUID.randomUUID(), "Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", LocalDate.of(1990, 1, 1), "Addr", true, Instant.now(), Instant.now());

        when(createPatientUseCase.execute(any(CreatePatientCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    @WithMockUser
    void getAllPatients_returns200() throws Exception {
        PatientResponse response = new PatientResponse(UUID.randomUUID(), "Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", LocalDate.of(1990, 1, 1), "Addr", true, Instant.now(), Instant.now());

        when(listPatientsUseCase.execute()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test
    @WithMockUser
    void getPatientById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        PatientResponse response = new PatientResponse(id, "Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", LocalDate.of(1990, 1, 1), "Addr", true, Instant.now(), Instant.now());

        when(getPatientByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/api/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser
    void updatePatient_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePatientRequestDto request = new UpdatePatientRequestDto("Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", null, "Addr");
        PatientResponse response = new PatientResponse(id, "Jane", "Doe", "DNI", "123456", "555-1234", "jane@example.com", LocalDate.of(1990, 1, 1), "Addr", true, Instant.now(), Instant.now());

        when(updatePatientUseCase.execute(eq(id), any(UpdatePatientCommand.class))).thenReturn(response);

        mockMvc.perform(put("/api/patients/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @WithMockUser
    void deletePatient_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/patients/{id}", id))
                .andExpect(status().isNoContent());

        verify(deletePatientUseCase).execute(id);
    }
}
