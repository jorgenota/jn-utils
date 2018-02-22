package com.jorgenota.utils.springboot.aws.sqs.consumer.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.sqs.consumer")
public class SqsConsumerConfigurationProperties {
    private String region;
    private String endpoint;
    private int maxNumberOfMessages = 10;
    private long backOffTime = 10000;
}
