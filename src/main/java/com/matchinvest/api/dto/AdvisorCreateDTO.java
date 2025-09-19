package com.matchinvest.api.dto;

import com.matchinvest.api.enums.InvestmentFocus;
import jakarta.validation.constraints.*;

public record AdvisorCreateDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        String certifications,

        @NotNull(message = "Foco de investimento é obrigatório")
        InvestmentFocus investmentFocus,

        @PositiveOrZero(message = "Anos de experiência deve ser maior ou igual a 0")
        Integer yearsExperience
) {}
