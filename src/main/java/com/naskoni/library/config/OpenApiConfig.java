package com.naskoni.library.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${api.version}")
  private String apiVersion;

  @Bean
  public OpenAPI libraryOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Library Rest API")
            .description("Library Rest API Documentation with OpenAPI")
            .version(apiVersion))
        .components(new Components()
            .addSecuritySchemes(
                "basicAuth",
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
            ))
        .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
  }
}