package com.matchinvest.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.RiskProfile;

public record InvestorResponseDTO(
        UUID id,
        String name,
        String email,
        RiskProfile riskProfile,
        BigDecimal availableAmount,
        String goals,
        LocalDateTime createdAt
) {}
