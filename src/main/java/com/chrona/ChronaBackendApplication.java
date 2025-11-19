package com.chrona;

import com.chrona.config.ChronaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ChronaProperties.class)
public class ChronaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronaBackendApplication.class, args);
    }
}
