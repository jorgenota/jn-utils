package com.jorgenota.utils.springboot.aws.s3.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jorgenota.utils.aws.s3.S3ObjectStore;
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
@EnableConfigurationProperties({S3ConfigurationProperties.class})
public class S3Configuration {

    @Autowired
    private S3ConfigurationProperties configuration;

    @ConditionalOnMissingBean
    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
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
    public S3ObjectStore simpleS3Client() {
        if (configuration.getRetryAttempts() > 1)
            return new S3ObjectStore(amazonS3(), configuration.getRetryAttempts(), configuration.getRetrySleepTime());
        else {
            return new S3ObjectStore(amazonS3());
        }
    }

}
