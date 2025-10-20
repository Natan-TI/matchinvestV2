package com.matchinvest.api.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.matchinvest.api.entities.Role;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.repositories.RoleRepository;
import com.matchinvest.api.repositories.UserRepository;

@TestConfiguration
public class TestDataInitializer {

    @Bean
    public CommandLineRunner seedTestData(
            RoleRepository roleRepo,
            UserRepository userRepo,
            PasswordEncoder encoder) {

        return args -> {

            // Cria as roles se não existirem
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
            Role investorRole = roleRepo.findByName("ROLE_INVESTOR")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_INVESTOR")));
            Role advisorRole = roleRepo.findByName("ROLE_ADVISOR")
                    .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADVISOR")));

            // Cria o usuário admin se não existir
            userRepo.findByEmail("admin@matchinvest.com").orElseGet(() -> {
                User admin = User.builder()
                        .name("Admin")
                        .email("admin@matchinvest.com")
                        .password(encoder.encode("admin123"))
                        .enabled(true)
                        .roles(new HashSet<>(Set.of(adminRole)))
                        .build();
                return userRepo.save(admin);
            });
        };
    }
}
