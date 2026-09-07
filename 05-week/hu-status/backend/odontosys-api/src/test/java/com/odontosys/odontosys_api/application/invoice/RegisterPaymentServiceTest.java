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

import com.odontosys.odontosys_api.application.invoice.command.RegisterPaymentCommand;
import com.odontosys.odontosys_api.application.invoice.response.InvoiceResponse;
import com.odontosys.odontosys_api.domain.exception.InvoiceNotFoundException;
import com.odontosys.odontosys_api.domain.model.Invoice;
import com.odontosys.odontosys_api.domain.model.Patient;
import com.odontosys.odontosys_api.domain.model.Payment;
import com.odontosys.odontosys_api.domain.model.PaymentMethod;
import com.odontosys.odontosys_api.domain.port.out.EmailSenderPort;
import com.odontosys.odontosys_api.domain.port.out.InvoiceRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.PatientRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.PaymentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class RegisterPaymentServiceTest {

    @Mock
    private InvoiceRepositoryPort invoiceRepository;
    @Mock
    private PaymentRepositoryPort paymentRepository;
    @Mock
    private PatientRepositoryPort patientRepository;
    @Mock
    private EmailSenderPort emailSender;

    private RegisterPaymentService registerPaymentService;

    @BeforeEach
    void setUp() {
        registerPaymentService = new RegisterPaymentService(invoiceRepository, paymentRepository, patientRepository, emailSender);
    }

    @Test
    void execute_withValidCommand_registersPaymentAndSendsEmail() {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        RegisterPaymentCommand command = new RegisterPaymentCommand(new BigDecimal("50.00"), PaymentMethod.CASH, null, "Notes");

        Invoice invoice = Invoice.create("INV-00001", patientId, UUID.randomUUID(), List.of(), null, null);
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArguments()[0]);

        Patient patient = Patient.create("John", "Doe", "DNI", "123", "555", "john@example.com", null, "Addr");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        Payment payment = Payment.create(invoiceId, command.amount(), command.paymentMethod(), command.referenceNumber(), command.notes());
        when(paymentRepository.findByInvoiceId(invoiceId)).thenReturn(List.of(payment));

        // Act
        InvoiceResponse response = registerPaymentService.execute(invoiceId, command);

        // Assert
        assertNotNull(response);
        assertEquals(new BigDecimal("50.00"), response.paidAmount());
        assertEquals(1, response.payments().size());

        verify(paymentRepository).save(any(Payment.class));
        verify(emailSender).sendInvoiceReceiptNotification(
                eq("john@example.com"),
                eq("John Doe"),
                eq("INV-00001"),
                eq(BigDecimal.ZERO),
                eq(new BigDecimal("50.00")),
                anyString(),
                anyList(),
                eq("CASH")
        );
    }

    @Test
    void execute_withInvalidInvoice_throwsException() {
        // Arrange
        UUID invoiceId = UUID.randomUUID();
        RegisterPaymentCommand command = new RegisterPaymentCommand(new BigDecimal("50.00"), PaymentMethod.CASH, null, "Notes");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvoiceNotFoundException.class, () -> registerPaymentService.execute(invoiceId, command));
        verifyNoInteractions(paymentRepository, emailSender);
    }
}
