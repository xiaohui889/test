package com.xbot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("xbot 后台api")
                        .description("基于 Spring Boot 3 + JPA 的后台api")
                        .version("v1.0.0")
                        .license(new License()
                                .name("Apache 2.0").url("http://springdoc.org")));
    }
}