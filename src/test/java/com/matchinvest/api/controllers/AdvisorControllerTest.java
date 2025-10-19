package com.matchinvest.api.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchinvest.api.dto.auth.AccountType;
import com.matchinvest.api.dto.auth.AuthRequest;
import com.matchinvest.api.dto.auth.InvestorRegisterData;
import com.matchinvest.api.dto.auth.RegisterRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdvisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    private String authenticateAndGetToken() throws Exception {
        String jsonLogin = """
            { "email": "admin@matchinvest.com", "password": "admin123" }
        """;

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLogin))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
    
    private String userToken() throws Exception {
        var email = "user" + System.currentTimeMillis() + "@match.com";

        var reg = objectMapper.writeValueAsString(
            new RegisterRequest(
                "User Test",
                email,
                "123456",
                AccountType.INVESTOR,
                new InvestorRegisterData(
                	"Metas",
                    new BigDecimal("5000.00"),
                    "MODERADO"
                ),
                null
            )
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reg))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());

        var login = objectMapper.writeValueAsString(new AuthRequest(email, "123456"));
        var resp = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(login))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(resp).get("token").asText();
    }


    // 201: ADMIN cria advisor
    @Test
    void shouldCreateAdvisor() throws Exception {
        String token = authenticateAndGetToken();

        String json = """
            {
              "name": "Julia Dias",
              "email": "julia.dias@exemplo.com",
              "yearsExperience": 8,
              "investmentFocus": "ACOES",
              "certifications": "CFA"
            }
            """;

        mockMvc.perform(post("/api/advisors")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Julia Dias"))
                .andExpect(jsonPath("$.investmentFocus").value("ACOES"));
    }
    
    // 403: sem token
    @Test
    void shouldRejectWithoutToken_403() throws Exception {
        var json = """
            {
              "name":"Ana Sem Token",
              "email":"ana.sem.token@exemplo.com",
              "certifications":"CFA",
              "investmentFocus":"ACOES",
              "yearsExperience":5
            }
            """;
        mockMvc.perform(post("/api/advisors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isForbidden());
    }

    
    // 403: com ROLE_USER tentando acessar endpoint de ADMIN
    @Test
    void shouldForbidUserRoleOnCreate_403() throws Exception {
        var token = userToken();
        var json = """
            {
              "name":"Pedro User",
              "email":"pedro.user@exemplo.com",
              "certifications":"CPA-20",
              "investmentFocus":"ACOES",
              "yearsExperience":4
            }
            """;
        mockMvc.perform(post("/api/advisors")
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isForbidden());
    }

}
