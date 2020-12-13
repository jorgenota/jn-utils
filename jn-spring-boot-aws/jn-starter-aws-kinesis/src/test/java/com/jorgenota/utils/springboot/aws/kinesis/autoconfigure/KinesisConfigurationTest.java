package com.jorgenota.utils.springboot.aws.kinesis.autoconfigure;

import com.amazonaws.Protocol;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.jorgenota.utils.springboot.aws.testsupport.IrelandRegionAwsEnvironmentConfiguration;
import com.jorgenota.utils.springboot.aws.testsupport.TestUtils;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.lang.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jorge Alonso
 */
class KinesisConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(KinesisConfiguration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noAwsEnvironmentRegionConfigured() {
            contextRunner
                .run((context) -> assertThat_creationOfAmazonKinesis_fails(context));
        }

        private void assertThat_creationOfAmazonKinesis_fails(AssertableApplicationContext context) {
            assertThat(context)
                .getFailure()
                .isInstanceOf(BeanCreationException.class)
                .hasMessageContaining("Error creating bean with name 'amazonKinesis'");
        }
    }

    @Nested
    @DisplayName("Testing cases when AmazonKinesis bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void awsEnvironmentRegionConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .run((context) -> assertThat_AmazonKinesis_isCreated(context, "eu-west-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customRegionConfigured() {
            contextRunner
                .withPropertyValues("aws.kinesis.region=us-east-1")
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .run((context) -> assertThat_AmazonKinesis_isCreated(context, "us-east-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customEndpointConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.kinesis.endpoint=https://kinesis.us-west-2.amazonaws.com")
                .run((context) -> assertThat_AmazonKinesis_isCreated(context, "eu-west-1", "https://kinesis.us-west-2.amazonaws.com"));
        }

        @Test
        void awsEnvironmentRegionConfigured_customMaxConnectionsAndProtocolConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.kinesis.config.maxConnections=11", "aws.kinesis.config.protocol=HTTPS")
                .run((context) -> assertThat(context)
                    .getBean("amazonKinesis", AmazonKinesisAsync.class)
                    .hasFieldOrPropertyWithValue("clientConfiguration.maxConnections", 11)
                    .hasFieldOrPropertyWithValue("clientConfiguration.protocol", Protocol.HTTPS));
        }

        private void assertThat_AmazonKinesis_isCreated(AssertableApplicationContext context, String configuredRegion, @Nullable String configuredEndpoint) {
            AbstractObjectAssert<?, AmazonKinesisAsync> amazonSesAbstractObjectAssert = assertThat(context)
                .getBean("amazonKinesis", AmazonKinesisAsync.class)
                .hasFieldOrPropertyWithValue("signingRegion", configuredRegion);

            if (configuredEndpoint == null) {
                amazonSesAbstractObjectAssert
                    .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI("https://kinesis." + configuredRegion + ".amazonaws.com"))
                    .hasFieldOrPropertyWithValue("signerRegionOverride", null);
            } else {
                amazonSesAbstractObjectAssert
                    .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI(configuredEndpoint))
                    .hasFieldOrPropertyWithValue("signerRegionOverride", configuredRegion);
            }
        }
    }
}
