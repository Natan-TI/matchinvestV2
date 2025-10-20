package com.matchinvest.api.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.matchinvest.api.entities.Role;
import com.matchinvest.api.entities.User;
import com.matchinvest.api.repositories.RoleRepository;
import com.matchinvest.api.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role defaultRole;

    @BeforeEach
    void setup() {
        defaultRole = new Role(UUID.randomUUID(), "ROLE_USER");
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepo.existsByEmail("novo@mail.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(encoder.encode("123456")).thenReturn("encoded123");
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = userService.registerUser("Novo", "novo@mail.com", "123456");

        assertThat(result.getEmail()).isEqualTo("novo@mail.com");
        assertThat(result.getPassword()).isEqualTo("encoded123");
        assertThat(result.getRoles()).extracting(Role::getName).contains("ROLE_USER");

        verify(userRepo).existsByEmail("novo@mail.com");
        verify(roleRepo).findByName("ROLE_USER");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        when(userRepo.existsByEmail("jaexiste@mail.com")).thenReturn(true);

        assertThatThrownBy(() ->
            userService.registerUser("Nome", "jaexiste@mail.com", "123")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("E-mail já cadastrado");

        verify(userRepo).existsByEmail("jaexiste@mail.com");
        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldThrowWhenDefaultRoleNotFound() {
        when(userRepo.existsByEmail("semrole@mail.com")).thenReturn(false);
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            userService.registerUser("SemRole", "semrole@mail.com", "123456")
        )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Role padrão 'ROLE_USER' não encontrada");

        verify(userRepo).existsByEmail("semrole@mail.com");
        verify(roleRepo).findByName("ROLE_USER");
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        User user = new User();
        user.setEmail("find@mail.com");

        when(userRepo.findByEmail("find@mail.com")).thenReturn(Optional.of(user));

        User found = userService.findByEmailOrThrow("find@mail.com");

        assertThat(found.getEmail()).isEqualTo("find@mail.com");
        verify(userRepo).findByEmail("find@mail.com");
    }

    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepo.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            userService.findByEmailOrThrow("missing@mail.com")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Usuário não encontrado");

        verify(userRepo).findByEmail("missing@mail.com");
    }
}
