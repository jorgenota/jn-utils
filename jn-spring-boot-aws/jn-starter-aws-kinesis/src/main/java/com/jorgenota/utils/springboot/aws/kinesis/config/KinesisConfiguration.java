package com.jorgenota.utils.springboot.aws.kinesis.config;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.jorgenota.utils.aws.kinesis.SimpleKinesisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({
    KinesisConfigurationProperties.class})
public class KinesisConfiguration {

    @Autowired
    private KinesisConfigurationProperties configuration;

    @ConditionalOnMissingBean
    @Bean
    public AmazonKinesis amazonKinesis() {
        AmazonKinesisClientBuilder builder = AmazonKinesisClientBuilder.standard();
        if (!StringUtils.isEmpty(configuration.getRegion())) {
            builder.setRegion(configuration.getRegion());
        }
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public SimpleKinesisClient simpleKinesisClient() {
        if (configuration.getRetryAttempts() > 1)
            return new SimpleKinesisClient(amazonKinesis(), configuration.getRetryAttempts(), configuration.getRetrySleepTime());
        else {
            return new SimpleKinesisClient(amazonKinesis());
        }
    }
}
