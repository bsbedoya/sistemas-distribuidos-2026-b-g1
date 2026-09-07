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
import com.odontosys.odontosys_api.application.invoice.command.RegisterPaymentCommand;
import com.odontosys.odontosys_api.application.invoice.response.InvoiceResponse;
import com.odontosys.odontosys_api.domain.model.InvoiceStatus;
import com.odontosys.odontosys_api.domain.model.PaymentMethod;
import com.odontosys.odontosys_api.domain.port.in.invoice.CreateInvoiceFromMedicalRecordUseCase;
import com.odontosys.odontosys_api.domain.port.in.invoice.GetInvoiceByIdUseCase;
import com.odontosys.odontosys_api.domain.port.in.invoice.ListInvoicesUseCase;
import com.odontosys.odontosys_api.domain.port.in.invoice.RegisterPaymentUseCase;
import com.odontosys.odontosys_api.domain.port.in.invoice.SendInvoiceEmailUseCase;
import com.odontosys.odontosys_api.domain.port.in.invoice.UpdateInvoiceUseCase;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.RegisterPaymentRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.UpdateInvoiceRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateInvoiceFromMedicalRecordUseCase createInvoiceFromMedicalRecordUseCase;

    @MockBean
    private RegisterPaymentUseCase registerPaymentUseCase;

    @MockBean
    private GetInvoiceByIdUseCase getInvoiceByIdUseCase;

    @MockBean
    private ListInvoicesUseCase listInvoicesUseCase;

    @MockBean
    private SendInvoiceEmailUseCase sendInvoiceEmailUseCase;

    @MockBean
    private UpdateInvoiceUseCase updateInvoiceUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;

    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    @WithMockUser
    void createInvoiceFromMedicalRecord_returns201() throws Exception {
        UUID medicalRecordId = UUID.randomUUID();
        InvoiceResponse response = new InvoiceResponse(
                UUID.randomUUID(), "INV-123", UUID.randomUUID(), medicalRecordId,
                Instant.now(), BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100),
                BigDecimal.ZERO, InvoiceStatus.PENDING, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(createInvoiceFromMedicalRecordUseCase.execute(medicalRecordId)).thenReturn(response);

        mockMvc.perform(post("/api/invoices/from-medical-record/{medicalRecordId}", medicalRecordId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-123"));
    }

    @Test
    @WithMockUser
    void registerPayment_returns200() throws Exception {
        UUID invoiceId = UUID.randomUUID();
        RegisterPaymentRequestDto request = new RegisterPaymentRequestDto(BigDecimal.valueOf(100), PaymentMethod.CASH, null, null);
        InvoiceResponse response = new InvoiceResponse(
                invoiceId, "INV-123", UUID.randomUUID(), UUID.randomUUID(),
                Instant.now(), BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100),
                BigDecimal.valueOf(100), InvoiceStatus.PAID, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(registerPaymentUseCase.execute(eq(invoiceId), any(RegisterPaymentCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/invoices/{invoiceId}/payments", invoiceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @WithMockUser
    void sendInvoiceEmail_returns204() throws Exception {
        UUID invoiceId = UUID.randomUUID();

        mockMvc.perform(post("/api/invoices/{invoiceId}/send-email", invoiceId))
                .andExpect(status().isNoContent());

        verify(sendInvoiceEmailUseCase).execute(invoiceId);
    }

    @Test
    @WithMockUser
    void getInvoiceById_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        InvoiceResponse response = new InvoiceResponse(
                id, "INV-123", UUID.randomUUID(), UUID.randomUUID(),
                Instant.now(), BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100),
                BigDecimal.ZERO, InvoiceStatus.PENDING, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(getInvoiceByIdUseCase.execute(id)).thenReturn(response);

        mockMvc.perform(get("/api/invoices/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceNumber").value("INV-123"));
    }

    @Test
    @WithMockUser
    void getInvoices_returns200() throws Exception {
        InvoiceResponse response = new InvoiceResponse(
                UUID.randomUUID(), "INV-123", UUID.randomUUID(), UUID.randomUUID(),
                Instant.now(), BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100),
                BigDecimal.ZERO, InvoiceStatus.PENDING, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(listInvoicesUseCase.execute(null, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].invoiceNumber").value("INV-123"));
    }

    @Test
    @WithMockUser
    void updateInvoice_returns200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateInvoiceRequestDto request = new UpdateInvoiceRequestDto(InvoiceStatus.PAID, BigDecimal.valueOf(100));
        InvoiceResponse response = new InvoiceResponse(
                id, "INV-123", UUID.randomUUID(), UUID.randomUUID(),
                Instant.now(), BigDecimal.valueOf(100), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.valueOf(100),
                BigDecimal.valueOf(100), InvoiceStatus.PAID, List.of(), List.of(), Instant.now(), Instant.now()
        );

        when(updateInvoiceUseCase.execute(id, InvoiceStatus.PAID, BigDecimal.valueOf(100))).thenReturn(response);

        mockMvc.perform(put("/api/invoices/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }
}
