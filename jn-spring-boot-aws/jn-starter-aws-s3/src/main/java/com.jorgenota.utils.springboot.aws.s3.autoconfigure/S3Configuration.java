package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({S3ConfigurationProperties.class})
public class S3Configuration {

    @Bean
    @ConditionalOnMissingBean
    public AmazonS3 amazonS3(S3ConfigurationProperties config, AwsEnvironment awsEnvironment) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
        awsEnvironment.configureAwsClientBuilder(builder, config, null);

        if (config.getPathStyleAccess() != null) {
            builder.setPathStyleAccessEnabled(config.getPathStyleAccess());
        }
        if ((config.getChunkedEncodingDisabled() != null) && config.getChunkedEncodingDisabled()) {
            builder.disableChunkedEncoding();
        }
        if (config.getAccelerateModeEnabled() != null) {
            builder.setAccelerateModeEnabled(config.getAccelerateModeEnabled());
        }
        if (config.getPayloadSigningEnabled() != null) {
            builder.setPayloadSigningEnabled(config.getPayloadSigningEnabled());
        }
        if ((config.getDualstackEnabled() != null) && config.getDualstackEnabled()) {
            builder.enableDualstack();
        }

        return builder.build();
    }
}
