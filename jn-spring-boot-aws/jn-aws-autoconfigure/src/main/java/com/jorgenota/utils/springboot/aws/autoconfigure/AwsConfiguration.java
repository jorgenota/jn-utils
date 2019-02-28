package com.jorgenota.utils.springboot.aws.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({AwsConfigurationProperties.class})
public class AwsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    AwsEnvironment awsEnvironment(AwsConfigurationProperties config) {
        return new AwsEnvironment(config);
    }

}
