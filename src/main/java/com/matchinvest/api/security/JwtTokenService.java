package com.matchinvest.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenService {
  private final Key key;
  private final long expiration;
  private final String issuer;

  public JwtTokenService(
    @Value("${security.jwt.secret}") String secret,
    @Value("${security.jwt.expiration}") long expiration,
    @Value("${security.jwt.issuer}") String issuer
  ) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.expiration = expiration;
    this.issuer = issuer;
  }

  public String generate(String subject, Map<String, Object> claims) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
      .setSubject(subject)
      .setIssuer(issuer)
      .setIssuedAt(new Date(now))
      .setExpiration(new Date(now + expiration))
      .addClaims(claims)
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
