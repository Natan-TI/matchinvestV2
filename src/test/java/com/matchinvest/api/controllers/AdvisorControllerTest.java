package com.matchinvest.api.controllers;

import com.matchinvest.api.entities.Advisor;
import com.matchinvest.api.enums.InvestmentFocus;
import com.matchinvest.api.repositories.AdvisorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdvisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdvisorRepository advisorRepository;

    @Test
    void shouldCreateAdvisor() throws Exception {
        String json = """
                {
                  "name": "Maria Souza",
                  "email": "maria.souza@exemplo.com",
                  "certifications": "CFA",
                  "investmentFocus": "ACOES",
                  "yearsExperience": 8
                }
                """;

        mockMvc.perform(post("/api/advisors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Souza"))
                .andExpect(jsonPath("$.investmentFocus").value(InvestmentFocus.ACOES.name()));
    }
}
