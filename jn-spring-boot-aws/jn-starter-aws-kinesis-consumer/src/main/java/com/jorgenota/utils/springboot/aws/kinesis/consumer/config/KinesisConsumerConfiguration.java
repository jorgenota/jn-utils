package com.jorgenota.utils.springboot.aws.kinesis.consumer.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.InitialPositionInStream;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.jorgenota.utils.aws.kinesis.KinesisRecordProcessorFactory;
import com.jorgenota.utils.aws.kinesis.KinesisSingleRecordProcessor;
import com.jorgenota.utils.springboot.aws.kinesis.consumer.KinesisRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * @author Jorge Alonso
 */
@Configuration
@EnableConfigurationProperties({
    KinesisConsumerConfigurationProperties.class})
public class KinesisConsumerConfiguration {

    @Autowired
    private KinesisConsumerConfigurationProperties configuration;

    @Value(value = "${spring.application.name}")
    private String applicationName;

    @ConditionalOnMissingBean
    @Bean
    public KinesisRecordProcessorFactory recordProcessorFactory(KinesisSingleRecordProcessor processor) {
        notNull(processor, "processor must not be null");
        return new KinesisRecordProcessorFactory(processor, configuration.getRetryAttempts(), configuration.getRetrySleepTime(), configuration.getCheckPointInterval());
    }

    @ConditionalOnMissingBean
    @Bean
    public List<Worker> kinesisWorkerList(KinesisRecordProcessorFactory recordProcessorFactory) throws UnknownHostException {
        List<Worker> workers = new ArrayList<>(configuration.getStreams().size());
        for (KinesisConsumerConfigurationProperties.StreamConfig streamConfig : configuration.getStreams()) {
            KinesisClientLibConfiguration clientLibConfiguration = clientLibConfiguration(streamConfig);
            workers.add(new Worker.Builder().recordProcessorFactory(recordProcessorFactory)
                .config(clientLibConfiguration).build());
        }
        return workers;
    }

    private KinesisClientLibConfiguration clientLibConfiguration(KinesisConsumerConfigurationProperties.StreamConfig streamConfig) throws UnknownHostException {
        String workerId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + UUID.randomUUID();
        KinesisClientLibConfiguration clientLibConfiguration = new KinesisClientLibConfiguration(
            applicationName, streamConfig.getName(), new DefaultAWSCredentialsProviderChain(), workerId)
            .withMaxRecords(streamConfig.getMaxRecords())
            .withInitialPositionInStream(InitialPositionInStream.TRIM_HORIZON);

        if (!StringUtils.isEmpty(configuration.getRegion())) {
            clientLibConfiguration.withRegionName(configuration.getRegion());
        }
        if (!StringUtils.isEmpty(configuration.getEndpoint())) {
            clientLibConfiguration.withKinesisEndpoint(configuration.getEndpoint());
        }

        return clientLibConfiguration;
    }

    @ConditionalOnMissingBean
    @Bean
    public KinesisRunner kinesisRunner(List<Worker> kinesisWorkerList) {
        return new KinesisRunner(kinesisWorkerList);
    }
}
