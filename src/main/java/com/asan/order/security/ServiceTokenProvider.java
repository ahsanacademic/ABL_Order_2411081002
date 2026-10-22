package com.asan.order.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class ServiceTokenProvider {

  private final SecretKey signingKey;

  public ServiceTokenProvider(@Value("${app.jwt.secret}") String secret) {
    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < 32) {
      throw new IllegalArgumentException("app.jwt.secret must contain at least 32 UTF-8 bytes");
    }
    this.signingKey = Keys.hmacShaKeyFor(secretBytes);
  }

  public String createToken() {
    Instant issuedAt = Instant.now();
    return Jwts.builder()
        .issuer("abl-auth")
        .audience().add("abl-services").and()
        .subject("order-service")
        .claim("role", "service")
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(issuedAt.plusSeconds(120)))
        .signWith(signingKey)
        .compact();
  }
}
