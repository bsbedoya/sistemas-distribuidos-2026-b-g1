package com.odontosys.odontosys_api.application.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.appointment.response.AppointmentResponse;
import com.odontosys.odontosys_api.domain.model.Appointment;
import com.odontosys.odontosys_api.domain.model.AppointmentStatus;
import com.odontosys.odontosys_api.domain.port.out.AppointmentRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ListAppointmentsServiceTest {

    @Mock
    private AppointmentRepositoryPort appointmentRepository;

    private ListAppointmentsService listAppointmentsService;

    @BeforeEach
    void setUp() {
        listAppointmentsService = new ListAppointmentsService(appointmentRepository);
    }

    @Test
    void execute_withFilters_returnsMappedResponses() {
        // Arrange
        UUID dentistId = UUID.randomUUID();
        UUID patientId = null;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        AppointmentStatus status = AppointmentStatus.SCHEDULED;

        Appointment appointment1 = Appointment.create(UUID.randomUUID(), dentistId, UUID.randomUUID(), startDate, LocalTime.of(10, 0), LocalTime.of(11, 0), "Checkup 1");
        Appointment appointment2 = Appointment.create(UUID.randomUUID(), dentistId, UUID.randomUUID(), startDate.plusDays(1), LocalTime.of(14, 0), LocalTime.of(15, 0), "Checkup 2");

        when(appointmentRepository.findByFilters(dentistId, patientId, startDate, endDate, status))
                .thenReturn(List.of(appointment1, appointment2));

        // Act
        List<AppointmentResponse> result = listAppointmentsService.execute(dentistId, patientId, startDate, endDate, status);

        // Assert
        assertEquals(2, result.size());
        assertEquals(appointment1.getPatientId(), result.get(0).patientId());
        assertEquals(appointment2.getPatientId(), result.get(1).patientId());
    }

    @Test
    void execute_withNoMatches_returnsEmptyList() {
        // Arrange
        when(appointmentRepository.findByFilters(any(), any(), any(), any(), any())).thenReturn(List.of());

        // Act
        List<AppointmentResponse> result = listAppointmentsService.execute(null, null, null, null, null);

        // Assert
        assertTrue(result.isEmpty());
    }
}
