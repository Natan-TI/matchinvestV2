package com.matchinvest.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("📈 MatchInvest API")
                .description("API para conectar **investidores** e **assessores de investimento**.\n\n" +
                             "Endpoints organizados em três grupos principais:\n" +
                             "- 👤 **Investors**: CRUD de investidores.\n" +
                             "- 🧑‍💼 **Advisors**: CRUD de assessores.\n" +
                             "- 🤝 **Matches**: criação e gerenciamento de conexões entre investidores e assessores.\n\n" +
                             "Desenvolvido como projeto acadêmico, seguindo boas práticas REST, DTOs, VO e validações.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Equipe MatchInvest")
                        .url("https://github.com/Natan-TI/matchinvestV2"))
                .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
