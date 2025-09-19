package com.matchinvest.api.dto;

import com.matchinvest.api.enums.MatchStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record MatchResponseDTO(
        UUID id,
        UUID investorId,
        UUID advisorId,
        Double score,
        MatchStatus status,
        LocalDateTime createdAt
) {}
