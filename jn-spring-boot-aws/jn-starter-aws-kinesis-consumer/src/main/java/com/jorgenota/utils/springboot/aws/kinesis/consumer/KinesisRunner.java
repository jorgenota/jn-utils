package com.jorgenota.utils.springboot.aws.kinesis.consumer;

import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Jorge Alonso
 */
@Slf4j
public class KinesisRunner {
    private final List<Worker> kinesisWorkerList;

    public KinesisRunner(List<Worker> kinesisWorkerList) {
        this.kinesisWorkerList = kinesisWorkerList;
    }

    /**
     * Spring start the worker after get up the context
     */
    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        startWorkers(event.getApplicationContext());
    }

    /**
     * Spring stops the worker after get up the context
     */
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
        shutDownWorkers();
    }

    private void startWorkers(ApplicationContext context) {
        log.info("Starting kinesis workers...");
        kinesisWorkerList.forEach(worker -> new Thread(() -> {
            try {
                worker.run();
            } catch (Throwable t) {
                log.error("Caught throwable while processing data: ", t);
                terminateApplication(context);
            }
        }).start());
    }

    private void terminateApplication(ApplicationContext context) {
        SpringApplication.exit(context);
        System.exit(1);
    }

    private void shutDownWorkers() {
        log.info("Stoping kinesis workers...");
        List<Future<Boolean>> shutDownResults = new ArrayList<>(kinesisWorkerList.size());
        kinesisWorkerList.forEach(worker -> shutDownResults.add(worker.startGracefulShutdown()));
        shutDownResults.forEach(result -> {
            try {
                result.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception while shutting down worker", e);
            }
        });
    }
}
