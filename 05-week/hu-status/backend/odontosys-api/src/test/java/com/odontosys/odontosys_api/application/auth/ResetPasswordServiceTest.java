package com.odontosys.odontosys_api.application.auth;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.odontosys.odontosys_api.application.auth.command.ResetPasswordCommand;
import com.odontosys.odontosys_api.domain.exception.InvalidResetCodeException;
import com.odontosys.odontosys_api.domain.exception.UserNotFoundException;
import com.odontosys.odontosys_api.domain.model.PasswordResetToken;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.PasswordEncoderPort;
import com.odontosys.odontosys_api.domain.port.out.PasswordResetTokenRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.RefreshTokenRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private PasswordResetTokenRepositoryPort resetTokenRepository;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;

    private ResetPasswordService resetPasswordService;

    @BeforeEach
    void setUp() {
        resetPasswordService = new ResetPasswordService(
                userRepository,
                resetTokenRepository,
                refreshTokenRepository,
                passwordEncoder
        );
    }

    @Test
    void execute_withValidCode_updatesPasswordAndRevokesSessions() {
        // Arrange
        String email = "test@example.com";
        String newPassword = "new_password123";
        PasswordResetToken token = PasswordResetToken.create(email, 15);
        String code = token.getCode();
        ResetPasswordCommand command = new ResetPasswordCommand(email, code, newPassword);

        when(resetTokenRepository.findLatestByEmail(email)).thenReturn(Optional.of(token));

        User user = User.create(email, "old_hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        when(passwordEncoder.encode(newPassword)).thenReturn("new_hashed_password");

        // Act
        resetPasswordService.execute(command);

        // Assert
        assertEquals("new_hashed_password", user.getPasswordHash());
        verify(userRepository).save(user);

        assertTrue(token.isUsed());
        verify(resetTokenRepository).save(token);

        verify(refreshTokenRepository).revokeAllByUserId(user.getId());
    }

    @Test
    void execute_withInvalidCode_throwsInvalidResetCodeException() {
        // Arrange
        String email = "test@example.com";
        ResetPasswordCommand command = new ResetPasswordCommand(email, "000000", "new_password");

        PasswordResetToken token = PasswordResetToken.create(email, 15); // code will be different
        when(resetTokenRepository.findLatestByEmail(email)).thenReturn(Optional.of(token));

        // Act & Assert
        assertThrows(InvalidResetCodeException.class, () -> resetPasswordService.execute(command));
        verifyNoInteractions(userRepository, passwordEncoder, refreshTokenRepository);
    }

    @Test
    void execute_withNoTokenFound_throwsInvalidResetCodeException() {
        // Arrange
        ResetPasswordCommand command = new ResetPasswordCommand("test@example.com", "123456", "new_password");
        when(resetTokenRepository.findLatestByEmail(command.email())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidResetCodeException.class, () -> resetPasswordService.execute(command));
        verifyNoInteractions(userRepository, passwordEncoder, refreshTokenRepository);
    }
}
