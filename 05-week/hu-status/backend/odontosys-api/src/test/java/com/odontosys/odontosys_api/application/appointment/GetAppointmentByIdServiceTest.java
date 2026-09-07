package com.odontosys.odontosys_api.application.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.appointment.response.AppointmentResponse;
import com.odontosys.odontosys_api.domain.exception.AppointmentNotFoundException;
import com.odontosys.odontosys_api.domain.model.Appointment;
import com.odontosys.odontosys_api.domain.port.out.AppointmentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GetAppointmentByIdServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;

    private GetAppointmentByIdService getAppointmentByIdService;

    @BeforeEach
    void setUp() {
        getAppointmentByIdService = new GetAppointmentByIdService(appointmentRepository);
    }

    @Test
    void execute_withExistingId_returnsResponse() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        Appointment appointment = Appointment.create(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup");
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // Act
        AppointmentResponse response = getAppointmentByIdService.execute(appointmentId);

        // Assert
        assertNotNull(response);
        assertEquals(appointment.getPatientId(), response.patientId());
        assertEquals(appointment.getDentistId(), response.dentistId());
    }

    @Test
    void execute_withNonexistentId_throwsException() {
        // Arrange
        UUID appointmentId = UUID.randomUUID();
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AppointmentNotFoundException.class, () -> getAppointmentByIdService.execute(appointmentId));
    }
}
