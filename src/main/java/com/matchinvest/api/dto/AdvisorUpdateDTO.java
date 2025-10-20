package com.matchinvest.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record AdvisorUpdateDTO(
	    @NotBlank(message = "Certificação é obrigatória.") String certifications,
	    @NotBlank(message = "Foco de investimento é obrigatório.") String investmentFocus,
	    @PositiveOrZero(message = "Anos de experiência inválido.") int yearsExperience
	) {}
