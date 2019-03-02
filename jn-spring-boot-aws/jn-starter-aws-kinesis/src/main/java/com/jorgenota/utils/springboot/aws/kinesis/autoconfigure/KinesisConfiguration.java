package com.jorgenota.utils.springboot.aws.kinesis.autoconfigure;

import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({KinesisConfigurationProperties.class})
public class KinesisConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonKinesisAsync amazonKinesis(KinesisConfigurationProperties config, AwsEnvironment awsEnvironment) {
        AmazonKinesisAsyncClientBuilder builder = AmazonKinesisAsyncClientBuilder.standard();
        awsEnvironment.configureAwsClientBuilder(builder, config.getRegion(), config.getEndpoint(), null);
        return builder.build();
    }
}
