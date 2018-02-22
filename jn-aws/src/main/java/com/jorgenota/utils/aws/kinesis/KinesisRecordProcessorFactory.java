package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class KinesisRecordProcessorFactory implements IRecordProcessorFactory {

    private final KinesisSingleRecordProcessor processor;
    private final int attemptNumber;
    private final long sleepTime;
    private final long checkPointInterval;

    /**
     * Creates a new KinesisRecordProcessorFactory.
     *
     * @param processor          record processor that will process every single record
     * @param attemptNumber      attempts number in milliseconds to configure the internal retrier
     * @param sleepTime          sleep time in milliseconds to configure the internal retrier
     * @param checkPointInterval interval between checkpoints in milliseconds
     */

    public KinesisRecordProcessorFactory(KinesisSingleRecordProcessor processor, int attemptNumber, long sleepTime, long checkPointInterval) {
        this.processor = processor;
        this.attemptNumber = attemptNumber;
        this.sleepTime = sleepTime;
        this.checkPointInterval = checkPointInterval;
    }

    @Override
    public IRecordProcessor createProcessor() {
        return new KinesisRecordProcessor(processor, attemptNumber, sleepTime, checkPointInterval);
    }

}
