package com.matchinvest.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.vo.Email;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class AdvisorServiceTest {

    @Mock
    private AdvisorRepository repo;

    @InjectMocks
    private AdvisorService service;

    @Test
    void shouldReturnAllAdvisors() {
        Advisor adv1 = new Advisor();
        adv1.setId(UUID.randomUUID());
        adv1.setName("Maria Souza");
        adv1.setEmail(new Email("maria@email.com"));
        adv1.setCertifications("CFA");
        adv1.setYearsExperience(5);

        Advisor adv2 = new Advisor();
        adv2.setId(UUID.randomUUID());
        adv2.setName("Carlos Lima");
        adv2.setEmail(new Email("carlos@email.com"));
        adv2.setCertifications("CPA-20");
        adv2.setYearsExperience(3);

        when(repo.findAll()).thenReturn(List.of(adv1, adv2));

        var result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).email()).isEqualTo("maria@email.com");
        verify(repo).findAll();
    }


    @Test
    void shouldReturnAdvisorById() {
        var advisor = new Advisor();
        advisor.setId(UUID.randomUUID());
        advisor.setName("Maria Souza");
        advisor.setEmail(new Email("maria@email.com")); 

        UUID id = advisor.getId();
        when(repo.findById(id)).thenReturn(Optional.of(advisor));

        var result = service.findById(id);

        assertThat(result.name()).isEqualTo("Maria Souza");
        assertThat(result.email()).isEqualTo("maria@email.com");
        verify(repo).findById(id);
    }


    @Test
    void shouldThrowWhenAdvisorNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Advisor not found");
    }
}
