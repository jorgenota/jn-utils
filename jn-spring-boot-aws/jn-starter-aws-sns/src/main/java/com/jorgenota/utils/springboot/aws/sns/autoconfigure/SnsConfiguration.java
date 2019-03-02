package com.jorgenota.utils.springboot.aws.sns.autoconfigure;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({SnsConfigurationProperties.class})
public class SnsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonSNSAsync amazonSns(SnsConfigurationProperties config, AwsEnvironment awsEnvironment) {
        AmazonSNSAsyncClientBuilder builder = AmazonSNSAsyncClientBuilder.standard();
        awsEnvironment.configureAwsClientBuilder(builder, config.getRegion(), config.getEndpoint(), null);
        return builder.build();
    }
}
