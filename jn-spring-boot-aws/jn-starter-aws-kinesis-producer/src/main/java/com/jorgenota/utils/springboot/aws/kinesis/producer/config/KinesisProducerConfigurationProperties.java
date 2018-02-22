package com.jorgenota.utils.springboot.aws.kinesis.producer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.kinesis.producer")
public class KinesisProducerConfigurationProperties {
    private String region;
    private String endpoint;
    private int retryAttempts = 1;
    private long retrySleepTime;
}
