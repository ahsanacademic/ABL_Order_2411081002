package com.asan.order.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class ServiceTokenProviderTest {

  @Test
  void createsShortLivedServiceRoleTokenForInternalCalls() {
    String secret = "only-a-test-key-not-a-production-secret-value";
    ServiceTokenProvider tokenProvider = new ServiceTokenProvider(secret);
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    var claims = Jwts.parser()
        .verifyWith(key)
        .requireIssuer("abl-auth")
        .requireAudience("abl-services")
        .build()
        .parseSignedClaims(tokenProvider.createToken())
        .getPayload();

    assertEquals("order-service", claims.getSubject());
    assertEquals("service", claims.get("role", String.class));
  }
}
