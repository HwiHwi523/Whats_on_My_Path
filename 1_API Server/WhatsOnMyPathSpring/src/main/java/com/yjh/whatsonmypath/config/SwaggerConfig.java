package com.yjh.whatsonmypath.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    private ApiInfo swaggerInfo() {
        return new ApiInfoBuilder()
                .title("Whats on My Path?")
                .description("Whats on My Path? API Document")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("public-api")
                .apiInfo(swaggerInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yjh.whatsonmypath.controller"))
                .build();
    }
}
