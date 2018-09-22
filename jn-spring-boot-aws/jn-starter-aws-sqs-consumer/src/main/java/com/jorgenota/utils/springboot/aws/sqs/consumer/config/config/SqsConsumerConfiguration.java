package com.jorgenota.utils.springboot.aws.sqs.consumer.config.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.jorgenota.utils.aws.sqs.listener.SqsMessageHandler;
import com.jorgenota.utils.aws.sqs.listener.SqsMessageListenerContainer;
import com.jorgenota.utils.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({
    SqsConsumerConfigurationProperties.class})
public class SqsConsumerConfiguration {

    @Autowired
    private SqsConsumerConfigurationProperties configuration;


    @ConditionalOnMissingBean
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        AmazonSQSAsyncClientBuilder builder = AmazonSQSAsyncClientBuilder.standard();
        if (!StringUtils.isEmpty(configuration.getRegion())) {
            if (!StringUtils.isEmpty(configuration.getEndpoint())) {
                builder.setEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(configuration.getEndpoint(), configuration.getRegion()));
            } else {
                builder.setRegion(configuration.getRegion());
            }
        } else if (!StringUtils.isEmpty(configuration.getEndpoint())) {
            Preconditions.empty(configuration.getEndpoint(), "Endpoint should be empty if region is empty");
        }
        return builder.build();
    }


    @ConditionalOnMissingBean
    @Bean
    public SqsMessageListenerContainer simpleMessageListenerContainer(AmazonSQSAsync amazonSqs, List<SqsMessageHandler> messageHandlers) {
        notNull(amazonSqs, "amazonSqs must not be null");

        SqsMessageListenerContainer simpleMessageListenerContainer = new SqsMessageListenerContainer(amazonSqs, messageHandlers);
        simpleMessageListenerContainer.setAutoStartup(true);
        simpleMessageListenerContainer.setMaxNumberOfMessages(configuration.getMaxNumberOfMessages());
        simpleMessageListenerContainer.setBackOffTime(configuration.getBackOffTime());

        return simpleMessageListenerContainer;
    }
}
