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

import com.odontosys.odontosys_api.application.appointment.command.CreateAppointmentCommand;
import com.odontosys.odontosys_api.application.appointment.response.AppointmentResponse;
import com.odontosys.odontosys_api.domain.exception.PatientNotFoundException;
import com.odontosys.odontosys_api.domain.exception.SlotNotAvailableException;
import com.odontosys.odontosys_api.domain.exception.UserNotFoundException;
import com.odontosys.odontosys_api.domain.model.Appointment;
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
class CreateAppointmentServiceTest {

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

    private CreateAppointmentService createAppointmentService;

    @BeforeEach
    void setUp() {
        createAppointmentService = new CreateAppointmentService(
                appointmentRepository,
                slotRepository,
                patientRepository,
                userRepository,
                emailSender
        );
    }

    @Test
    void execute_withValidCommand_createsAppointmentAndSendsEmail() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, dentistId, slotId, "Checkup");

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User dentist = User.create("dentist@example.com", "hash", "Jane", "Smith", "555-9876", "DNI-DENTIST", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        AvailabilitySlot slot = AvailabilitySlot.create(dentistId, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        Appointment savedAppointment = Appointment.create(patient.getId(), dentist.getId(), slot.getId(), slot.getDate(), slot.getStartTime(), slot.getEndTime(), command.reason());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        AppointmentResponse response = createAppointmentService.execute(command);

        // Assert
        assertNotNull(response);
        verify(slotRepository).save(slotCaptor.capture());
        assertFalse(slotCaptor.getValue().isAvailable()); // Slot should be marked as booked

        verify(appointmentRepository).save(appointmentCaptor.capture());
        assertEquals(patient.getId(), appointmentCaptor.getValue().getPatientId());
        assertEquals(dentist.getId(), appointmentCaptor.getValue().getDentistId());
        assertEquals(slot.getId(), appointmentCaptor.getValue().getSlotId());

        verify(emailSender).sendAppointmentConfirmationNotification(
                eq(patient.getEmail()),
                eq("John Doe"),
                eq("Dr. Jane Smith"),
                eq(slot.getDate()),
                eq(slot.getStartTime())
        );
    }

    @Test
    void execute_withNonexistentPatient_throwsPatientNotFoundException() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, UUID.randomUUID(), UUID.randomUUID(), "Checkup");
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundException.class, () -> createAppointmentService.execute(command));
        verifyNoInteractions(userRepository, slotRepository, appointmentRepository, emailSender);
    }

    @Test
    void execute_withNonexistentDentist_throwsUserNotFoundException() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, dentistId, UUID.randomUUID(), "Checkup");

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userRepository.findById(dentistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> createAppointmentService.execute(command));
        verifyNoInteractions(slotRepository, appointmentRepository, emailSender);
    }

    @Test
    void execute_withUnavailableSlot_throwsSlotNotAvailableException() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, dentistId, slotId, "Checkup");

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User dentist = User.create("dentist@example.com", "hash", "Jane", "Smith", "555-9876", "DNI-DENTIST", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        AvailabilitySlot slot = AvailabilitySlot.create(dentistId, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        slot.markAsBooked(); // Make it unavailable
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        // Act & Assert
        assertThrows(SlotNotAvailableException.class, () -> createAppointmentService.execute(command));
        verifyNoInteractions(appointmentRepository, emailSender);
        verify(slotRepository, never()).save(any());
    }

    @Test
    void execute_withPatientWithoutEmail_skipsEmailNotification() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CreateAppointmentCommand command = new CreateAppointmentCommand(patientId, dentistId, slotId, "Checkup");

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User dentist = User.create("dentist@example.com", "hash", "Jane", "Smith", "555-9876", "DNI-DENTIST", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        AvailabilitySlot slot = AvailabilitySlot.create(dentistId, LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0));
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        Appointment savedAppointment = Appointment.create(patientId, dentistId, slotId, slot.getDate(), slot.getStartTime(), slot.getEndTime(), command.reason());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);

        // Act
        createAppointmentService.execute(command);

        // Assert
        verify(emailSender, never()).sendAppointmentConfirmationNotification(any(), any(), any(), any(), any());
    }
}
