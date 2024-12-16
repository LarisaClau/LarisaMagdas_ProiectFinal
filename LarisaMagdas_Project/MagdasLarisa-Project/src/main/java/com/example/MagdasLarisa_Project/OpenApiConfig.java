package com.example.MagdasLarisa_Project;

import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MagdasLarisa Project API")
                        .version("1.0")
                        .description("Documentația API-ului pentru MagdasLarisa Project"));
    }
}
