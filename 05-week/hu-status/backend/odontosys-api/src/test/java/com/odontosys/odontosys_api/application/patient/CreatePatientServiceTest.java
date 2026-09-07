package com.odontosys.odontosys_api.application.patient;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.patient.command.CreatePatientCommand;
import com.odontosys.odontosys_api.application.patient.response.PatientResponse;
import com.odontosys.odontosys_api.domain.exception.PatientAlreadyExistsException;
import com.odontosys.odontosys_api.domain.model.Patient;
import com.odontosys.odontosys_api.domain.port.out.PatientRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreatePatientServiceTest {

    @Mock
    private PatientRepositoryPort patientRepository;

    private CreatePatientService createPatientService;

    @BeforeEach
    void setUp() {
        createPatientService = new CreatePatientService(patientRepository);
    }

    @Test
    void execute_withValidCommand_createsAndReturnsPatient() {
        // Arrange
        CreatePatientCommand command = new CreatePatientCommand(
                "John", "Doe", "DNI", "12345678", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address"
        );

        when(patientRepository.existsByDocumentNumber(command.documentNumber())).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        PatientResponse response = createPatientService.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(command.firstName(), response.firstName());
        assertEquals(command.documentNumber(), response.documentNumber());

        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void execute_withExistingDocumentNumber_throwsException() {
        // Arrange
        CreatePatientCommand command = new CreatePatientCommand(
                "John", "Doe", "DNI", "12345678", "555-1234", "john@example.com", LocalDate.of(1990, 1, 1), "Address"
        );

        when(patientRepository.existsByDocumentNumber(command.documentNumber())).thenReturn(true);

        // Act & Assert
        assertThrows(PatientAlreadyExistsException.class, () -> createPatientService.execute(command));
        verify(patientRepository, never()).save(any(Patient.class));
    }
}
