package com.jorgenota.utils.springboot.aws.support;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.ProxyAuthenticationMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.jorgenota.utils.springboot.aws.autoconfigure.AwsConfigurationProperties;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Jorge Alonso
 */
class AwsEnvironmentTest {


    @Nested
    @DisplayName("Cases when AwsEnvironment instantiation fails...")
    class TestCasesWhenAwsEnvironmentInstantiationFails {

        @Test
        void autoDetectIsTrue_noRegionConfigured_notOnEC2() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();

            assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(configuration, "Region detection is only possible if the application is running on an EC2 instance");
        }

        @Test
        void autoDetectIsFalse_noRegionConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setAutoDetectRegion(false);

            assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(configuration, "A staticRegion must be provided in case it is not being autodetected");
        }

        @Test
        void failWhen_regionConfigured_noCredentialsProviderConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            configuration.setUseDefaultCredentialsChain(false);

            assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(configuration, "An accessKey and a secretKey must be provided in case useDefaultCredentialsChain is not set");
        }

        @Test
        void failWhen_regionConfigured_noSecretKeyConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            configuration.setUseDefaultCredentialsChain(false);
            configuration.setAccessKey("test");

            assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(configuration, "An accessKey and a secretKey must be provided in case useDefaultCredentialsChain is not set");
        }

        @Test
        void failWhen_regionConfigured_noAccessKeyConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            configuration.setUseDefaultCredentialsChain(false);
            configuration.setSecretKey("test");

            assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(configuration, "An accessKey and a secretKey must be provided in case useDefaultCredentialsChain is not set");
        }

        private void assertThat_exceptionIsThrown_whenInstantiating_AwsEnvironment(AwsConfigurationProperties configuration, String message) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new AwsEnvironment(configuration))
                .withMessageContaining(message);
        }
    }

    @Nested
    @DisplayName("Cases when AwsEnvironment instantiation succeeds...")
    class TestCasesWhenAwsEnvironmentInstantiationSucceeds {

        @Test
        void awsEnvironmentCreated_regionConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");

            AwsEnvironment awsEnvironment = new AwsEnvironment(configuration);
            assertThat(awsEnvironment.getDefaultRegion()).isEqualTo("eu-west-1");
        }

        @Test
        void awsEnvironmentCreated_regionConfigured_DefaultCredentialsProviderChainConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            configuration.setUseDefaultCredentialsChain(true);

            AwsEnvironment awsEnvironment = new AwsEnvironment(configuration);
            assertThat(awsEnvironment.getDefaultCredentialsProvider()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void awsEnvironmentCreated_regionConfigured_accessKeyAndSecretKeyConfigured() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            configuration.setUseDefaultCredentialsChain(false);
            configuration.setAccessKey("test");
            configuration.setSecretKey("testSecret");

            AwsEnvironment awsEnvironment = new AwsEnvironment(configuration);
            assertThat(awsEnvironment.getDefaultCredentialsProvider()).isInstanceOf(AWSStaticCredentialsProvider.class);
            assertThat(awsEnvironment.getDefaultCredentialsProvider().getCredentials().getAWSAccessKeyId()).isEqualTo("test");
            assertThat(awsEnvironment.getDefaultCredentialsProvider().getCredentials().getAWSSecretKey()).isEqualTo("testSecret");
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Cases for utility method configureAwsClientBuilder()")
    class TestCases_ConfigureAwsClientBuilder {
        AwsEnvironment awsEnvironment;
        AwsClientBuilder builder;

        @BeforeAll
        void initAwsEnvironment() {
            AwsConfigurationProperties configuration = new AwsConfigurationProperties();
            configuration.setStaticRegion("eu-west-1");
            this.awsEnvironment = new AwsEnvironment(configuration);
        }

        @BeforeEach
        void initAwsClientBuilder() {
            this.builder = new AwsClientBuilder<AwsClientBuilder, Object>(null) {
                @Override
                public Object build() {
                    return new Object();
                }
            };
        }

        @Test
        void testNoCustomValues() {
            awsEnvironment.configureAwsClientBuilder(builder, null, null);
            assertThat(builder.getRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
            assertThat(builder.getClientConfiguration()).isNull();
        }

        @Test
        void testCustomRegion() {
            AWSClientProperties clientProperties = new AWSClientProperties();
            clientProperties.setRegion("us-east-1");

            awsEnvironment.configureAwsClientBuilder(builder, clientProperties, null);
            assertThat(builder.getRegion()).isEqualTo("us-east-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomEndpoint() {
            AWSClientProperties clientProperties = new AWSClientProperties();
            clientProperties.setEndpoint("https://s3.us-east-1.amazonaws.com");

            awsEnvironment.configureAwsClientBuilder(builder, clientProperties, null);
            assertThat(builder.getRegion()).isNull();
            assertThat(builder.getEndpoint().getServiceEndpoint()).isEqualTo("https://s3.us-east-1.amazonaws.com");
            assertThat(builder.getEndpoint().getSigningRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomRegionAndEndpoint() {
            AWSClientProperties clientProperties = new AWSClientProperties();
            clientProperties.setRegion("eu-west-2");
            clientProperties.setEndpoint("https://s3.us-east-1.amazonaws.com");

            awsEnvironment.configureAwsClientBuilder(builder, clientProperties, null);
            assertThat(builder.getRegion()).isNull();
            assertThat(builder.getEndpoint().getServiceEndpoint()).isEqualTo("https://s3.us-east-1.amazonaws.com");
            assertThat(builder.getEndpoint().getSigningRegion()).isEqualTo("eu-west-2");
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomCredentialsProvider() {
            AWSStaticCredentialsProvider customCredentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "testSecret"));

            awsEnvironment.configureAwsClientBuilder(builder, null, customCredentialsProvider);
            assertThat(builder.getRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(AWSStaticCredentialsProvider.class);
            assertThat(builder.getCredentials().getCredentials().getAWSAccessKeyId()).isEqualTo("test");
            assertThat(builder.getCredentials().getCredentials().getAWSSecretKey()).isEqualTo("testSecret");
        }

        @Test
        void testEmptyClientConfig() {
            AWSClientProperties clientProperties = new AWSClientProperties();
            clientProperties.setConfig(new AWSClientProperties.Config());

            awsEnvironment.configureAwsClientBuilder(builder, clientProperties, null);
            assertThat(builder.getClientConfiguration()).usingRecursiveComparison().isEqualTo(new ClientConfiguration());
        }


        @Test
        void testCustomClientConfig() {
            AWSClientProperties.Config config = new AWSClientProperties.Config();
            config.setCacheResponseMetadata(false);
            config.setClientExecutionTimeout(111);
            config.setConnectionMaxIdleMillis(222L);
            config.setConnectionTimeout(333);
            config.setConnectionTTL(444L);
            config.setDisableSocketProxy(true);
            config.setMaxConnections(7);
            config.setMaxConsecutiveRetriesBeforeThrottling(9);
            config.setMaxErrorRetry(2);
            config.setNonProxyHosts("www.google.com");
            config.setPreemptiveBasicProxyAuth(true);
            config.setProtocol(Protocol.HTTPS);
            config.setProxyAuthenticationMethods(List.of(ProxyAuthenticationMethod.BASIC));
            config.setProxyDomain("domain.com");
            config.setProxyHost("host");
            config.setProxyPassword("pass");
            config.setProxyPort(25);
            config.setProxyProtocol(Protocol.HTTP);
            config.setProxyUsername("user");
            config.setRequestTimeout(999);
            config.setResponseMetadataCacheSize(888);
            config.setSignerOverride("S3SignerType");
            config.setUseGzip(true);
            config.setUseThrottleRetries(false);

            AWSClientProperties clientProperties = new AWSClientProperties();
            clientProperties.setConfig(config);

            awsEnvironment.configureAwsClientBuilder(builder, clientProperties, null);

            ClientConfiguration expectedConfig = new ClientConfiguration();
            expectedConfig.setMaxConnections(11);
            expectedConfig.setCacheResponseMetadata(false);
            expectedConfig.setClientExecutionTimeout(111);
            expectedConfig.setConnectionMaxIdleMillis(222L);
            expectedConfig.setConnectionTimeout(333);
            expectedConfig.setConnectionTTL(444L);
            expectedConfig.setDisableSocketProxy(true);
            expectedConfig.setMaxConnections(7);
            expectedConfig.setMaxConsecutiveRetriesBeforeThrottling(9);
            expectedConfig.setMaxErrorRetry(2);
            expectedConfig.setNonProxyHosts("www.google.com");
            expectedConfig.setPreemptiveBasicProxyAuth(true);
            expectedConfig.setProtocol(Protocol.HTTPS);
            expectedConfig.setProxyAuthenticationMethods(List.of(ProxyAuthenticationMethod.BASIC));
            expectedConfig.setProxyDomain("domain.com");
            expectedConfig.setProxyHost("host");
            expectedConfig.setProxyPassword("pass");
            expectedConfig.setProxyPort(25);
            expectedConfig.setProxyProtocol(Protocol.HTTP);
            expectedConfig.setProxyUsername("user");
            expectedConfig.setRequestTimeout(999);
            expectedConfig.setResponseMetadataCacheSize(888);
            expectedConfig.setSignerOverride("S3SignerType");
            expectedConfig.setUseGzip(true);
            expectedConfig.setUseThrottleRetries(false);

            assertThat(builder.getClientConfiguration()).usingRecursiveComparison().isEqualTo(expectedConfig);
        }
    }
}
