package com.jorgenota.utils.springboot.aws.sns.autoconfigure;

import com.jorgenota.utils.springboot.aws.support.AWSClientProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Jorge Alonso
 */
@Data
@ConfigurationProperties("aws.sns")
public class SnsConfigurationProperties extends AWSClientProperties {
}
