package com.matchinvest.api.dto.auth;

import jakarta.validation.constraints.*;

public record RegisterRequest(
  @NotBlank String name,
  @Email @NotBlank String email,
  @Size(min=6) String password,
  @NotNull AccountType accountType,
  InvestorRegisterData investor,   // obrigatório se accountType = INVESTOR
  AdvisorRegisterData advisor      // obrigatório se accountType = ADVISOR
) {}
