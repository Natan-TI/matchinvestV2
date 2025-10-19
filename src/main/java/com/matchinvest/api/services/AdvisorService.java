package com.matchinvest.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.AdvisorCreateDTO;
import com.matchinvest.api.dto.AdvisorResponseDTO;
import com.matchinvest.api.dto.AdvisorUpdateDTO;
import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.enums.InvestmentFocus;
import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.repositories.UserRepository;
import com.matchinvest.api.vo.Email;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdvisorService {
	
	private final AdvisorRepository repository;
	private final UserRepository userRepo;
	
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
	public AdvisorResponseDTO update(UUID id, AdvisorUpdateDTO dto) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
		Advisor advisor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Advisor not found"));
		
		if (!advisor.getUser().getId().equals(user.getId()) && !user.hasRole("ADMIN")) {
            throw new AccessDeniedException("Você só pode atualizar seu próprio perfil");
        }

		advisor.setCertifications(dto.certifications());
		advisor.setInvestmentFocus(InvestmentFocus.valueOf(dto.investmentFocus().toUpperCase()));
		advisor.setYearsExperience(dto.yearsExperience());
		
		advisor.setName(advisor.getUser().getName());
		advisor.setEmail(new Email(advisor.getUser().getEmail()));
		
		return toResponse(repository.save(advisor));
	}
	
	@Transactional
	public void deleteOwnAccount() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    User user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	    userRepo.delete(user);
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
