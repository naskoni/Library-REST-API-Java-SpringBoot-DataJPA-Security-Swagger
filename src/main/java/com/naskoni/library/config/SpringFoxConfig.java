package com.naskoni.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Swagger UI locally - http://localhost:8080/library/api/swagger-ui.html */
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig implements WebMvcConfigurer {

  @Value("${api.version}")
  private String apiVersion;

  @Bean
  public Docket api() {
    List<SecurityScheme> schemes = new ArrayList<>();
    schemes.add(new BasicAuth("basicAuth"));

    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.naskoni.library"))
        .paths(PathSelectors.any())
        .build()
        .directModelSubstitute(Date.class, String.class)
        .genericModelSubstitutes(ResponseEntity.class)
        .apiInfo(apiInfo())
        .securitySchemes(schemes)
        .securityContexts(Arrays.asList(securityContext()));
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Library Rest API")
        .description("Library Rest API Documentation with Swagger")
        .termsOfServiceUrl("Terms of service")
        .version(apiVersion)
        .build();
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(Arrays.asList(basicAuthReference()))
        .forPaths(PathSelectors.any())
        .build();
  }

  private SecurityReference basicAuthReference() {
    return new SecurityReference("basicAuth", new AuthorizationScope[0]);
  }
}
