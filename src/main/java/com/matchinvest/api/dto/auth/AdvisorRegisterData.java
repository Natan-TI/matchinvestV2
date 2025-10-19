package com.matchinvest.api.dto.auth;

public record AdvisorRegisterData(
  String certifications,
  String investmentFocus,
  Integer yearsExperience
) {}
