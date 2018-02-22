package com.jorgenota.utils.aws.kinesis;

import com.amazonaws.services.kinesis.model.Record;

/**
 * @author Jorge Alonso
 */
public interface KinesisSingleRecordProcessor {
    void processRecord(Record objRecord);
}
