package com.chrona.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chrona.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private long expirationMillis = 86400000;
}
