package com.odontosys.odontosys_api.application.medicalrecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.medicalrecord.command.CreateMedicalRecordCommand;
import com.odontosys.odontosys_api.application.medicalrecord.command.MedicalRecordProcedureItemCommand;
import com.odontosys.odontosys_api.application.medicalrecord.response.MedicalRecordResponse;
import com.odontosys.odontosys_api.domain.exception.PatientNotFoundException;
import com.odontosys.odontosys_api.domain.model.Appointment;
import com.odontosys.odontosys_api.domain.model.AppointmentStatus;
import com.odontosys.odontosys_api.domain.model.MedicalRecord;
import com.odontosys.odontosys_api.domain.model.Patient;
import com.odontosys.odontosys_api.domain.model.Procedure;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.in.invoice.CreateInvoiceFromMedicalRecordUseCase;
import com.odontosys.odontosys_api.domain.port.out.AppointmentRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.MedicalRecordRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.PatientRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.ProcedureRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateMedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepositoryPort medicalRecordRepository;
    @Mock
    private PatientRepositoryPort patientRepository;
    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private ProcedureRepositoryPort procedureRepository;
    @Mock
    private AppointmentRepositoryPort appointmentRepository;
    @Mock
    private CreateInvoiceFromMedicalRecordUseCase createInvoiceUseCase;

    private CreateMedicalRecordService createMedicalRecordService;

    @BeforeEach
    void setUp() {
        createMedicalRecordService = new CreateMedicalRecordService(
                medicalRecordRepository,
                patientRepository,
                userRepository,
                procedureRepository,
                appointmentRepository,
                createInvoiceUseCase
        );
    }

    @Test
    void execute_withValidCommand_createsMedicalRecordAndUpdatesAppointmentAndGeneratesInvoice() {
        // Arrange
        UUID patientId = UUID.randomUUID();
        UUID dentistId = UUID.randomUUID();
        UUID appointmentId = UUID.randomUUID();
        UUID procedureId = UUID.randomUUID();

        MedicalRecordProcedureItemCommand itemCmd = new MedicalRecordProcedureItemCommand(procedureId, new BigDecimal("50.00"), 11, "Notes");
        CreateMedicalRecordCommand command = new CreateMedicalRecordCommand(patientId, dentistId, appointmentId, "Diagnosis", "Notes", List.of(itemCmd));

        Patient patient = Patient.create("John", "Doe", "DNI", "123", "555", "john@example.com", null, "Addr");
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));

        User user = User.create("dentist@example.com", "hash", "Jane", "Smith", "555", "DOC", Set.of(Role.DENTIST));
        when(userRepository.findById(dentistId)).thenReturn(Optional.of(user));

        Procedure procedure = Procedure.create("Cleaning", "Teeth", new BigDecimal("50.00"), 30);
        when(procedureRepository.findById(procedureId)).thenReturn(Optional.of(procedure));

        Appointment appointment = Appointment.create(patientId, dentistId, UUID.randomUUID(), java.time.LocalDate.now(), java.time.LocalTime.of(10, 0), java.time.LocalTime.of(11, 0), "Reason");
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        MedicalRecordResponse response = createMedicalRecordService.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals("Diagnosis", response.diagnosis());
        assertEquals(1, response.items().size());

        verify(appointmentRepository).save(appointment);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatus());

        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(createInvoiceUseCase).execute(any(UUID.class));
    }

    @Test
    void execute_withInvalidPatient_throwsException() {
        // Arrange
        CreateMedicalRecordCommand command = new CreateMedicalRecordCommand(UUID.randomUUID(), UUID.randomUUID(), null, "Diagnosis", "Notes", List.of());
        when(patientRepository.findById(command.patientId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PatientNotFoundException.class, () -> createMedicalRecordService.execute(command));
        verifyNoInteractions(medicalRecordRepository, createInvoiceUseCase);
    }
}
