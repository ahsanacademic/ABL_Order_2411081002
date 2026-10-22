package com.asan.order.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class OrderAuthenticationFilterTest {

  private static final String SECRET = "only-a-test-key-not-a-production-secret-value";
  private final OrderAuthenticationFilter filter = new OrderAuthenticationFilter(new JwtTokenVerifier(SECRET));

  @Test
  void rejectsRequestWithoutToken() throws Exception {
    MockFilterChain chain = new MockFilterChain();
    MockHttpServletResponse response = invoke(null, chain);

    assertEquals(401, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void authenticatesCustomerAndAddsAuthUserIdToRequest() throws Exception {
    MockFilterChain chain = new MockFilterChain();
    MockHttpServletResponse response = invoke(token("pelanggan", 17L), chain);

    assertEquals(200, response.getStatus());
    assertEquals(17L, chain.getRequest().getAttribute(OrderAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE));
  }

  @Test
  void deniesAdminRoleOnCustomerOrderRoutes() throws Exception {
    MockFilterChain chain = new MockFilterChain();
    MockHttpServletResponse response = invoke(token("admin", 17L), chain);

    assertEquals(403, response.getStatus());
    assertNull(chain.getRequest());
  }

  @Test
  void rejectsCustomerTokenWithoutUserIdClaim() throws Exception {
    MockFilterChain chain = new MockFilterChain();
    MockHttpServletResponse response = invoke(token("pelanggan", null), chain);

    assertEquals(401, response.getStatus());
    assertNull(chain.getRequest());
  }

  private MockHttpServletResponse invoke(String token, MockFilterChain chain) throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/order/query");
    request.setServletPath("/api/order/query");
    if (token != null) {
      request.addHeader("Authorization", "Bearer " + token);
    }
    MockHttpServletResponse response = new MockHttpServletResponse();
    filter.doFilter(request, response, chain);
    return response;
  }

  private String token(String role, Long userId) {
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    Instant issuedAt = Instant.now();
    var builder = Jwts.builder()
        .issuer("abl-auth")
        .audience().add("abl-services").and()
        .subject("test-user")
        .claim("role", role)
        .issuedAt(Date.from(issuedAt))
        .expiration(Date.from(issuedAt.plusSeconds(60)));
    if (userId != null) {
      builder.claim("userId", userId);
    }
    return builder.signWith(key).compact();
  }
}
