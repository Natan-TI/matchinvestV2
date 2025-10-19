package com.matchinvest.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InvestorUpdateDTO(

	    @NotBlank(message = "Metas são obrigatórias.")
	    String goals,

	    @NotNull(message = "Valor disponível é obrigatório.")
	    @PositiveOrZero(message = "Valor deve ser maior ou igual a zero.")
	    BigDecimal avaliableAmount,

	    @NotBlank(message = "Perfil de risco é obrigatório.")
	    String riskProfile

	) {}