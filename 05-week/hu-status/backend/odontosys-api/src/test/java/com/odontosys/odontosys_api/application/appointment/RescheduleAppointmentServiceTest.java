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

import com.odontosys.odontosys_api.application.appointment.command.RescheduleAppointmentCommand;
import com.odontosys.odontosys_api.application.appointment.response.AppointmentResponse;
import com.odontosys.odontosys_api.domain.exception.AppointmentNotFoundException;
import com.odontosys.odontosys_api.domain.exception.SlotNotAvailableException;
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
class RescheduleAppointmentServiceTest {

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

    private RescheduleAppointmentService rescheduleAppointmentService;

    @BeforeEach
    void setUp() {
        rescheduleAppointmentService = new RescheduleAppointmentService(
                appointmentRepository,
                slotRepository,
                patientRepository,
                userRepository,
                emailSender
        );
    }

    @Test
    void execute_withValidNewSlot_reschedulesAndNotifies() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID oldSlotId = UUID.randomUUID();
        UUID newSlotId = UUID.randomUUID();
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(newSlotId);

        Appointment appointment = Appointment.create(patientId, dentistId, oldSlotId, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup");
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AvailabilitySlot oldSlot = AvailabilitySlot.create(dentistId, LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));
        oldSlot.markAsBooked();
        when(slotRepository.findById(oldSlotId)).thenReturn(Optional.of(oldSlot));

        AvailabilitySlot newSlot = AvailabilitySlot.create(dentistId, LocalDate.now().plusDays(1), LocalTime.of(15, 0), LocalTime.of(16, 0));
        when(slotRepository.findById(newSlotId)).thenReturn(Optional.of(newSlot));

        Patient patient = Patient.create("John", "Doe", "DNI", "123456", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User dentist = User.create("dentist@example.com", "hash", "Jane", "Smith", "555-9876", "DNI-DENTIST", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(dentist));

        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        AppointmentResponse response = rescheduleAppointmentService.execute(appointmentId, command);

        // Assert
        assertNotNull(response);
        assertEquals(newSlot.getId(), response.slotId());
        assertEquals(newSlot.getDate(), response.appointmentDate());
        assertEquals(AppointmentStatus.CONFIRMED, response.status());

        verify(slotRepository, times(2)).save(slotCaptor.capture());
        assertTrue(slotCaptor.getAllValues().get(0).isAvailable()); // Old slot
        assertFalse(slotCaptor.getAllValues().get(1).isAvailable()); // New slot

        verify(emailSender).sendAppointmentRescheduledNotification(
                eq(patient.getEmail()),
                eq("John Doe"),
                eq("Dr. Jane Smith"),
                eq(newSlot.getDate()),
                eq(newSlot.getStartTime())
        );
    }

    @Test
    void execute_withUnavailableNewSlot_throwsSlotNotAvailableException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        UUID newSlotId = UUID.randomUUID();
        RescheduleAppointmentCommand command = new RescheduleAppointmentCommand(newSlotId);

        Appointment appointment = Appointment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup");
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AvailabilitySlot newSlot = AvailabilitySlot.create(UUID.randomUUID(), LocalDate.now().plusDays(1), LocalTime.of(15, 0), LocalTime.of(16, 0));
        newSlot.markAsBooked(); // Make it unavailable
        when(slotRepository.findById(newSlotId)).thenReturn(Optional.of(newSlot));

        // Act & Assert
        assertThrows(SlotNotAvailableException.class, () -> rescheduleAppointmentService.execute(appointmentId, command));
        verify(appointmentRepository, never()).save(any());
        verifyNoInteractions(emailSender);
    }
}
