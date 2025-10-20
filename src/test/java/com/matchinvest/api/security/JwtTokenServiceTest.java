package com.matchinvest.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new JwtTokenService(
                "12345678901234567890123456789012",
                3600000L,
                "matchinvest-test"
        );
    }

    @Test
    void shouldGenerateAndParseValidToken() {
        String token = tokenService.generate(
                "user@matchinvest.com",
                Map.of("roles", "ROLE_INVESTOR", "accountType", "INVESTOR")
        );

        assertThat(token).isNotBlank();

        Jws<Claims> parsed = tokenService.parse(token);

        assertThat(parsed.getBody().getSubject()).isEqualTo("user@matchinvest.com");
        assertThat(parsed.getBody().getIssuer()).isEqualTo("matchinvest-test");
        assertThat(parsed.getBody().get("roles")).isEqualTo("ROLE_INVESTOR");
        assertThat(parsed.getBody().get("accountType")).isEqualTo("INVESTOR");
    }

    @Test
    void shouldThrowForInvalidToken() {
        assertThatThrownBy(() -> tokenService.parse("invalid.token.value"))
                .isInstanceOf(Exception.class);
    }
}
