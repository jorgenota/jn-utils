package com.jorgenota.utils.springboot.aws.support;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.ProxyAuthenticationMethod;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Client configuration options such as proxy settings, user agent string, max retry
 * attempts, etc. See {@link ClientConfiguration}.
 *
 * @author Jorge Alonso
 * @
 */
@Data
public class AWSClientProperties {
    @Nullable
    private String region;
    @Nullable
    private String endpoint;

    @Nullable
    private Config config;


    @Data
    public static class Config {
        @Nullable
        private Boolean cacheResponseMetadata;
        @Nullable
        private Integer clientExecutionTimeout;
        @Nullable
        private Long connectionMaxIdleMillis;
        @Nullable
        private Integer connectionTimeout;
        @Nullable
        private Long connectionTTL;
        @Nullable
        private Boolean disableSocketProxy;
        @Nullable
        private Integer maxConnections;
        @Nullable
        private Integer maxConsecutiveRetriesBeforeThrottling;
        @Nullable
        private Integer maxErrorRetry;
        @Nullable
        private String nonProxyHosts;
        @Nullable
        private Boolean preemptiveBasicProxyAuth;
        @Nullable
        private Protocol protocol;
        @Nullable
        private List<ProxyAuthenticationMethod> proxyAuthenticationMethods;
        @Nullable
        private String proxyDomain;
        @Nullable
        private String proxyHost;
        @Nullable
        private String proxyPassword;
        @Nullable
        private Integer proxyPort;
        @Nullable
        private Protocol proxyProtocol;
        @Nullable
        private String proxyUsername;
        @Nullable
        private Integer requestTimeout;
        @Nullable
        private Integer responseMetadataCacheSize;
        @Nullable
        private String signerOverride;
        @Nullable
        private Boolean useGzip;
        @Nullable
        private Boolean useThrottleRetries;
    }
}
