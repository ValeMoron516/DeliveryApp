package com.desarrolloweb.DeliveryApp.config;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.JwsHeader;

import static org.junit.jupiter.api.Assertions.*;

/** Pure JWT security regression tests: no Spring context, no network, no database. */
class DeliveryJwtSecurityTest {
    private DeliverySecurityConfig config;
    private SecretKey key;
    private JwtEncoder encoder;
    private JwtDecoder decoder;

    @BeforeEach
    void setUp() {
        config = new DeliverySecurityConfig();
        byte[] bytes = new byte[48];
        for (int i = 0; i < bytes.length; i++) bytes[i] = (byte) (i + 17);
        key = config.deliveryJwtKey(Base64.getEncoder().encodeToString(bytes));
        encoder = config.jwtEncoder(key);
        decoder = config.jwtDecoder(key);
    }

    private String token(String subject, List<String> roles, Instant issuedAt, Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(subject).issuedAt(issuedAt)
                .expiresAt(expiresAt).claim("roles", roles).build();
        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test void rejectsMissingSecret() {
        assertThrows(IllegalStateException.class, () -> config.deliveryJwtKey("  "));
    }

    @Test void rejectsInvalidBase64() {
        assertThrows(IllegalStateException.class, () -> config.deliveryJwtKey("not-base64!"));
    }

    @Test void rejectsShortKey() {
        assertThrows(IllegalStateException.class,
                () -> config.deliveryJwtKey(Base64.getEncoder().encodeToString(new byte[16])));
    }

    @Test void acceptsStrongKey() {
        assertNotNull(key);
        assertEquals("HmacSHA256", key.getAlgorithm());
    }

    @Test void signsAndVerifiesSubject() {
        String raw = token("123", List.of("CLIENTE"), Instant.now().minusSeconds(5), Instant.now().plusSeconds(600));
        assertEquals("123", decoder.decode(raw).getSubject());
    }

    @Test void extractsClienteAuthority() {
        Jwt jwt = decoder.decode(token("8", List.of("CLIENTE"), Instant.now().minusSeconds(5), Instant.now().plusSeconds(600)));
        var auth = config.jwtAuthenticationConverter().convert(jwt);
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        assertFalse(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test void extractsMultipleRolesInOneSession() {
        Jwt jwt = decoder.decode(token("9", List.of("CLIENTE", "REPARTIDOR", "ADMIN"),
                Instant.now().minusSeconds(5), Instant.now().plusSeconds(600)));
        var auth = config.jwtAuthenticationConverter().convert(jwt);
        assertNotNull(auth);
        assertEquals("9", auth.getName());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_REPARTIDOR")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test void rejectsTamperedSignature() {
        String raw = token("123", List.of("CLIENTE"), Instant.now().minusSeconds(5), Instant.now().plusSeconds(600));
        String[] parts = raw.split("\\.");
        assertEquals(3, parts.length);
        String signature = parts[2];
        char flip = signature.charAt(0) == 'A' ? 'B' : 'A';
        String tampered = parts[0] + "." + parts[1] + "." + flip + signature.substring(1);
        assertThrows(JwtException.class, () -> decoder.decode(tampered));
    }

    @Test void rejectsExpiredToken() {
        String raw = token("123", List.of("CLIENTE"), Instant.now().minusSeconds(600), Instant.now().minusSeconds(300));
        assertThrows(JwtValidationException.class, () -> decoder.decode(raw));
    }

    @Test void rejectsTokenSignedByDifferentSecret() {
        byte[] other = new byte[48];
        for (int i = 0; i < other.length; i++) other[i] = (byte) (i + 50);
        JwtDecoder foreignDecoder = config.jwtDecoder(config.deliveryJwtKey(Base64.getEncoder().encodeToString(other)));
        String raw = token("123", List.of("CLIENTE"), Instant.now().minusSeconds(5), Instant.now().plusSeconds(600));
        assertThrows(JwtException.class, () -> foreignDecoder.decode(raw));
    }
}
