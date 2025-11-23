package com.metarash.backend.config.props;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "minio")
public record MinioProperties(
        @NotBlank @URL String url,
        @NotBlank String accessKey,
        @NotBlank String secretKey,
        @NotBlank String bucketName,
        boolean secure // с дефолтным значением false
) {
    public String getFullUrl() {
        return url + "/" + bucketName;
    }
}