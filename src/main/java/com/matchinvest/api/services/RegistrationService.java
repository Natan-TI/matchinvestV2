package com.matchinvest.api.services;

import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.dto.auth.AuthResponse;
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
import com.matchinvest.api.vo.Email;
import com.matchinvest.api.vo.Money;

@Service
public class RegistrationService {

  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final AdvisorRepository advisorRepo;
  private final InvestorRepository investorRepo;
  private final PasswordEncoder encoder;
  private final JwtTokenService tokenService;
  
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RegistrationService.class);

  public RegistrationService(
      UserRepository userRepo,
      RoleRepository roleRepo,
      AdvisorRepository advisorRepo,
      InvestorRepository investorRepo,
      PasswordEncoder encoder,
      JwtTokenService tokenService) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.advisorRepo = advisorRepo;
    this.investorRepo = investorRepo;
    this.encoder = encoder;
    this.tokenService = tokenService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest req) {
    if (userRepo.existsByEmail(req.email())) {
      throw new IllegalArgumentException("E-mail já cadastrado");
    }

    // 1) cria o User com a role certa
    User u = new User();
    u.setName(req.name());
    u.setEmail(req.email());
    u.setPassword(encoder.encode(req.password()));

    String roleName = switch (req.accountType()) {
      case INVESTOR -> "ROLE_INVESTOR";
      case ADVISOR  -> "ROLE_ADVISOR";
    };

    Role role = roleRepo.findByName(roleName)
      .orElseThrow(() -> new IllegalStateException("Role não encontrada: " + roleName));

    u.setRoles(Set.of(role));
    u = userRepo.save(u);

    // 2) cria o perfil vinculado
    switch (req.accountType()) {
      case INVESTOR -> {
        if (req.investor() == null) throw new IllegalArgumentException("Dados de investor são obrigatórios");
        var d = req.investor();
        Investor inv = new Investor();
        inv.setUser(u);
        inv.setName(u.getName());
        inv.setEmail(new Email(u.getEmail()));
        inv.setGoals(d.goals());
        inv.setAvaliableAmount(new Money(d.avaliableAmount()));
        inv.setRiskProfile(RiskProfile.valueOf(d.riskProfile().toUpperCase()));
        investorRepo.save(inv);
      }
      case ADVISOR -> {
        if (req.advisor() == null) throw new IllegalArgumentException("Dados de advisor são obrigatórios");
        var d = req.advisor();
        Advisor adv = new Advisor();
        adv.setUser(u);
        adv.setName(u.getName());
        adv.setEmail(new Email(u.getEmail()));
        adv.setCertifications(d.certifications());
        adv.setInvestmentFocus(InvestmentFocus.valueOf(d.investmentFocus().toUpperCase()));
        adv.setYearsExperience(d.yearsExperience());
        advisorRepo.save(adv);
      }
    }

    // 3) Gera token
    String token = tokenService.generate(
      u.getEmail(),
      Map.of("roles", u.getRoles().stream().map(Role::getName).toList(),
             "accountType", req.accountType().name())
    );
    
    logger.info("Usuário '{}' registrou-se e aceitou os termos em {}", req.email(), java.time.LocalDateTime.now());

    return new AuthResponse(token, "Bearer", 3600000);
  }
}
