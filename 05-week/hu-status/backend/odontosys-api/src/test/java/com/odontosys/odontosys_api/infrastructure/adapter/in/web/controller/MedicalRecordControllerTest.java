package com.odontosys.odontosys_api.infrastructure.adapter.in.web.controller;

import java.math.BigDecimal;
import java.time.Instant;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odontosys.odontosys_api.application.medicalrecord.command.CreateMedicalRecordCommand;
import com.odontosys.odontosys_api.application.medicalrecord.command.UpdateMedicalRecordCommand;
import com.odontosys.odontosys_api.application.medicalrecord.response.MedicalRecordResponse;
import com.odontosys.odontosys_api.domain.port.in.medicalrecord.CreateMedicalRecordUseCase;
import com.odontosys.odontosys_api.domain.port.in.medicalrecord.GetMedicalRecordByIdUseCase;
import com.odontosys.odontosys_api.domain.port.in.medicalrecord.GetPatientMedicalHistoryUseCase;
import com.odontosys.odontosys_api.domain.port.in.medicalrecord.UpdateMedicalRecordUseCase;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.CreateMedicalRecordRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.UpdateMedicalRecordRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateMedicalRecordUseCase createMedicalRecordUseCase;

    @MockBean
    private GetMedicalRecordByIdUseCase getMedicalRecordByIdUseCase;

    @MockBean
    private GetPatientMedicalHistoryUseCase getPatientMedicalHistoryUseCase;

    @MockBean
    private UpdateMedicalRecordUseCase updateMedicalRecordUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;

    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    @WithMockUser
    void createRecord_returns201() throws Exception {
        UUID patientId = UUID.randomUUID();
        CreateMedicalRecordRequestDto request = new CreateMedicalRecordRequestDto(
                patientId, UUID.randomUUID(), UUID.randomUUID(), "Diagnosis", "Notes", List.of()
        );

        MedicalRecordResponse response = new MedicalRecordResponse(
                UUID.randomUUID(), patientId, UUID.randomUUID(), UUID.randomUUID(),
                "Diagnosis", "Notes", List.of(), BigDecimal.ZERO, Instant.now(), Instant.now()
        );

        when(createMedicalRecordUseCase.execute(any(CreateMedicalRecordCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.diagnosis").value("Diagnosis"));
    }

    @Test
    @WithMockUser
    void getPatientHistory_returns200() throws Exception {
        UUID patientId = UUID.randomUUID();
        MedicalRecordResponse response = new MedicalRecordResponse(
                UUID.randomUUID(), patientId, UUID.randomUUID(), UUID.randomUUID(),
                "Diagnosis", "Notes", List.of(), BigDecimal.ZERO, Instant.now(), Instant.now()
        );

        when(getPatientMedicalHistoryUseCase.execute(patientId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/medical-records/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].diagnosis").value("Diagnosis"));
    }

    @Test
    @WithMockUser
    void getRecordById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        MedicalRecordResponse response = new MedicalRecordResponse(
                id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Diagnosis", "Notes", List.of(), BigDecimal.ZERO, Instant.now(), Instant.now()
        );

        when(getMedicalRecordByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/api/medical-records/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("Diagnosis"));
    }

    @Test
    @WithMockUser
    void updateRecord_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateMedicalRecordRequestDto request = new UpdateMedicalRecordRequestDto("New Diagnosis", "New Notes", List.of());
        MedicalRecordResponse response = new MedicalRecordResponse(
                id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "New Diagnosis", "New Notes", List.of(), BigDecimal.ZERO, Instant.now(), Instant.now()
        );

        when(updateMedicalRecordUseCase.execute(eq(id), any(UpdateMedicalRecordCommand.class))).thenReturn(response);

        mockMvc.perform(put("/api/medical-records/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnosis").value("New Diagnosis"));
    }
}
