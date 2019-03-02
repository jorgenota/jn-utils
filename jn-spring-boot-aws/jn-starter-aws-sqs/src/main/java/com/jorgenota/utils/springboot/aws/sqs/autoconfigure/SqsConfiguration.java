package com.jorgenota.utils.springboot.aws.sqs.autoconfigure;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({SqsConfigurationProperties.class})
public class SqsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonSQSAsync amazonSqs(SqsConfigurationProperties config, AwsEnvironment awsEnvironment) {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard();
        awsEnvironment.configureAwsClientBuilder(builder, config.getRegion(), config.getEndpoint(), null);
        return builder.build();
    }
}
