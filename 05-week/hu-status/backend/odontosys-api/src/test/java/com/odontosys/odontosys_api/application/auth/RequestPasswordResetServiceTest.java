package com.odontosys.odontosys_api.application.auth;

import java.util.Optional;
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

import com.odontosys.odontosys_api.application.auth.command.RequestPasswordResetCommand;
import com.odontosys.odontosys_api.domain.model.PasswordResetToken;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.EmailSenderPort;
import com.odontosys.odontosys_api.domain.port.out.PasswordResetTokenRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordResetTokenRepositoryPort resetTokenRepository;
    @Mock
    private EmailSenderPort emailSender;

    @Captor
    private ArgumentCaptor<PasswordResetToken> tokenCaptor;

    private RequestPasswordResetService requestPasswordResetService;
    private final int codeExpirationMinutes = 15;

    @BeforeEach
    void setUp() {
        requestPasswordResetService = new RequestPasswordResetService(
                userRepository,
                resetTokenRepository,
                emailSender,
                codeExpirationMinutes
        );
    }

    @Test
    void execute_withExistingUser_invalidatesOldTokensCreatesNewAndSendsEmail() {
        // Arrange
        String email = "test@example.com";
        RequestPasswordResetCommand command = new RequestPasswordResetCommand(email);

        User user = User.create(email, "hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        requestPasswordResetService.execute(command);

        // Assert
        verify(resetTokenRepository).invalidateAllByEmail(email);

        verify(resetTokenRepository).save(tokenCaptor.capture());
        PasswordResetToken savedToken = tokenCaptor.getValue();
        assertEquals(email, savedToken.getEmail());
        assertNotNull(savedToken.getCode());

        verify(emailSender).sendPasswordResetCode(eq(email), eq(savedToken.getCode()), eq(codeExpirationMinutes));
    }

    @Test
    void execute_withNonexistentUser_doesNothing() {
        // Arrange
        RequestPasswordResetCommand command = new RequestPasswordResetCommand("notfound@example.com");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // Act
        requestPasswordResetService.execute(command);

        // Assert
        verifyNoInteractions(resetTokenRepository, emailSender);
    }

    @Test
    void execute_withDisabledUser_doesNothing() {
        // Arrange
        String email = "disabled@example.com";
        RequestPasswordResetCommand command = new RequestPasswordResetCommand(email);

        User user = User.create(email, "hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        user.deactivate();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        requestPasswordResetService.execute(command);

        // Assert
        verifyNoInteractions(resetTokenRepository, emailSender);
    }
}
