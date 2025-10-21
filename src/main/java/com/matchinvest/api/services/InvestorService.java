package com.matchinvest.api.services;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.InvestorCreateDTO;
import com.matchinvest.api.dto.InvestorResponseDTO;
import com.matchinvest.api.dto.InvestorUpdateDTO;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.repositories.UserRepository;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InvestorService {
	
	private final InvestorRepository repository;
	private final UserRepository userRepo;
	
	@Transactional
	public InvestorResponseDTO create(InvestorCreateDTO dto) { 
		Investor investor = new Investor();
		investor.setName(dto.name());
		investor.setEmail(new Email(dto.email()));
		investor.setRiskProfile(dto.riskProfile());
		investor.setAvaliableAmount(new Money(dto.avaliableAmount()));
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
	public InvestorResponseDTO update(UUID id, InvestorUpdateDTO dto) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
		Investor investor = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Investor not found"));
		
		if (!investor.getUser().getId().equals(user.getId()) || !user.hasRole("ADMIN")) {
            throw new AccessDeniedException("Você só pode atualizar seu próprio perfil");
        }
		
		investor.setRiskProfile(RiskProfile.valueOf(dto.riskProfile().toUpperCase()));
		investor.setAvaliableAmount(new Money(dto.avaliableAmount()));
		investor.setGoals(dto.goals());
		
		investor.setName(investor.getUser().getName());
		investor.setEmail(new Email(investor.getUser().getEmail()));
		
		return toResponse(repository.save(investor));
	}
	
	@Transactional
	public void deleteOwnAccount() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName();

	    User user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

	    userRepo.delete(user);
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
