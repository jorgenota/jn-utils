package com.jorgenota.utils.springboot.aws.sns.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.sns")
public class SnsConfigurationProperties {
    @Nullable
    private String region;
    @Nullable
    private String endpoint;
}
