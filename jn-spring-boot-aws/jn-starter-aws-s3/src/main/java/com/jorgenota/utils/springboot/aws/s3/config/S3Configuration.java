package com.jorgenota.utils.springboot.aws.s3.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jorgenota.utils.aws.s3.SimpleS3Client;
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
            builder.setRegion(configuration.getRegion());
        }
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public SimpleS3Client simpleS3Client() {
        if (configuration.getRetryAttempts() > 1)
            return new SimpleS3Client(amazonS3(), configuration.getRetryAttempts(), configuration.getRetrySleepTime());
        else {
            return new SimpleS3Client(amazonS3());
        }
    }

}
