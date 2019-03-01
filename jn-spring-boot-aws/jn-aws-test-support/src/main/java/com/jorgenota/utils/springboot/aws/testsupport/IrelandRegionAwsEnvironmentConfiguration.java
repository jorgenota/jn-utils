package com.jorgenota.utils.springboot.aws.testsupport;

import com.jorgenota.utils.springboot.aws.autoconfigure.AwsConfigurationProperties;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
public class IrelandRegionAwsEnvironmentConfiguration {
    @Bean
    AwsEnvironment awsEnvironment() {
        AwsConfigurationProperties config = new AwsConfigurationProperties();
        config.setStaticRegion("eu-west-1");
        return new AwsEnvironment(config);
    }
}
