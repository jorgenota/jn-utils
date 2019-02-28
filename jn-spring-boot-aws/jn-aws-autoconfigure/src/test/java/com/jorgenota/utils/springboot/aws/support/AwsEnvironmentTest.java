package com.jorgenota.utils.springboot.aws.support;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.jorgenota.utils.springboot.aws.autoconfigure.AwsConfigurationProperties;
import org.junit.jupiter.api.*;

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
            awsEnvironment.configureAwsClientBuilder(builder, null, null, null);
            assertThat(builder.getRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomRegion() {
            awsEnvironment.configureAwsClientBuilder(builder, "us-east-1", null, null);
            assertThat(builder.getRegion()).isEqualTo("us-east-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomEndpoint() {
            awsEnvironment.configureAwsClientBuilder(builder, null, "https://s3.us-east-1.amazonaws.com", null);
            assertThat(builder.getRegion()).isNull();
            assertThat(builder.getEndpoint().getServiceEndpoint()).isEqualTo("https://s3.us-east-1.amazonaws.com");
            assertThat(builder.getEndpoint().getSigningRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void testCustomRegionAndEndpoint() {
            awsEnvironment.configureAwsClientBuilder(builder, "eu-west-2", "https://s3.us-east-1.amazonaws.com", null);
            assertThat(builder.getRegion()).isNull();
            assertThat(builder.getEndpoint().getServiceEndpoint()).isEqualTo("https://s3.us-east-1.amazonaws.com");
            assertThat(builder.getEndpoint().getSigningRegion()).isEqualTo("eu-west-2");
            assertThat(builder.getCredentials()).isInstanceOf(DefaultAWSCredentialsProviderChain.class);
        }

        @Test
        void test3() {
            AWSStaticCredentialsProvider customCredentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("test", "testSecret"));
            awsEnvironment.configureAwsClientBuilder(builder, null, null, customCredentialsProvider);
            assertThat(builder.getRegion()).isEqualTo("eu-west-1");
            assertThat(builder.getEndpoint()).isNull();
            assertThat(builder.getCredentials()).isInstanceOf(AWSStaticCredentialsProvider.class);
            assertThat(builder.getCredentials().getCredentials().getAWSAccessKeyId()).isEqualTo("test");
            assertThat(builder.getCredentials().getCredentials().getAWSSecretKey()).isEqualTo("testSecret");
        }

    }
}
