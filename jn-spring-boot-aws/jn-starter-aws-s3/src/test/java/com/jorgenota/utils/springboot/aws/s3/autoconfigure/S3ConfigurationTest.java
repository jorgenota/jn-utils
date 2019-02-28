package com.jorgenota.utils.springboot.aws.s3.autoconfigure;

import com.amazonaws.services.s3.AmazonS3;
import com.jorgenota.utils.springboot.aws.testsupport.IrelandRegionConfiguration;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

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
        void noDefaultRegionConfigured() {
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
        void defaultRegionConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionConfiguration.class)
                    .run((context) -> assertThat_amazonS3_isCreated(context, "eu-west-1", null));
        }

        @Test
        void defaultRegionConfigured_customRegionConfigured() {
            contextRunner
                    .withPropertyValues("aws.s3.region=us-east-1")
                    .withUserConfiguration(IrelandRegionConfiguration.class)
                    .run((context) -> assertThat_amazonS3_isCreated(context, "us-east-1", null));
        }

        @Test
        void defaultRegionConfigured_customEndpointConfigured() {
            contextRunner
                    .withUserConfiguration(IrelandRegionConfiguration.class)
                    .withPropertyValues("aws.s3.endpoint=https://s3.us-east-1.amazonaws.com")
                    .run((context) -> assertThat_amazonS3_isCreated(context, "us-east-1", "https://s3.us-east-1.amazonaws.com"));
        }

        private void assertThat_amazonS3_isCreated(AssertableApplicationContext context, String expectedRegion, @Nullable String expectedEndpoint) {
            assertThat(context).hasSingleBean(AmazonS3.class);
            AbstractObjectAssert<?, AmazonS3> amazonS3AbstractObjectAssert = assertThat(context).getBean("amazonS3", AmazonS3.class).hasFieldOrPropertyWithValue("regionName", expectedRegion);
            if (expectedEndpoint != null) {
                URI uriEndpoint;
                try {
                    uriEndpoint = new URI(expectedEndpoint);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Endpoint value is an invalid URI");
                }
                amazonS3AbstractObjectAssert.hasFieldOrPropertyWithValue("endpoint", uriEndpoint);
            }
        }
    }
}
