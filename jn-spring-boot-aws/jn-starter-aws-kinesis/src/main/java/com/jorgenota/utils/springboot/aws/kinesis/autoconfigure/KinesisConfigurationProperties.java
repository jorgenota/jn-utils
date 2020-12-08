package com.jorgenota.utils.springboot.aws.kinesis.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.kinesis")
public class KinesisConfigurationProperties extends AWSClientProperties {
}
