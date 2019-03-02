package com.jorgenota.utils.springboot.aws.sns.autoconfigure;

import com.amazonaws.services.sns.AmazonSNSAsync;
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
class SnsConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SnsConfiguration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noAwsEnvironmentRegionConfigured() {
            contextRunner
                    .run((context) -> assertThat_creationOfAmazonSns_fails(context));
        }

        private void assertThat_creationOfAmazonSns_fails(AssertableApplicationContext context) {
            assertThat(context)
                    .getFailure()
                    .isInstanceOf(BeanCreationException.class)
                    .hasMessageContaining("Error creating bean with name 'amazonSns'");
        }
    }

    @Nested
    @DisplayName("Testing cases when AmazonSns bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void awsEnvironmentRegionConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSns_isCreated(context, "eu-west-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customRegionConfigured() {
            contextRunner
                    .withPropertyValues("aws.sns.region=us-east-1")
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .run((context) -> assertThat_amazonSns_isCreated(context, "us-east-1", null));
        }

        @Test
        void awsEnvironmentRegionConfigured_customEndpointConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionAwsEnvironmentConfiguration.class)
                    .withPropertyValues("aws.sns.endpoint=https://sns.us-west-2.amazonaws.com")
                    .run((context) -> assertThat_amazonSns_isCreated(context, "eu-west-1", "https://sns.us-west-2.amazonaws.com"));
        }

        private void assertThat_amazonSns_isCreated(AssertableApplicationContext context, String configuredRegion, @Nullable String configuredEndpoint) {
            AbstractObjectAssert<?, AmazonSNSAsync> amazonSesAbstractObjectAssert = assertThat(context)
                    .getBean("amazonSns", AmazonSNSAsync.class)
                    .hasFieldOrPropertyWithValue("signingRegion", configuredRegion);

            if (configuredEndpoint == null) {
                amazonSesAbstractObjectAssert
                        .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI("https://sns." + configuredRegion + ".amazonaws.com"))
                        .hasFieldOrPropertyWithValue("signerRegionOverride", null);
            } else {
                amazonSesAbstractObjectAssert
                        .hasFieldOrPropertyWithValue("endpoint", TestUtils.toURI(configuredEndpoint))
                        .hasFieldOrPropertyWithValue("signerRegionOverride", configuredRegion);
            }
        }
    }
}
