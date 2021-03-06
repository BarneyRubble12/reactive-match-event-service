package com.bedrock.reactive.matchservice.config;

import java.util.function.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig {

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("Reactive API DEMO")
        .description("From Zero to Hello!")
        .termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
        .contact(new Contact("Daniel Hernandez", "https://github.com/BarneyRubble12",
            "barneyrubble.70777@gmail.com"))
        .license("Apache License Version 2.0")
        .licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
        .version("2.0")
        .build();
  }

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2).enable(true)
        .groupName("Reactive API")
        .apiInfo(apiInfo())
        .select()
        .paths(regex("/match.*"))
        // .paths(not(PathSelectors.regex("/actuator.*")))
        .build();
  }

  private static <T> Predicate<T> not(Predicate<T> input) {
    return it -> !input.test(it);
  }
}
