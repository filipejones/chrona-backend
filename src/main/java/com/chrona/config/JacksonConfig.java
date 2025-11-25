package com.chrona.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonConfig {

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println("DEBUG: JacksonConfig initialized!");
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            System.out.println("DEBUG: JacksonCustomizer applied!");
        };
    }
}
