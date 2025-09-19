package com.matchinvest.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.AdvisorCreateDTO;
import com.matchinvest.api.dto.AdvisorResponseDTO;
import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvisorService {
	
	private final AdvisorRepository repository;
	
	@Transactional
	public AdvisorResponseDTO create(AdvisorCreateDTO dto) { 
		Advisor Advisor = new Advisor();
		Advisor.setName(dto.name());
		Advisor.setEmail(new Email(dto.email()));
		Advisor.setCertifications(dto.certifications());
		Advisor.setInvestmentFocus(dto.investmentFocus());
		Advisor.setYearsExperience(dto.yearsExperience());
		
		repository.save(Advisor);
		return toResponse(Advisor);
	}
	
	@Transactional(readOnly = true)
	public List<AdvisorResponseDTO> findAll() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}
	
	@Transactional(readOnly = true)
	public AdvisorResponseDTO findById(UUID id) {
		Advisor Advisor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Advisor not found"));
		return toResponse(Advisor);
	}
	
	@Transactional
	public AdvisorResponseDTO update(UUID id, AdvisorCreateDTO dto) {
		Advisor Advisor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Advisor not found"));
		
		Advisor.setName(dto.name());
		Advisor.setEmail(new Email(dto.email()));
		Advisor.setCertifications(dto.certifications());
		Advisor.setInvestmentFocus(dto.investmentFocus());
		Advisor.setYearsExperience(dto.yearsExperience());
		
		return toResponse(repository.save(Advisor));
	}
	
	@Transactional
	public void delete(UUID id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Advisor not found");
		}
		repository.deleteById(id);
	}
	
	private AdvisorResponseDTO toResponse(Advisor Advisor) {
		return new AdvisorResponseDTO(
				Advisor.getId(),
				Advisor.getName(),
				Advisor.getEmail().getValue(),
				Advisor.getCertifications(),
				Advisor.getInvestmentFocus(),
				Advisor.getYearsExperience(),
				Advisor.getCreatedAt()
				);
	}
}
