package com.jorgenota.utils.springboot.aws.kinesis.producer.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.jorgenota.utils.aws.kinesis.SimpleKinesisClient;
import com.jorgenota.utils.base.Preconditions;
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
    KinesisProducerConfigurationProperties.class})
public class KinesisProducerConfiguration {

    @Autowired
    private KinesisProducerConfigurationProperties configuration;

    @ConditionalOnMissingBean
    @Bean
    public AmazonKinesis amazonKinesis() {
        AmazonKinesisClientBuilder builder = AmazonKinesisClientBuilder.standard();
        if (!StringUtils.isEmpty(configuration.getRegion())) {
            if (!StringUtils.isEmpty(configuration.getEndpoint())) {
                builder.setEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(configuration.getEndpoint(), configuration.getRegion()));
            } else {
                builder.setRegion(configuration.getRegion());
            }
        } else if (!StringUtils.isEmpty(configuration.getEndpoint())) {
            Preconditions.empty(configuration.getEndpoint(), "Endpoint should be empty if region is empty");
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
