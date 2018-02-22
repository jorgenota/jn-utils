package com.jorgenota.utils.springboot.aws.kinesis.consumer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.kinesis.consumer")
public class KinesisConsumerConfigurationProperties {
    private String region;
    private String endpoint;
    private int retryAttempts = 1;
    private long retrySleepTime;
    private long checkPointInterval = 30000;
    private List<StreamConfig> streams = new ArrayList<>();

    @Data
    class StreamConfig {
        String name;
        int maxRecords = 100;
    }
}
