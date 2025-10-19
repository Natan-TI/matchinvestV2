package com.matchinvest.api.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.MatchCreateDTO;
import com.matchinvest.api.dto.MatchResponseDTO;
import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.entities.Match;
import com.matchinvest.api.enums.MatchStatus;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.repositories.MatchRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

	private final MatchRepository matchRepository;
	private final InvestorRepository investorRepository;
	private final AdvisorRepository advisorRepository;
	
	@Transactional
	public MatchResponseDTO create(MatchCreateDTO dto) {
		Investor investor = investorRepository.findById(dto.investorId())
				.orElseThrow(() -> new EntityNotFoundException("Investor not found"));
		
		Advisor advisor = advisorRepository.findById(dto.advisorId())
				.orElseThrow(() -> new EntityNotFoundException("Advisor not found"));
		
		double scoreValue = calculateScore(investor, advisor);
		
		Match match = new Match();
		match.setInvestor(investor);
		match.setAdvisor(advisor);
		match.setScore(BigDecimal.valueOf(scoreValue));
		match.setStatus(MatchStatus.PENDENTE);
		
		matchRepository.save(match);
		return toResponse(match);
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponseDTO> findAll() {
		return matchRepository.findAll()
				.stream()
				.map(this::toResponse)
				.toList();
	}
	
	@Transactional(readOnly = true)
	public MatchResponseDTO findById(UUID id) {
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Match not found."));
		return toResponse(match);
	}
	
	@Transactional
	public void deleteById(UUID id) {
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Match not found."));
		matchRepository.delete(match);
	}
	
	@Transactional
	public MatchResponseDTO accept(UUID id) {
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Match not found"));
		match.setStatus(MatchStatus.ACEITO);
		return toResponse(match);
	}
	
	
	@Transactional
	public MatchResponseDTO reject(UUID id) {
		Match match = matchRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Match not found"));
		match.setStatus(MatchStatus.REJEITADO);
		return toResponse(match);
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponseDTO> findByInvestor(UUID investorId) {
		Investor investor = investorRepository.findById(investorId)
				.orElseThrow(() -> new EntityNotFoundException("Investor not found"));
		
		return matchRepository.findByInvestor(investor)
				.stream()
				.map(this::toResponse)
				.toList();
	}
	
	@Transactional(readOnly = true)
	public List<MatchResponseDTO> findByAdvisor(UUID advisorId) {
		Advisor advisor = advisorRepository.findById(advisorId)
				.orElseThrow(() -> new EntityNotFoundException("Advisor not found"));
		
		return matchRepository.findByAdvisor(advisor)
				.stream()
				.map(this::toResponse)
				.toList();
	}
	
	private Double calculateScore(Investor investor, Advisor advisor) {
		if (investor.getRiskProfile().name().equalsIgnoreCase(advisor.getInvestmentFocus().name())) {
			return 100.0;
		}
		return 70.0;
	}
	
	private MatchResponseDTO toResponse(Match match) {
		return new MatchResponseDTO(
				match.getId(),
				match.getInvestor().getId(),
				match.getAdvisor().getId(),
				match.getScore().doubleValue(),
				match.getStatus(),
				match.getCreatedAt()
				);
	}
}
