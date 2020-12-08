package com.jorgenota.utils.springboot.aws.integrationtest;

import cloud.localstack.CommonUtils;
import cloud.localstack.Localstack;
import cloud.localstack.awssdkv1.TestUtils;
import cloud.localstack.docker.LocalstackDockerExtension;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import com.amazonaws.services.kinesis.AmazonKinesisAsync;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jorge Alonso
 */
@ExtendWith(LocalstackDockerExtension.class)
@LocalstackDockerProperties(imageTag = "0.12.2", services = {"s3", "sqs", "sns", "kinesis"})
@SpringBootTest(classes = IntegrationTestConfiguration.class)
@TestPropertySource("classpath:integrationtest.properties")
class MainIT {

//    static {
//        System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true");
//    }

    @Nested
    @DisplayName("Kinesis Test cases... ")
    class KinesisIntegrationTestCases {
        @Autowired
        AmazonKinesisAsync amazonKinesis;


        @Test
        void testKinesis() {
            assertThat(amazonKinesis).isNotNull();
        }
    }

    @Nested
    @DisplayName("S3 Test cases... ")
    class S3IntegrationTestCases {

        @Autowired
        AmazonS3 amazonS3;

        @Test
        public void testS3() {
            assertThat(amazonS3).isNotNull();

//            assertThat(amazonS3.listBuckets()).isEmpty();
//            AmazonS3 client = TestUtils.getClientS3();
//            client.createBucket("bucket2");
//
//            String bucketName = "bucket1";
//            String key = "key1";
//            String data = "{}";
//            amazonS3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
//            amazonS3.createBucket(bucketName);
//            amazonS3.putObject(bucketName, key, data);
//            assertThat(amazonS3.listBuckets()).hasSize(1);
//
//            amazonS3.deleteBucket(bucketName);
//            assertThat(amazonS3.listBuckets()).isEmpty();
        }
    }

    @Nested
    @DisplayName("SES Test cases... ")
    class SesIntegrationTestCases {
        @Autowired
        AmazonSimpleEmailServiceAsync amazonSes;


        @Test
        void testSes() {
            assertThat(amazonSes).isNotNull();
        }
    }

    @Nested
    @DisplayName("SNS Test cases... ")
    class SnsIntegrationTestCases {
        @Autowired
        AmazonSNSAsync amazonSns;


        @Test
        void testSns() {
            assertThat(amazonSns).isNotNull();
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("SQS Test cases... ")
    class SqsIntegrationTestCases {
        @Autowired
        AmazonSQSAsync amazonSqs;

        @BeforeAll
        public void setup() {
            Map<String, String> attributeMap = new HashMap<>();
            attributeMap.put("DelaySeconds", "0");
            attributeMap.put("MaximumMessageSize", "262144");
            attributeMap.put("MessageRetentionPeriod", "1209600");
            attributeMap.put("ReceiveMessageWaitTimeSeconds", "20");
            attributeMap.put("VisibilityTimeout", "30");

            AmazonSQS client = TestUtils.getClientSQS();
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue2").withAttributes(attributeMap);
            CreateQueueResult result = client.createQueue(createQueueRequest);
            assertThat(result).isNotNull();

            /* Disable SSL certificate checks for local testing */
            if (Localstack.useSSL()) {
                CommonUtils.disableSslCertChecking();
            }
        }

        @Test
        void testSqs() {
            assertThat(amazonSqs).isNotNull();
        }
    }
}
