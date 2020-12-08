package com.jorgenota.utils.springboot.aws.autoconfigure;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.jorgenota.utils.springboot.aws.support.AwsEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jorge Alonso
 */
class AwsConfigurationTest {

//    private AnnotationConfigApplicationContext context;
//
//    @BeforeEach
//    public void setupContext() throws Exception {
//        this.context = new AnnotationConfigApplicationContext();
//        this.context.register(AwsConfiguration.class);
//     }
//
//    @AfterEach
//    public void tearDownContext() throws Exception {
//        if (this.context != null) {
//            this.context.close();
//        }
//    }
//
//        @Test
//        public void failWhen_noRegionConfigured_autoDetectIsFalse() {
//        TestPropertyValues.of("aws.context.autoDetectRegion=false").applyTo(this.context);
//
//        assertThatExceptionOfType(BeanCreationException.class)
//                .as("must throw BeanCreationException when refreshing the context")
//                .isThrownBy(() -> {
//                    this.context.refresh();
//                })
//                .withMessageContaining("Error creating bean with name 'awsEnvironment'");
//    }


    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(AwsConfiguration.class));

    @Nested
    @DisplayName("Cases when context refreshing fails...")
    class TestCasesWhenContextRefreshingFails {

        @Test
        void noRegionConfigured_autoDetectIsFalse() {
            contextRunner
                .withPropertyValues("aws.context.autoDetectRegion=false")
                .run((context) -> assertThat_creationOfAwsEnvironment_fails(context));
        }

        @Test
        void noRegionConfigured_autoDetectIsTrue_butNotOnEC2() {
            // autodetect is true by default

            contextRunner
                .run((context) -> assertThat_creationOfAwsEnvironment_fails(context));
        }

        @Test
        void regionConfigured_noCredentialsProviderConfigured() {
            // autodetect is true by default

            contextRunner
                .withPropertyValues("aws.context.staticRegion=eu-west-1", "aws.context.useDefaultCredentialsChain=false")
                .run((context) -> assertThat_creationOfAwsEnvironment_fails(context));
        }

        private void assertThat_creationOfAwsEnvironment_fails(AssertableApplicationContext context) {
            assertThat(context)
                .getFailure()
                .isInstanceOf(BeanCreationException.class)
                .hasMessageContaining("Error creating bean with name 'awsEnvironment'");
        }
    }

    @Nested
    @DisplayName("Cases when AwsEnvironment bean is successfully configured ...")
    class TestCasesWhenContextRefreshingSucceeds {

        @Test
        void regionConfigured() {
            contextRunner
                .withPropertyValues("aws.context.staticRegion=eu-west-1")
                .run((context) -> assertThat_awsEnvironment_isCreated(context, "eu-west-1", DefaultAWSCredentialsProviderChain.class));
        }


        @Test
        void regionConfigured_useDefaultCredentialsChainConfigured() {
            contextRunner
                .withPropertyValues("aws.context.staticRegion=eu-west-1", "aws.context.useDefaultCredentialsChain=true")
                .run((context) -> assertThat_awsEnvironment_isCreated(context, "eu-west-1", DefaultAWSCredentialsProviderChain.class));
        }

        @Test
        void regionConfigured_accessKeyAndSecretKeyConfigured() {
            contextRunner
                .withPropertyValues("aws.context.staticRegion=eu-west-1", "aws.context.accessKey=test", "aws.context.secretKey=testSecret")
                .run((context) -> assertThat_awsEnvironment_isCreated(context, "eu-west-1", AWSStaticCredentialsProvider.class));
        }

        private void assertThat_awsEnvironment_isCreated(AssertableApplicationContext context, String region, Class<? extends AWSCredentialsProvider> credentialsProviderClass) {
            assertThat(context).hasSingleBean(AwsEnvironment.class);
            assertThat(context).getBean("awsEnvironment", AwsEnvironment.class)
                .hasFieldOrPropertyWithValue("defaultRegion", region)
                .matches(env -> env.getDefaultCredentialsProvider().getClass().equals(credentialsProviderClass));
        }
    }

}
