package com.jorgenota.utils.springboot.aws.s3.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties {
    private String region;
    private String endpoint;
    private int retryAttempts = 1;
    private long retrySleepTime;
}
