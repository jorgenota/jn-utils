package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties extends AWSClientProperties {
    @Nullable
    private Boolean pathStyleAccess;
    @Nullable
    private Boolean chunkedEncodingDisabled;
    @Nullable
    private Boolean accelerateModeEnabled;
    @Nullable
    private Boolean payloadSigningEnabled;
    @Nullable
    private Boolean dualstackEnabled;
}
