package com.matchinvest.api.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.matchinvest.api.dto.auth.AccountType;
import com.matchinvest.api.dto.auth.AdvisorRegisterData;
import com.matchinvest.api.dto.auth.InvestorRegisterData;
import com.matchinvest.api.dto.auth.RegisterRequest;
import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.entities.Investor;
import com.matchinvest.api.entities.Role;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.enums.InvestmentFocus;
import com.matchinvest.api.enums.RiskProfile;
import com.matchinvest.api.repositories.AdvisorRepository;
import com.matchinvest.api.repositories.InvestorRepository;
import com.matchinvest.api.repositories.RoleRepository;
import com.matchinvest.api.repositories.UserRepository;
import com.matchinvest.api.security.JwtTokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegistrationServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private RoleRepository roleRepo;
    @Mock private AdvisorRepository advisorRepo;
    @Mock private InvestorRepository investorRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtTokenService tokenService;

    @InjectMocks private RegistrationService registrationService;

    private Role investorRole;
    private Role advisorRole;

    @BeforeEach
    void setup() {
        investorRole = new Role();
        investorRole.setName("ROLE_INVESTOR");

        advisorRole = new Role();
        advisorRole.setName("ROLE_ADVISOR");

        when(encoder.encode(any())).thenReturn("encoded123");
        when(tokenService.generate(any(), any())).thenReturn("fake-jwt-token");
    }

    @Test
    void shouldRegisterInvestorSuccessfully() {
        when(userRepo.existsByEmail("inv@test.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_INVESTOR")).thenReturn(Optional.of(investorRole));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        InvestorRegisterData investorData = new InvestorRegisterData(
                "Aposentadoria",
                new BigDecimal("10000"),
                RiskProfile.CONSERVADOR.name()
        );

        RegisterRequest req = new RegisterRequest(
                "Investor Test",
                "inv@test.com",
                "123456",
                AccountType.INVESTOR,
                investorData,
                null
        );

        var resp = registrationService.register(req);

        assertThat(resp.token()).isEqualTo("fake-jwt-token");
        verify(investorRepo, times(1)).save(any(Investor.class));
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void shouldRegisterAdvisorSuccessfully() {
        when(userRepo.existsByEmail("adv@test.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_ADVISOR")).thenReturn(Optional.of(advisorRole));
        when(userRepo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AdvisorRegisterData advisorData = new AdvisorRegisterData(
                "CFA",
                InvestmentFocus.ACOES.name(),
                10
        );

        RegisterRequest req = new RegisterRequest(
                "Advisor Test",
                "adv@test.com",
                "123456",
                AccountType.ADVISOR,
                null,
                advisorData
        );

        var resp = registrationService.register(req);

        assertThat(resp.token()).isEqualTo("fake-jwt-token");
        verify(advisorRepo, times(1)).save(any(Advisor.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepo.existsByEmail("dup@test.com")).thenReturn(true);

        RegisterRequest req = new RegisterRequest(
                "Dup",
                "dup@test.com",
                "123456",
                AccountType.INVESTOR,
                new InvestorRegisterData("meta", new BigDecimal("1000"), "MODERADO"),
                null
        );

        assertThatThrownBy(() -> registrationService.register(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    void shouldThrowWhenRoleNotFound() {
        when(userRepo.existsByEmail("x@test.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_INVESTOR")).thenReturn(Optional.empty());

        RegisterRequest req = new RegisterRequest(
                "User",
                "x@test.com",
                "123456",
                AccountType.INVESTOR,
                new InvestorRegisterData("meta", new BigDecimal("1000"), "MODERADO"),
                null
        );

        assertThatThrownBy(() -> registrationService.register(req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Role não encontrada");
    }
}
