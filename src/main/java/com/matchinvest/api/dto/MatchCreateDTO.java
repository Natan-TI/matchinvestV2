package com.matchinvest.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MatchCreateDTO(
        @NotNull(message = "InvestorId é obrigatório")
        UUID investorId,

        @NotNull(message = "AdvisorId é obrigatório")
        UUID advisorId
) {}
