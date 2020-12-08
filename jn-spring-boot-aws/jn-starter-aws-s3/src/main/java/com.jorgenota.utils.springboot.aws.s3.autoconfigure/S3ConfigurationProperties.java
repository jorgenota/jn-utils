package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.s3")
public class S3ConfigurationProperties extends AWSClientProperties {
}
