package com.jorgenota.utils.springboot.aws.support;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
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

    public void configureAwsClientBuilder(AwsClientBuilder builder, @Nullable AWSClientProperties clientProperties, @Nullable AWSCredentialsProvider customCredentialsProvider) {

        if (clientProperties == null) {
            builder.setRegion(this.defaultRegion);
        } else {
            String region = StringUtils.hasLength(clientProperties.getRegion()) ? clientProperties.getRegion() : this.defaultRegion;
            if (StringUtils.hasLength(clientProperties.getEndpoint())) {
                builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(clientProperties.getEndpoint(), region));
            } else {
                builder.setRegion(region);
            }

            if (clientProperties.getConfig() != null) {
                AWSClientProperties.Config config = clientProperties.getConfig();
                ClientConfiguration clientConfig = new ClientConfiguration();

                if (config.getCacheResponseMetadata() != null)
                    clientConfig.setCacheResponseMetadata(config.getCacheResponseMetadata());
                if (config.getClientExecutionTimeout() != null)
                    clientConfig.setClientExecutionTimeout(config.getClientExecutionTimeout());
                if (config.getConnectionMaxIdleMillis() != null)
                    clientConfig.setConnectionMaxIdleMillis(config.getConnectionMaxIdleMillis());
                if (config.getConnectionTimeout() != null)
                    clientConfig.setConnectionTimeout(config.getConnectionTimeout());
                if (config.getConnectionTTL() != null) clientConfig.setConnectionTTL(config.getConnectionTTL());
                if (config.getDisableSocketProxy() != null)
                    clientConfig.setDisableSocketProxy(config.getDisableSocketProxy());
                if (config.getMaxConnections() != null) clientConfig.setMaxConnections(config.getMaxConnections());
                if (config.getMaxConsecutiveRetriesBeforeThrottling() != null)
                    clientConfig.setMaxConsecutiveRetriesBeforeThrottling(config.getMaxConsecutiveRetriesBeforeThrottling());
                if (config.getMaxErrorRetry() != null) clientConfig.setMaxErrorRetry(config.getMaxErrorRetry());
                if (config.getNonProxyHosts() != null) clientConfig.setNonProxyHosts(config.getNonProxyHosts());
                if (config.getPreemptiveBasicProxyAuth() != null)
                    clientConfig.setPreemptiveBasicProxyAuth(config.getPreemptiveBasicProxyAuth());
                if (config.getProtocol() != null) clientConfig.setProtocol(config.getProtocol());
                if (config.getProxyAuthenticationMethods() != null)
                    clientConfig.setProxyAuthenticationMethods(config.getProxyAuthenticationMethods());
                if (config.getProxyDomain() != null) clientConfig.setProxyDomain(config.getProxyDomain());
                if (config.getProxyHost() != null) clientConfig.setProxyHost(config.getProxyHost());
                if (config.getProxyPassword() != null) clientConfig.setProxyPassword(config.getProxyPassword());
                if (config.getProxyPort() != null) clientConfig.setProxyPort(config.getProxyPort());
                if (config.getProxyProtocol() != null) clientConfig.setProxyProtocol(config.getProxyProtocol());
                if (config.getProxyUsername() != null) clientConfig.setProxyUsername(config.getProxyUsername());
                if (config.getRequestTimeout() != null) clientConfig.setRequestTimeout(config.getRequestTimeout());
                if (config.getResponseMetadataCacheSize() != null)
                    clientConfig.setResponseMetadataCacheSize(config.getResponseMetadataCacheSize());
                if (config.getSignerOverride() != null) clientConfig.setSignerOverride(config.getSignerOverride());
                if (config.getUseGzip() != null) clientConfig.setUseGzip(config.getUseGzip());
                if (config.getUseThrottleRetries() != null)
                    clientConfig.setUseThrottleRetries(config.getUseThrottleRetries());

                builder.setClientConfiguration(clientConfig);
            }

        }

        // Configure AWSCredentialsProvider
        AWSCredentialsProvider credentialsProvider = ObjectUtils.isEmpty(customCredentialsProvider) ? this.defaultCredentialsProvider : customCredentialsProvider;
        builder.setCredentials(credentialsProvider);
    }
}
