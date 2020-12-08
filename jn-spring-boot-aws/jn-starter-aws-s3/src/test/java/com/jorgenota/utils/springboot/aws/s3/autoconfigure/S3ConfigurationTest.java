package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import com.amazonaws.services.s3.AmazonS3;
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
class S3ConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(S3Configuration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noAwsEnvironmentRegionConfigured() {
            contextRunner
                    .run((context) -> assertThat_creationOfAmazonS3_fails(context));
        }

        private void assertThat_creationOfAmazonS3_fails(AssertableApplicationContext context) {
            assertThat(context)
                    .getFailure()
                    .isInstanceOf(BeanCreationException.class)
                    .hasMessageContaining("Error creating bean with name 'amazonS3'");
        }
    }

    @Nested
    @DisplayName("Testing cases when AmazonS3 bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void awsEnvironmentRegionConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonS3_isCreated(context, "eu-west-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customRegionConfigured() {
            contextRunner
                    .withPropertyValues("aws.s3.region=eu-west-2")
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonS3_isCreated(context, "eu-west-2", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customEndpointConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.s3.endpoint=https://s3.us-west-2.amazonaws.com")
                .run((context) -> assertThat_amazonS3_isCreated(context, "eu-west-1", "https://s3.us-west-2.amazonaws.com"));
        }

        @Test
        void awsEnvironmentRegionConfigured_customMaxConnectionsConfigured() {
            contextRunner
                .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                .withPropertyValues("aws.s3.config.maxConnections=11")
                .run((context) -> assertThat(context)
                    .getBean("amazonS3", AmazonS3.class)
                    .hasFieldOrPropertyWithValue("clientConfiguration.maxConnections", 11));
        }

        private void assertThat_amazonS3_isCreated(AssertableApplicationContext context, String configuredRegion, @Nullable String configuredEndpoint) {
            AbstractObjectAssert<?, AmazonS3> amazonSesAbstractObjectAssert = assertThat(context)
                .getBean("amazonS3", AmazonS3.class)
                .hasFieldOrPropertyWithValue("signingRegion", configuredRegion);

            if (configuredEndpoint == null) {
                amazonSesAbstractObjectAssert
                    .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI("https://s3." + configuredRegion + ".amazonaws.com"))
                    .hasFieldOrPropertyWithValue("signerRegionOverride", null);
            } else {
                amazonSesAbstractObjectAssert
                        .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI(configuredEndpoint))
                        .hasFieldOrPropertyWithValue("signerRegionOverride", configuredRegion);
            }
        }

    }
}
