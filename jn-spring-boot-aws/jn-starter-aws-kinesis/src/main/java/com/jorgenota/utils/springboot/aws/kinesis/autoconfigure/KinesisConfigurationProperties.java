package com.jorgenota.utils.springboot.aws.kinesis.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.kinesis")
public class KinesisConfigurationProperties {
    @Nullable
    private String region;
    @Nullable
    private String endpoint;
}
