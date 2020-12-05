package com.jorgenota.utils.springboot.aws.support;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.util.EC2MetadataUtils;
import com.jorgenota.utils.springboot.aws.autoconfigure.AwsConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author Jorge Alonso
 */
public class AwsEnvironment {

    private String defaultRegion;
    private AWSCredentialsProvider defaultCredentialsProvider;

    public AwsEnvironment(AwsConfigurationProperties config) {
        initRegion(config.isAutoDetectRegion(), config.getStaticRegion());
        initCredentialsProvider(config.isUseDefaultCredentialsChain(), config.getAccessKey(), config.getSecretKey());
    }

    private void initRegion(boolean autoDetect, @Nullable String region) {
        if (StringUtils.hasLength(region)) {
            this.defaultRegion = region;
        } else if (autoDetect) {
            try {
                EC2MetadataUtils.InstanceInfo instanceInfo = EC2MetadataUtils.getInstanceInfo();
                if (instanceInfo != null) {
                    this.defaultRegion = instanceInfo.getRegion();
                }
            } catch (AmazonClientException e) {
                // Do nothing... but defaultRegion is not set
            }

            Assert.notNull(this.defaultRegion, "Region detection is only possible if the application is running on an EC2 instance");
        } else {
            throw new IllegalArgumentException("A staticRegion must be provided in case it is not being autodetected");
        }
    }

    private void initCredentialsProvider(boolean useDefaultCredentialsChain, @Nullable String accessKey, @Nullable String secretKey) {
        if (StringUtils.hasLength(accessKey) && StringUtils.hasLength(secretKey)) {
            this.defaultCredentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        } else if (useDefaultCredentialsChain) {
            this.defaultCredentialsProvider = new DefaultAWSCredentialsProviderChain();
        } else {
            throw new IllegalArgumentException("An accessKey and a secretKey must be provided in case useDefaultCredentialsChain is not set");
        }
    }

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public AWSCredentialsProvider getDefaultCredentialsProvider() {
        return defaultCredentialsProvider;
    }

    public void configureAwsClientBuilder(AwsClientBuilder builder, @Nullable String customRegion, @Nullable String serviceEndpoint, @Nullable AWSCredentialsProvider customCredentialsProvider) {
        String region = StringUtils.hasLength(customRegion) ? customRegion : this.defaultRegion;
        if (StringUtils.hasLength(serviceEndpoint)) {
            builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(serviceEndpoint, region));
        } else {
            builder.setRegion(region);
        }

        AWSCredentialsProvider credentialsProvider = ObjectUtils.isEmpty(customCredentialsProvider) ? this.defaultCredentialsProvider : customCredentialsProvider;
        builder.setCredentials(credentialsProvider);
    }
}
