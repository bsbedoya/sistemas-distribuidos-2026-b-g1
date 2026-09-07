package com.odontosys.odontosys_api.application.invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.invoice.response.InvoiceResponse;
import com.odontosys.odontosys_api.domain.exception.MedicalRecordNotFoundException;
import com.odontosys.odontosys_api.domain.model.Invoice;
import com.odontosys.odontosys_api.domain.model.InvoiceItem;
import com.odontosys.odontosys_api.domain.model.MedicalRecord;
import com.odontosys.odontosys_api.domain.model.MedicalRecordProcedureItem;
import com.odontosys.odontosys_api.domain.port.out.InvoiceRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.MedicalRecordRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateInvoiceServiceTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;
    @Mock
    private MedicalRecordRepositoryPort medicalRecordRepository;

    private CreateInvoiceService createInvoiceService;

    @BeforeEach
    void setUp() {
        createInvoiceService = new CreateInvoiceService(invoiceRepository, medicalRecordRepository);
    }

    @Test
    void execute_withValidMedicalRecord_createsInvoice() {
        // Arrange
        UUID medicalRecordId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        MedicalRecordProcedureItem item = MedicalRecordProcedureItem.create(UUID.randomUUID(), "Cleaning", new BigDecimal("50.00"), 11, "Notes");
        MedicalRecord record = MedicalRecord.create(patientId, UUID.randomUUID(), UUID.randomUUID(), "Diagnosis", "Notes", List.of(item));

        when(medicalRecordRepository.findById(medicalRecordId)).thenReturn(Optional.of(record));
        when(invoiceRepository.findByMedicalRecordId(medicalRecordId)).thenReturn(Optional.empty());
        when(invoiceRepository.count()).thenReturn(0L);

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        InvoiceResponse response = createInvoiceService.execute(medicalRecordId);

        // Assert
        assertNotNull(response);
        assertEquals("INV-00001", response.invoiceNumber());
        assertEquals(patientId, response.patientId());
        assertEquals(new BigDecimal("50.00"), response.totalAmount());
        assertEquals(1, response.items().size());

        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void execute_withExistingInvoice_returnsExistingInvoice() {
        // Arrange
        UUID medicalRecordId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        MedicalRecord record = MedicalRecord.create(patientId, UUID.randomUUID(), UUID.randomUUID(), "Diagnosis", "Notes", List.of());

        Invoice existingInvoice = Invoice.create("INV-00001", patientId, medicalRecordId, List.of(), null, null);

        when(medicalRecordRepository.findById(medicalRecordId)).thenReturn(Optional.of(record));
        when(invoiceRepository.findByMedicalRecordId(medicalRecordId)).thenReturn(Optional.of(existingInvoice));

        // Act
        InvoiceResponse response = createInvoiceService.execute(medicalRecordId);

        // Assert
        assertNotNull(response);
        assertEquals("INV-00001", response.invoiceNumber());

        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void execute_withInvalidMedicalRecordId_throwsException() {
        // Arrange
        UUID medicalRecordId = UUID.randomUUID();
        when(medicalRecordRepository.findById(medicalRecordId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MedicalRecordNotFoundException.class, () -> createInvoiceService.execute(medicalRecordId));
        verifyNoInteractions(invoiceRepository);
    }
}
