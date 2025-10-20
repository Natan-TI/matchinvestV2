package com.matchinvest.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import com.matchinvest.api.config.TestDataInitializer;
import com.matchinvest.api.dto.MatchCreateDTO;
import com.matchinvest.api.dto.MatchResponseDTO;
import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.entities.Match;
import com.matchinvest.api.enums.InvestmentFocus;
import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.repositories.MatchRepository;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@Import({TestDataInitializer.class})
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private InvestorRepository investorRepository;

    @Mock
    private AdvisorRepository advisorRepository;

    @InjectMocks
    private MatchService matchService;

    private Investor investor;
    private Advisor advisor;

    @BeforeEach
    void setup() {
        investor = Investor.builder()
                .id(UUID.randomUUID())
                .name("João da Silva")
                .email(new Email("joao@email.com"))
                .riskProfile(RiskProfile.MODERADO)
                .avaliableAmount(new Money(new BigDecimal("10000.00")))
                .goals("Aposentadoria")
                .createdAt(LocalDateTime.now())
                .build();

        advisor = Advisor.builder()
                .id(UUID.randomUUID())
                .name("Maria Souza")
                .email(new Email("maria@email.com"))
                .certifications("CFA")
                .investmentFocus(InvestmentFocus.ACOES)
                .yearsExperience(5)
                .createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    void shouldCreateMatch() {
        when(investorRepository.findById(investor.getId())).thenReturn(Optional.of(investor));
        when(advisorRepository.findById(advisor.getId())).thenReturn(Optional.of(advisor));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArguments()[0]);

        MatchCreateDTO dto = new MatchCreateDTO(investor.getId(), advisor.getId());

        MatchResponseDTO result = matchService.create(dto);

        assertThat(result.investorId()).isEqualTo(investor.getId());
        assertThat(result.advisorId()).isEqualTo(advisor.getId());
        assertThat(result.status().toString()).isEqualTo("PENDENTE");
        verify(matchRepository, times(1)).save(any(Match.class));
    }
    
    @Test
    void shouldThrowWhenInvestorNotFound() {
        UUID investorId = UUID.randomUUID();
        UUID advisorId = UUID.randomUUID();

        when(investorRepository.findById(investorId)).thenReturn(Optional.empty());

        MatchCreateDTO dto = new MatchCreateDTO(investorId, advisorId);

        assertThatThrownBy(() -> matchService.create(dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Investor not found");
    }
    
    @Test
    void shouldThrowWhenAdvisorNotFound() {
        UUID investorId = UUID.randomUUID();
        UUID advisorId = UUID.randomUUID();

        when(investorRepository.findById(investorId)).thenReturn(Optional.of(investor));
        when(advisorRepository.findById(advisorId)).thenReturn(Optional.empty());

        MatchCreateDTO dto = new MatchCreateDTO(investorId, advisorId);

        assertThatThrownBy(() -> matchService.create(dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Advisor not found");
    }

    @Test
    void shouldReturnAllMatchesByInvestorId() {
        Match match = Match.builder()
                .id(UUID.randomUUID())
                .advisor(advisor)
                .investor(investor)
                .score(new BigDecimal("75.0"))
                .status(com.matchinvest.api.enums.MatchStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .build();

        when(investorRepository.findById(investor.getId())).thenReturn(Optional.of(investor));
        when(matchRepository.findByInvestor(investor))
                .thenReturn(java.util.List.of(match));

        var result = matchService.findByInvestor(investor.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).score()).isEqualByComparingTo(75.0);
    }


}
