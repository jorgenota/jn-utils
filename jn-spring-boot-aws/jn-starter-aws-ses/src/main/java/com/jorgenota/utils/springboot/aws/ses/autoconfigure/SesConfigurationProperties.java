package com.jorgenota.utils.springboot.aws.ses.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.ses")
public class SesConfigurationProperties extends AWSClientProperties {
}
