package com.jorgenota.utils.springboot.aws.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.context")
public class AwsConfigurationProperties {
    private boolean autoDetectRegion = true;
    @Nullable
    private String staticRegion;

    private boolean useDefaultCredentialsChain = true;
    @Nullable
    private String accessKey;
    @Nullable
    private String secretKey;
}
