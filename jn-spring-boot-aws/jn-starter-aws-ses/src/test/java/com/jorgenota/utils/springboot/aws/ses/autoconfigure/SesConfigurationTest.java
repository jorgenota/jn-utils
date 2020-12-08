package com.jorgenota.utils.springboot.aws.ses.autoconfigure;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
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
class SesConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SesConfiguration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noAwsEnvironmentRegionConfigured() {
            contextRunner
                    .run((context) -> assertThat_creationOfAmazonSes_fails(context));
        }

        private void assertThat_creationOfAmazonSes_fails(AssertableApplicationContext context) {
            assertThat(context)
                    .getFailure()
                    .isInstanceOf(BeanCreationException.class)
                    .hasMessageContaining("Error creating bean with name 'amazonSes'");
        }
    }

    @Nested
    @DisplayName("Testing cases when AmazonSes bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void awsEnvironmentRegionConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSes_isCreated(context, "eu-west-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customRegionConfigured() {
            contextRunner
                    .withPropertyValues("aws.ses.region=us-east-1")
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSes_isCreated(context, "us-east-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customEndpointConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.ses.endpoint=email-smtp.us-west-2.amazonaws.com")
                .run((context) -> assertThat_amazonSes_isCreated(context, "eu-west-1", "https://email-smtp.us-west-2.amazonaws.com"));
        }

        @Test
        void awsEnvironmentRegionConfigured_customMaxConnectionsConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.ses.config.maxConnections=11")
                .run((context) -> assertThat(context)
                    .getBean("amazonSes", AmazonSimpleEmailServiceAsync.class)
                    .hasFieldOrPropertyWithValue("clientConfiguration.maxConnections", 11));
        }

        private void assertThat_amazonSes_isCreated(AssertableApplicationContext context, String configuredRegion, @Nullable String configuredEndpoint) {
            AbstractObjectAssert<?, AmazonSimpleEmailServiceAsync> amazonSesAbstractObjectAssert = assertThat(context)
                .getBean("amazonSes", AmazonSimpleEmailServiceAsync.class)
                .hasFieldOrPropertyWithValue("signingRegion", configuredRegion);

            if (configuredEndpoint == null) {
                amazonSesAbstractObjectAssert
                    .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI("https://email." + configuredRegion + ".amazonaws.com"))
                    .hasFieldOrPropertyWithValue("signerRegionOverride", null);
            } else {
                amazonSesAbstractObjectAssert
                        .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI(configuredEndpoint))
                        .hasFieldOrPropertyWithValue("signerRegionOverride", configuredRegion);
            }
        }
    }
}
