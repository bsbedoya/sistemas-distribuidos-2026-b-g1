package com.odontosys.odontosys_api.application.auth;

import java.time.LocalDate;
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

import com.odontosys.odontosys_api.application.auth.command.LoginCommand;
import com.odontosys.odontosys_api.application.auth.response.AuthResponse;
import com.odontosys.odontosys_api.domain.exception.InvalidCredentialsException;
import com.odontosys.odontosys_api.domain.exception.UserDisabledException;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.out.PasswordEncoderPort;
import com.odontosys.odontosys_api.domain.port.out.RefreshTokenRepositoryPort;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;
    @Mock
    private PasswordEncoderPort passwordEncoder;
    @Mock
    private TokenProviderPort tokenProvider;

    private LoginService loginService;
    private final long refreshTokenExpirationMs = 1000L;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(
                userRepository,
                refreshTokenRepository,
                passwordEncoder,
                tokenProvider,
                refreshTokenExpirationMs
        );
    }

    @Test
    void execute_withValidCredentials_returnsAuthResponseAndSavesToken() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        LoginCommand command = new LoginCommand(email, password);

        User user = User.create(email, "hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashed_password")).thenReturn(true);
        when(tokenProvider.generateAccessToken(user)).thenReturn("access_token");
        when(tokenProvider.generateRefreshToken()).thenReturn("raw_refresh_token");
        when(tokenProvider.hashRefreshToken("raw_refresh_token")).thenReturn("hashed_refresh_token");

        // Act
        AuthResponse response = loginService.execute(command);

        // Assert
        assertNotNull(response);
        assertEquals("access_token", response.accessToken());
        assertEquals("raw_refresh_token", response.refreshToken());
        assertEquals(email, response.user().email());

        verify(refreshTokenRepository).save(any(UUID.class), eq(user.getId()), eq("hashed_refresh_token"), eq(refreshTokenExpirationMs));
    }

    @Test
    void execute_withInvalidEmail_throwsInvalidCredentialsException() {
        // Arrange
        LoginCommand command = new LoginCommand("wrong@example.com", "password");
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(command));
        verifyNoInteractions(passwordEncoder, tokenProvider, refreshTokenRepository);
    }

    @Test
    void execute_withInvalidPassword_throwsInvalidCredentialsException() {
        // Arrange
        String email = "test@example.com";
        String password = "wrong_password";
        LoginCommand command = new LoginCommand(email, password);

        User user = User.create(email, "hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashed_password")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> loginService.execute(command));
        verifyNoInteractions(tokenProvider, refreshTokenRepository);
    }

    @Test
    void execute_withDisabledUser_throwsUserDisabledException() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        LoginCommand command = new LoginCommand(email, password);

        User user = User.create(email, "hashed_password", "John", "Doe", "555-1234", "12345678", Set.of(Role.ADMIN));
        user.deactivate();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashed_password")).thenReturn(true);

        // Act & Assert
        assertThrows(UserDisabledException.class, () -> loginService.execute(command));
        verifyNoInteractions(tokenProvider, refreshTokenRepository);
    }
}
