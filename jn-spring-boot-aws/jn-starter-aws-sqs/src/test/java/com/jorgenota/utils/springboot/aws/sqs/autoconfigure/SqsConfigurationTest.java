package com.jorgenota.utils.springboot.aws.sqs.autoconfigure;

import com.amazonaws.services.sqs.AmazonSQSAsync;
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
class SqsConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SqsConfiguration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noAwsEnvironmentRegionConfigured() {
            contextRunner
                    .run((context) -> assertThat_creationOfAmazonSqs_fails(context));
        }

        private void assertThat_creationOfAmazonSqs_fails(AssertableApplicationContext context) {
            assertThat(context)
                    .getFailure()
                    .isInstanceOf(BeanCreationException.class)
                    .hasMessageContaining("Error creating bean with name 'amazonSqs'");
        }
    }

    @Nested
    @DisplayName("Testing cases when AmazonSqs bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void awsEnvironmentRegionConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSqs_isCreated(context, "eu-west-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customRegionConfigured() {
            contextRunner
                    .withPropertyValues("aws.sqs.region=us-east-1")
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSqs_isCreated(context, "us-east-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customEndpointConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.sqs.endpoint=https://sqs.us-west-2.amazonaws.com")
                .run((context) -> assertThat_amazonSqs_isCreated(context, "eu-west-1", "https://sqs.us-west-2.amazonaws.com"));
        }

        @Test
        void awsEnvironmentRegionConfigured_customMaxConnectionsConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.sqs.config.maxConnections=11")
                .run((context) -> assertThat(context)
                    .getBean("amazonSqs", AmazonSQSAsync.class)
                    .hasFieldOrPropertyWithValue("clientConfiguration.maxConnections", 11));
        }

        private void assertThat_amazonSqs_isCreated(AssertableApplicationContext context, String configuredRegion, @Nullable String configuredEndpoint) {
            AbstractObjectAssert<?, AmazonSQSAsync> amazonSesAbstractObjectAssert = assertThat(context)
                .getBean("amazonSqs", AmazonSQSAsync.class)
                .hasFieldOrPropertyWithValue("signingRegion", configuredRegion);

            if (configuredEndpoint == null) {
                amazonSesAbstractObjectAssert
                    .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI("https://sqs." + configuredRegion + ".amazonaws.com"))
                    .hasFieldOrPropertyWithValue("signerRegionOverride", null);
            } else {
                amazonSesAbstractObjectAssert
                        .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI(configuredEndpoint))
                        .hasFieldOrPropertyWithValue("signerRegionOverride", configuredRegion);
            }
        }
    }
}
