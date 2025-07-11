package com.social.horror_pool.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Horror Pool API")
                        .version("0.1")
                        .description("Backend for a horror movie catalog platform")
                        .contact(new Contact()
                                .name("Ilya Ibragimov")
                                .url("https://github.com/IlyaIbragimov/horror_pool")
                        )
                );
    }
}
