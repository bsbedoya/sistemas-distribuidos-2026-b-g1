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
import com.odontosys.odontosys_api.application.procedure.command.CreateProcedureCommand;
import com.odontosys.odontosys_api.application.procedure.command.UpdateProcedureCommand;
import com.odontosys.odontosys_api.application.procedure.response.ProcedureResponse;
import com.odontosys.odontosys_api.domain.port.in.procedure.CreateProcedureUseCase;
import com.odontosys.odontosys_api.domain.port.in.procedure.DeleteProcedureUseCase;
import com.odontosys.odontosys_api.domain.port.in.procedure.GetProcedureByIdUseCase;
import com.odontosys.odontosys_api.domain.port.in.procedure.ListProceduresUseCase;
import com.odontosys.odontosys_api.domain.port.in.procedure.UpdateProcedureUseCase;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.CreateProcedureRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.UpdateProcedureRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProcedureController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProcedureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateProcedureUseCase createProcedureUseCase;

    @MockBean
    private UpdateProcedureUseCase updateProcedureUseCase;

    @MockBean
    private GetProcedureByIdUseCase getProcedureByIdUseCase;

    @MockBean
    private ListProceduresUseCase listProceduresUseCase;

    @MockBean
    private DeleteProcedureUseCase deleteProcedureUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;

    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    @WithMockUser
    void createProcedure_returns201() throws Exception {
        CreateProcedureRequestDto request = new CreateProcedureRequestDto("Consulta", "Revision", BigDecimal.valueOf(50), 30);
        ProcedureResponse response = new ProcedureResponse(
                UUID.randomUUID(), "Consulta", "Revision", BigDecimal.valueOf(50), 30, true, Instant.now(), Instant.now()
        );

        when(createProcedureUseCase.execute(any(CreateProcedureCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/procedures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Consulta"));
    }

    @Test
    @WithMockUser
    void getAllProcedures_returns200() throws Exception {
        ProcedureResponse response = new ProcedureResponse(
                UUID.randomUUID(), "Consulta", "Revision", BigDecimal.valueOf(50), 30, true, Instant.now(), Instant.now()
        );

        when(listProceduresUseCase.execute()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/procedures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Consulta"));
    }

    @Test
    @WithMockUser
    void getProcedureById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        ProcedureResponse response = new ProcedureResponse(
                id, "Consulta", "Revision", BigDecimal.valueOf(50), 30, true, Instant.now(), Instant.now()
        );

        when(getProcedureByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/api/procedures/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Consulta"));
    }

    @Test
    @WithMockUser
    void updateProcedure_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateProcedureRequestDto request = new UpdateProcedureRequestDto("Consulta", "Revision", BigDecimal.valueOf(60), 45);
        ProcedureResponse response = new ProcedureResponse(
                id, "Consulta", "Revision", BigDecimal.valueOf(60), 45, true, Instant.now(), Instant.now()
        );

        when(updateProcedureUseCase.execute(eq(id), any(UpdateProcedureCommand.class))).thenReturn(response);

        mockMvc.perform(put("/api/procedures/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(60));
    }

    @Test
    @WithMockUser
    void deleteProcedure_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/procedures/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteProcedureUseCase).execute(id);
    }
}
