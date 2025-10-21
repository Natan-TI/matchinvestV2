// AuthController.java
package com.matchinvest.api.controllers;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matchinvest.api.dto.auth.AuthRequest;
import com.matchinvest.api.dto.auth.AuthResponse;
import com.matchinvest.api.dto.auth.RegisterRequest;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.security.JwtTokenService;
import com.matchinvest.api.services.RegistrationService;
import com.matchinvest.api.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação")
public class AuthController {

  private final UserService userService;
  private final JwtTokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final RegistrationService registrationService;
  
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  public AuthController(UserService userService, JwtTokenService tokenService, PasswordEncoder passwordEncoder, RegistrationService registrationService) {
    this.userService = userService; this.tokenService = tokenService; this.passwordEncoder = passwordEncoder; this.registrationService = registrationService;
  }

  @Operation(summary = "Registrar novo usuário (ROLE_USER)")
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
    var resp = registrationService.register(req);
    return ResponseEntity.ok(resp);
  }


  @Operation(summary = "Realiza login e retorna o token JWT")
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
      User user = userService.findByEmailOrThrow(req.email());

      if (!passwordEncoder.matches(req.password(), user.getPassword())) {
          throw new BadCredentialsException("Credenciais inválidas");
      }
      
      var roles = user.getRoles()
              .stream()
              .map(r -> r.getName())
              .collect(Collectors.toList());

      String token = tokenService.generate(user.getEmail(), Map.of("roles", roles));
      
      logger.info("Usuário '{}' realizou login em {}", req.email(), java.time.LocalDateTime.now());

      return ResponseEntity.ok(new AuthResponse(token, "Bearer", 3600000));
  }
}
