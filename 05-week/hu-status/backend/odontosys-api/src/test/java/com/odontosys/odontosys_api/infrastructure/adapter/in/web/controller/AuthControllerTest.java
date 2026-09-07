package com.odontosys.odontosys_api.infrastructure.adapter.in.web.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odontosys.odontosys_api.application.auth.command.LoginCommand;
import com.odontosys.odontosys_api.application.auth.command.RefreshTokenCommand;
import com.odontosys.odontosys_api.application.auth.command.RequestPasswordResetCommand;
import com.odontosys.odontosys_api.application.auth.command.ResetPasswordCommand;
import com.odontosys.odontosys_api.application.auth.response.AuthResponse;
import com.odontosys.odontosys_api.application.auth.response.TokenResponse;
import com.odontosys.odontosys_api.application.auth.response.UserResponse;
import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;
import com.odontosys.odontosys_api.domain.port.in.auth.GetCurrentUserUseCase;
import com.odontosys.odontosys_api.domain.port.in.auth.LoginUseCase;
import com.odontosys.odontosys_api.domain.port.in.auth.LogoutUseCase;
import com.odontosys.odontosys_api.domain.port.in.auth.RefreshTokenUseCase;
import com.odontosys.odontosys_api.domain.port.in.auth.RequestPasswordResetUseCase;
import com.odontosys.odontosys_api.domain.port.in.auth.ResetPasswordUseCase;
import com.odontosys.odontosys_api.domain.port.out.UserRepositoryPort;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.ForgotPasswordRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.LoginRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.RefreshTokenRequestDto;
import com.odontosys.odontosys_api.infrastructure.adapter.in.web.dto.request.ResetPasswordRequestDto;
import com.odontosys.odontosys_api.infrastructure.security.CustomUserDetails;
import com.odontosys.odontosys_api.domain.port.out.TokenProviderPort;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginUseCase loginUseCase;
    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;
    @MockBean
    private LogoutUseCase logoutUseCase;
    @MockBean
    private GetCurrentUserUseCase getCurrentUserUseCase;
    @MockBean
    private RequestPasswordResetUseCase requestPasswordResetUseCase;
    @MockBean
    private ResetPasswordUseCase resetPasswordUseCase;

    @MockBean
    private TokenProviderPort jwtTokenProvider;
    @MockBean
    private UserRepositoryPort userRepository;

    @Test
    void login_returnsAuthResponse() throws Exception {
        LoginRequestDto request = new LoginRequestDto("test@example.com", "password123");
        UserResponse userResponse = new UserResponse(UUID.randomUUID(), "test@example.com", "John", "Doe", "555", "123", true, Set.of(Role.ADMIN), Instant.now());
        AuthResponse response = new AuthResponse(userResponse, "access_token", "refresh_token");

        when(loginUseCase.execute(any(LoginCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void refresh_returnsTokenResponse() throws Exception {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto("old_refresh_token");
        TokenResponse response = new TokenResponse("new_access_token", "new_refresh_token");

        when(refreshTokenUseCase.execute(any(RefreshTokenCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"));
    }

    @Test
    void logout_returns204() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.create("test@example.com", "hash", "John", "Doe", "555", "123", Set.of(Role.ADMIN));
        java.lang.reflect.Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent());

        SecurityContextHolder.clearContext();

        verify(logoutUseCase).execute(userId);
    }

    @Test
    void getCurrentUser_returnsUserResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.create("test@example.com", "hash", "John", "Doe", "555", "123", Set.of(Role.ADMIN));
        java.lang.reflect.Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, userId);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
        UserResponse response = new UserResponse(userId, "test@example.com", "John", "Doe", "555", "123", true, Set.of(Role.ADMIN), Instant.now());

        when(getCurrentUserUseCase.execute(userId)).thenReturn(response);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));

        SecurityContextHolder.clearContext();
    }

    @Test
    void forgotPassword_returns200() throws Exception {
        ForgotPasswordRequestDto request = new ForgotPasswordRequestDto("test@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(requestPasswordResetUseCase).execute(any(RequestPasswordResetCommand.class));
    }

    @Test
    void resetPassword_returns200() throws Exception {
        ResetPasswordRequestDto request = new ResetPasswordRequestDto("test@example.com", "123456", "new_password");

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(resetPasswordUseCase).execute(any(ResetPasswordCommand.class));
    }
}
