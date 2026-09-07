package com.odontosys.odontosys_api.infrastructure.security;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.odontosys.odontosys_api.domain.model.Role;
import com.odontosys.odontosys_api.domain.model.User;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "12345678901234567890123456789012"; // 32 chars for HMAC-SHA256
    private final long expiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expiration);
    }

    @Test
    void generateAccessToken_createsValidToken() {
        User user = User.create("test@example.com", "hash", "John", "Doe", "555", "123", Set.of(Role.ADMIN));
        String token = jwtTokenProvider.generateAccessToken(user);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateAccessToken(token));
        assertEquals(user.getId(), jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void validateAccessToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateAccessToken("invalid.token.here"));
    }

    @Test
    void generateRefreshToken_returnsUuidString() {
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        assertNotNull(refreshToken);
        assertDoesNotThrow(() -> UUID.fromString(refreshToken));
    }

    @Test
    void hashRefreshToken_returnsSha256Hash() {
        String rawToken = "test_token";
        String hash = jwtTokenProvider.hashRefreshToken(rawToken);
        
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 hash length in hex
    }
}
