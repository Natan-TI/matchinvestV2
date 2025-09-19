package com.matchinvest.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.InvestorCreateDTO;
import com.matchinvest.api.dto.InvestorResponseDTO;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestorService {
	
	private final InvestorRepository repository;
	
	@Transactional
	public InvestorResponseDTO create(InvestorCreateDTO dto) { 
		Investor investor = new Investor();
		investor.setName(dto.name());
		investor.setEmail(new Email(dto.email()));
		investor.setRiskProfile(dto.riskProfile());
		investor.setAvaliableAmount(new Money(dto.valueAmount()));
		investor.setGoals(dto.goals());
		
		repository.save(investor);
		return toResponse(investor);
	}
	
	@Transactional(readOnly = true)
	public List<InvestorResponseDTO> findAll() {
		return repository.findAll().stream().map(this::toResponse).toList();
	}
	
	@Transactional(readOnly = true)
	public InvestorResponseDTO findById(UUID id) {
		Investor investor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Investor not found"));
		return toResponse(investor);
	}
	
	@Transactional
	public InvestorResponseDTO update(UUID id, InvestorCreateDTO dto) {
		Investor investor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Investor not found"));
		
		investor.setName(dto.name());
		investor.setEmail(new Email(dto.email()));
		investor.setRiskProfile(dto.riskProfile());
		investor.setAvaliableAmount(new Money(dto.valueAmount()));
		investor.setGoals(dto.goals());
		
		return toResponse(repository.save(investor));
	}
	
	@Transactional
	public void delete(UUID id) {
		if (!repository.existsById(id)) {
			throw new EntityNotFoundException("Investor not found");
		}
		repository.deleteById(id);
	}
	
	private InvestorResponseDTO toResponse(Investor investor) {
		return new InvestorResponseDTO(
				investor.getId(),
				investor.getName(),
				investor.getEmail().getValue(),
				investor.getRiskProfile(),
				investor.getAvaliableAmount().getAmount(),
				investor.getGoals(),
				investor.getCreatedAt()
				);
	}
}
