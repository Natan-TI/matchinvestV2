package com.matchinvest.api.services.impl;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matchinvest.api.entities.Role;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.repositories.RoleRepository;
import com.matchinvest.api.repositories.UserRepository;
import com.matchinvest.api.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository repo, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.repo = repo;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public User registerUser(String name, String email, String rawPassword) {
        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(encoder.encode(rawPassword));

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("Role padrão 'ROLE_USER' não encontrada."));

        user.setRoles(Set.of(defaultRole));

        return repo.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmailOrThrow(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}
