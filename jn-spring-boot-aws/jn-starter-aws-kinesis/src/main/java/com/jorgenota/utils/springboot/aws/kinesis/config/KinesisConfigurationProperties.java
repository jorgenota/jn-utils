package com.jorgenota.utils.springboot.aws.kinesis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.kinesis")
public class KinesisConfigurationProperties {
    private String region;
    private int retryAttempts = 1;
    private long retrySleepTime;
}
