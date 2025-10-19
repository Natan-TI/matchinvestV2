package com.matchinvest.api.dto;

import java.math.BigDecimal;

import com.matchinvest.api.enums.RiskProfile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InvestorCreateDTO (
	
	@NotBlank(message = "Nome é obrigatório.")
	String name,
	
	@Email(message = "E-mail inválido.")
	@NotBlank(message = "E-mail é obrigatório.")
	String email,
	
	@NotNull(message = "Perfil de risco é obrigatório.")
	RiskProfile riskProfile,
	
	@NotNull(message = "Valor disponível é obrigatório.")
	@PositiveOrZero(message = "Valor deve ser maior ou igual a zero.")
	BigDecimal avaliableAmount,
	
	String goals
) {}
