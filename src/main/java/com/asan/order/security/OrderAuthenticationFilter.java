package com.asan.order.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OrderAuthenticationFilter extends OncePerRequestFilter {

  public static final String AUTH_USER_ID_ATTRIBUTE = "authUserId";

  private final JwtTokenVerifier tokenVerifier;

  public OrderAuthenticationFilter(JwtTokenVerifier tokenVerifier) {
    this.tokenVerifier = tokenVerifier;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return !"/api/order".equals(path) && !path.startsWith("/api/order/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak ada atau formatnya salah");
      return;
    }

    Claims claims;
    try {
      claims = tokenVerifier.verify(authorization.substring(7).trim());
    } catch (JwtException | IllegalArgumentException exception) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak valid atau kedaluwarsa");
      return;
    }

    if (!"pelanggan".equals(claims.get("role", String.class))) {
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "Akses order hanya untuk pelanggan");
      return;
    }

    Object rawUserId = claims.get("userId");
    if (!(rawUserId instanceof Number userId) || userId.longValue() <= 0) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token tidak memiliki identitas pelanggan yang valid");
      return;
    }

    request.setAttribute(AUTH_USER_ID_ATTRIBUTE, userId.longValue());
    filterChain.doFilter(request, response);
  }

  private void writeError(HttpServletResponse response, int status, String message) throws IOException {
    response.setStatus(status);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"error\":\"" + message + "\"}");
  }
}
