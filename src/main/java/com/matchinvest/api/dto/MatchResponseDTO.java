package com.matchinvest.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.matchinvest.api.enums.MatchStatus;

public record MatchResponseDTO(
        UUID id,
        UUID investorId,
        UUID advisorId,
        Double score,
        MatchStatus status,
        LocalDateTime createdAt
) {}
