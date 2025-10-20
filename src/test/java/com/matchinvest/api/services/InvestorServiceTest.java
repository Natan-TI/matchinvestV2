package com.matchinvest.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class InvestorServiceTest {

    @Mock
    private InvestorRepository repo;

    @InjectMocks
    private InvestorService service;

    private Investor investor1;
    private Investor investor2;

    @BeforeEach
    void setup() {
        investor1 = createInvestor("João da Silva", "joao@email.com", new BigDecimal("10000.00"), "Aposentadoria");
        investor2 = createInvestor("Maria Souza", "maria@email.com", new BigDecimal("25000.00"), "Investir em ações");
    }

    private Investor createInvestor(String name, String email, BigDecimal amount, String goals) {
        Investor inv = new Investor();
        inv.setId(UUID.randomUUID());
        inv.setName(name);
        inv.setEmail(new Email(email));
        inv.setAvaliableAmount(new Money(amount));
        inv.setGoals(goals);
        inv.setRiskProfile(RiskProfile.MODERADO);
        inv.setCreatedAt(LocalDateTime.now());
        return inv;
    }

    @Test
    void shouldReturnAllInvestors() {
        when(repo.findAll()).thenReturn(List.of(investor1, investor2));

        var result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo("joao@email.com");
        verify(repo, times(1)).findAll();
    }

    @Test
    void shouldFindInvestorById() {
        UUID id = investor1.getId();
        when(repo.findById(id)).thenReturn(Optional.of(investor1));

        var result = service.findById(id);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("João da Silva");
        verify(repo, times(1)).findById(id);
    }

    @Test
    void shouldThrowWhenInvestorNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Investor not found");
    }
}
