package com.asan.order.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.asan.order.entity.Order;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenVerifier {

  private final SecretKey signingKey;

  public JwtTokenVerifier(@Value("${app.jwt.secret}") String secret) {
    byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    if (secretBytes.length < 32) {
      throw new IllegalArgumentException("app.jwt.secret must contain at least 32 UTF-8 bytes");
    }
    this.signingKey = Keys.hmacShaKeyFor(secretBytes);
  }

  public Claims verify(String token) {
    return Jwts.parser()
        .verifyWith(signingKey)
        .requireIssuer("abl-auth")
        .requireAudience("abl-services")
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
