package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties {
    @Nullable
    private String region;
    @Nullable
    private String endpoint;
}
