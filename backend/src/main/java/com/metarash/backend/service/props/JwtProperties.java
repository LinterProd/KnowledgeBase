package com.metarash.backend.service.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String accessSecret;
    private String refreshSecret;
    private Duration accessExpiration;
    private Duration refreshExpiration;

    // Геттеры для получения в миллисекундах (если нужно)
    public long getAccessExpirationMs() {
        return accessExpiration.toMillis();
    }

    public long getRefreshExpirationMs() {
        return refreshExpiration.toMillis();
    }
}