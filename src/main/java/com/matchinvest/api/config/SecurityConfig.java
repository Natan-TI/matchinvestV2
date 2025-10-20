// SecurityConfig.java
package com.matchinvest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.matchinvest.api.security.JwtAuthenticationFilter;
import com.matchinvest.api.security.JwtTokenService;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtTokenService tokenService, UserDetailsService userDetailsService) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
    		  
    	// Rotas públicas
        .requestMatchers(
          "/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html"
        ).permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/login","/api/auth/register").permitAll()
        
        // Rotas de leitura abertas
        .requestMatchers(HttpMethod.GET, "/api/advisors/**", "/api/investors/**", "/api/matches/**").permitAll()
        
        // Criação, atualização e exclusão restritas a ADMIN
        .requestMatchers(HttpMethod.POST, "/api/advisors/**", "/api/investors/**", "/api/matches/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/investors/**").authenticated()
        .requestMatchers(HttpMethod.PATCH, "/api/investors/**").authenticated()
        .requestMatchers(HttpMethod.DELETE, "/api/investors/**").authenticated()
        
        // Qualquer outra rota requer autenticação
        .anyRequest().authenticated()
      );

    http.addFilterBefore(new JwtAuthenticationFilter(tokenService, userDetailsService),
            UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
