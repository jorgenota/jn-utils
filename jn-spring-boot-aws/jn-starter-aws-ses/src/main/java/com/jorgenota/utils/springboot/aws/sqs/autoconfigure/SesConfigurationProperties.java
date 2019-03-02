package com.jorgenota.utils.springboot.aws.sqs.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.ses")
public class SesConfigurationProperties {
    @Nullable
    private String region;
    @Nullable
    private String endpoint;
}
