package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.KinesisClientLibNonRetryableException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.ShutdownReason;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.jorgenota.utils.function.RunnableWithExceptions;
import com.jorgenota.utils.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.jorgenota.utils.base.Preconditions.isTrue;
import static com.jorgenota.utils.base.Preconditions.notNull;

@Slf4j
public class KinesisRecordProcessor<T> implements IRecordProcessor {

    private final Retrier retrier;
    private final long checkPointInterval;
    private final KinesisSingleRecordProcessor processor;
    // Next checkpoint time
    private long nextCheckpointTimeInMillis;
    // Kinesis Shard ID
    private String kinesisShardId;

    /**
     * Creates a new KinesisRecordProcessor configuring a processor for every received record and
     * a retrier to retry internal operations such as setting the checkpoint progress.
     *
     * @param processor          record processor that will process every single record
     * @param attemptNumber      attempts number in milliseconds to configure the internal retrier
     * @param sleepTime          sleep time in milliseconds to configure the internal retrier
     * @param checkPointInterval interval between checkpoints in milliseconds
     */
    public KinesisRecordProcessor(KinesisSingleRecordProcessor processor, int attemptNumber, long sleepTime, long checkPointInterval) {
        this.retrier = RetrierBuilder.newBuilder()
            .withWaitStrategy(WaitStrategies.fixedWait(sleepTime))
            .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
            .failIfException(e -> (e instanceof KinesisClientLibNonRetryableException))
            .build();
        isTrue(checkPointInterval > 0, "checkPointInterval must be > 0 but is %d", checkPointInterval);
        this.checkPointInterval = checkPointInterval;
        this.processor = notNull(processor, "processor must not be null");
    }

    @Override
    public void initialize(InitializationInput arg0) {
        log.info("Initializing record processor for shard: " + arg0.getShardId());
        this.kinesisShardId = arg0.getShardId();
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {

        // Process records and perform all exception handling.
        processRecordsInParallel(processRecordsInput.getRecords());

        // Checkpoint once every checkpoint interval.
        if (System.currentTimeMillis() > nextCheckpointTimeInMillis) {
            checkpoint(processRecordsInput.getCheckpointer());
            nextCheckpointTimeInMillis = System.currentTimeMillis() + checkPointInterval;
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        log.info("Shutting down record processor for shard: " + kinesisShardId);
        // Important recipient checkpoint after reaching end of shard, so we can
        // start processing data from child shards.
        if (shutdownInput.getShutdownReason() == ShutdownReason.TERMINATE) {
            checkpoint(shutdownInput.getCheckpointer());
        }
    }

    /**
     * Process records performing retries as needed. Skip "poison pill" records.
     *
     * @param listRecords Data records recipient be processed.
     */
    private void processRecordsInParallel(List<Record> listRecords) {

        if (!ObjectUtils.isEmpty(listRecords)) {
            // List of competable futures to process the list of record in parallel
            List<CompletableFuture<Void>> completableFutureList = listRecords.stream()
                .map(record -> CompletableFuture.runAsync(() -> processor.processRecord(record)))
                .collect(Collectors.toList());

            CompletableFuture<Void> allRecordsCompletableFuture = CompletableFuture.allOf(
                completableFutureList.toArray(new CompletableFuture[completableFutureList.size()]));

            // Wait to finish every record processing
            try {
                allRecordsCompletableFuture.get();
            } catch (InterruptedException e) {
                log.info("Failed to complete all current record processing " + e.getMessage());
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Failed to complete all current record processing " + e.getMessage());
            }

            log.debug("%d records processed from shard %s", listRecords.size(), this.kinesisShardId);
        }
    }

    /**
     * Checkpoint with retries.
     *
     * @param checkpointer
     */
    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        try {
            retrier.run((RunnableWithExceptions) () -> checkpointer.checkpoint());
        } catch (RetryException e) {
            log.error("Couln't save checkpoint for shard " + this.kinesisShardId, e);
        }
    }
}
