package com.matchinvest.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.InvestmentFocus;

public record AdvisorResponseDTO(
        UUID id,
        String name,
        String email,
        String certifications,
        InvestmentFocus investmentFocus,
        Integer yearsExperience,
        LocalDateTime createdAt
) {}
