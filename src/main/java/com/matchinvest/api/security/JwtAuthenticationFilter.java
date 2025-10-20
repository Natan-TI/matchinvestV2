package com.matchinvest.api.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenService tokenService;
  private final UserDetailsService userDetailsService;

  public JwtAuthenticationFilter(JwtTokenService tokenService, UserDetailsService userDetailsService) {
    this.tokenService = tokenService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {

    String header = req.getHeader("Authorization");
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        var jws = tokenService.parse(token);
        String username = jws.getBody().getSubject();

        @SuppressWarnings("unchecked")
        var roles = (List<String>) jws.getBody().get("roles");
        var authorities = roles == null ? List.<SimpleGrantedAuthority>of()
            : roles.stream().map(SimpleGrantedAuthority::new).toList();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        var auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
      }
    }

    chain.doFilter(req, res);
  }
}
