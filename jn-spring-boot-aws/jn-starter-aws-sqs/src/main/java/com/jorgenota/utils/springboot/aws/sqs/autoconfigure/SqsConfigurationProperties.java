package com.jorgenota.utils.springboot.aws.sqs.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.sqs")
public class SqsConfigurationProperties extends AWSClientProperties {
}
