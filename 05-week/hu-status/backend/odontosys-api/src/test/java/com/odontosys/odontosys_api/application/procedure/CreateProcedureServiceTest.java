package com.odontosys.odontosys_api.application.procedure;

import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.procedure.command.CreateProcedureCommand;
import com.odontosys.odontosys_api.application.procedure.response.ProcedureResponse;
import com.odontosys.odontosys_api.domain.exception.ProcedureAlreadyExistsException;
import com.odontosys.odontosys_api.domain.model.Procedure;
import com.odontosys.odontosys_api.domain.port.out.ProcedureRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateProcedureServiceTest {

    @Mock
    private ProcedureRepositoryPort procedureRepository;

    private CreateProcedureService createProcedureService;

    @BeforeEach
    void setUp() {
        createProcedureService = new CreateProcedureService(procedureRepository);
    }

    @Test
    void execute_withValidCommand_createsAndReturnsProcedure() {
        // Arrange
        CreateProcedureCommand command = new CreateProcedureCommand("Cleaning", "Teeth cleaning", new BigDecimal("50.00"), 30);
        when(procedureRepository.existsByName(command.name())).thenReturn(false);
        when(procedureRepository.save(any(Procedure.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        ProcedureResponse response = createProcedureService.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(command.name(), response.name());
        assertEquals(command.price(), response.price());

        verify(procedureRepository).save(any(Procedure.class));
    }

    @Test
    void execute_withExistingName_throwsException() {
        // Arrange
        CreateProcedureCommand command = new CreateProcedureCommand("Cleaning", "Teeth cleaning", new BigDecimal("50.00"), 30);
        when(procedureRepository.existsByName(command.name())).thenReturn(true);

        // Act & Assert
        assertThrows(ProcedureAlreadyExistsException.class, () -> createProcedureService.execute(command));
        verify(procedureRepository, never()).save(any(Procedure.class));
    }
}
