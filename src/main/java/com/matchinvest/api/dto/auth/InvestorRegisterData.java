package com.matchinvest.api.dto.auth;

import java.math.BigDecimal;

public record InvestorRegisterData(
  String goals,
  BigDecimal avaliableAmount,
  String riskProfile
) {}
