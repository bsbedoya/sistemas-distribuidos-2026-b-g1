package com.odontosys.odontosys_api.application.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.domain.exception.AppointmentNotFoundException;
import com.odontosys.odontosys_api.domain.model.Appointment;
import com.odontosys.odontosys_api.domain.model.AppointmentStatus;
import com.odontosys.odontosys_api.domain.model.AvailabilitySlot;
import com.odontosys.odontosys_api.domain.model.Patient;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.AppointmentRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.AvailabilitySlotRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.EmailSenderPort;
import com.odontosys.odontosys_api.domain.port.out.PatientRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CancelAppointmentServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;
    @Mock
    private AvailabilitySlotRepositoryPort slotRepository;
    @Mock
    private PatientRepositoryPort patientRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private EmailSenderPort emailSender;

    @Captor
    private ArgumentCaptor<Appointment> appointmentCaptor;
    @Captor
    private ArgumentCaptor<AvailabilitySlot> slotCaptor;

    private CancelAppointmentService cancelAppointmentService;

    @BeforeEach
    void setUp() {
        cancelAppointmentService = new CancelAppointmentService(
                appointmentRepository,
                slotRepository,
                patientRepository,
                userRepository,
                emailSender
        );
    }

    @Test
    void execute_withExistingAppointment_cancelsAndReleasesSlot() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        String cancelReason = "Paciente no puede asistir";

        Appointment appointment = Appointment.create(patientId, dentistId, slotId, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup");
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AvailabilitySlot slot = AvailabilitySlot.create(dentistId, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        slot.markAsBooked();
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User dentist = User.create("dentist@example.com", "hash", "Jane", "Smith", "555-9876", "DNI-DENTIST", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        // Act
        cancelAppointmentService.execute(appointmentId, cancelReason);

        // Assert
        verify(slotRepository).save(slotCaptor.capture());
        assertTrue(slotCaptor.getValue().isAvailable());

        verify(appointmentRepository).save(appointmentCaptor.capture());
        assertEquals(AppointmentStatus.CANCELLED, appointmentCaptor.getValue().getStatus());
        assertTrue(appointmentCaptor.getValue().getNotes().contains(cancelReason));

        verify(emailSender).sendAppointmentCancelledNotification(
                eq(patient.getEmail()),
                eq("John Doe"),
                eq("Dr. Jane Smith"),
                eq(appointment.getAppointmentDate()),
                eq(appointment.getStartTime()),
                eq(cancelReason)
        );
    }

    @Test
    void execute_withNonexistentId_throwsAppointmentNotFoundException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class, () -> cancelAppointmentService.execute(appointmentId, "Reason"));
        verifyNoInteractions(slotRepository, patientRepository, userRepository, emailSender);
        verify(appointmentRepository, never()).save(any());
    }
}
