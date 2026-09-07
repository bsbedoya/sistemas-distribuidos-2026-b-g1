package com.odontosys.odontosys_api.application.auth;

import java.util.Set;
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

import com.odontosys.odontosys_api.application.auth.command.CreateUserCommand;
import com.odontosys.odontosys_api.application.auth.response.UserResponse;
import com.odontosys.odontosys_api.domain.exception.UserAlreadyExistsException;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.PasswordEncoderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private CreateUserService createUserService;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserService(userRepository, passwordEncoder);
    }

    @Test
    void execute_withValidData_createsUser() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "new@example.com",
                "password123",
                "Jane",
                "Doe",
                "555-4321",
                "DOC-123",
                Set.of(Role.DENTIST)
        );

        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(userRepository.existsByDocumentNumber(command.documentNumber())).thenReturn(false);
        when(passwordEncoder.encode(command.password())).thenReturn("hashed_password");

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        UserResponse response = createUserService.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals(command.email(), response.email());
        assertEquals(command.firstName(), response.firstName());

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("hashed_password", savedUser.getPasswordHash());
        assertTrue(savedUser.getRoles().contains(Role.DENTIST));
    }

    @Test
    void execute_withExistingEmail_throwsException() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "existing@example.com", "pass", "John", "Doe", null, null, Set.of(Role.SECRETARY_ASSISTANT)
        );
        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> createUserService.execute(command));
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void execute_withExistingDocumentNumber_throwsException() {
        // Arrange
        CreateUserCommand command = new CreateUserCommand(
                "new@example.com", "pass", "John", "Doe", null, "DOC-EXISTING", Set.of(Role.SECRETARY_ASSISTANT)
        );
        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(userRepository.existsByDocumentNumber(command.documentNumber())).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> createUserService.execute(command));
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }
}
