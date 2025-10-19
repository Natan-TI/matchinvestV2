package com.matchinvest.api.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

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
class InvestorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================
    // Métodos auxiliares
    // =========================
    private String adminToken() throws Exception {
        String login = """
            { "email": "admin@matchinvest.com", "password": "admin123" }
        """;

        String resp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(resp).get("token").asText();
    }

    private String investorToken() throws Exception {
        var email = "investortestcase@match.com";
        var reg = objectMapper.writeValueAsString(
            new RegisterRequest(
                "Investor Test",
                email,
                "123456",
                AccountType.INVESTOR,
                new InvestorRegisterData(
                    "Aposentadoria",
                    new BigDecimal("15000.00"),
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

    // =========================
    // TESTES
    // =========================

    // Deve permitir ADMIN criar um novo investor
    @Test
    void shouldCreateInvestorAsAdmin_201() throws Exception {
        String token = adminToken();

        String json = """
            {
              "name": "Carlos Lima",
              "email": "carlos.lima@exemplo.com",
              "goals": "Investir para aposentadoria",
              "avaliableAmount": 20000.00,
              "riskProfile": "MODERADO"
            }
            """;

        mockMvc.perform(post("/api/investors")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Carlos Lima"))
            .andExpect(jsonPath("$.riskProfile").value("MODERADO"));
    }

    // Deve negar criação sem token (403)
    @Test
    void shouldRejectWithoutToken_403() throws Exception {
        var json = """
            {
              "name":"Investidor Sem Token",
              "email":"investidor.sem.token@exemplo.com",
              "goals":"Lucro rápido",
              "avaliableAmount":5000.00,
              "riskProfile":"ARROJADO"
            }
            """;

        mockMvc.perform(post("/api/investors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isForbidden());
    }

    // Deve negar criação com token de usuário comum (403)
    @Test
    void shouldForbidUserRoleOnCreate_403() throws Exception {
        var token = investorToken();
        var json = """
            {
              "name":"Pedro User",
              "email":"pedro.user@exemplo.com",
              "goals":"Testando restrição",
              "avaliableAmount":3000.00,
              "riskProfile":"MODERADO"
            }
            """;

        mockMvc.perform(post("/api/investors")
                .header("Authorization","Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isForbidden());
    }

    // Deve permitir INVESTOR atualizar o próprio perfil (200)
    @Test
    void shouldAllowInvestorToUpdateOwnProfile_200() throws Exception {
        String token = investorToken();

        // Busca lista de investors e pega o primeiro ID
        var getResponse = mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/investors")
                    .header("Authorization", "Bearer " + token)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        var json = new com.fasterxml.jackson.databind.ObjectMapper().readTree(getResponse);
        UUID investorId = UUID.fromString(json.get(0).get("id").asText());
        
        for (var node : json) {
            if (node.get("email").asText().equals("investortestcase@match.com")) {
                investorId = UUID.fromString(node.get("id").asText());
                break;
            }
        }

        assert investorId != null : "Investor não encontrado para o email especificado";

        String updateJson = """
            {
              "goals":"Alterei minha meta de investimento",
              "avaliableAmount":18000.00,
              "riskProfile":"CONSERVADOR"
            }
            """;

        mockMvc.perform(put("/api/investors/" + investorId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.riskProfile").value("CONSERVADOR"));
    }

}
