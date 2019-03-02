package com.jorgenota.utils.springboot.aws.sqs.autoconfigure;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({SesConfigurationProperties.class})
public class SesConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonSimpleEmailServiceAsync amazonSes(SesConfigurationProperties config, AwsEnvironment awsEnvironment) {
        AmazonSimpleEmailServiceAsyncClientBuilder builder = AmazonSimpleEmailServiceAsyncClientBuilder.standard();
        awsEnvironment.configureAwsClientBuilder(builder, config.getRegion(), config.getEndpoint(), null);
        return builder.build();
    }
}
