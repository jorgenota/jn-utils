package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.jorgenota.utils.messaging.DestinationResolutionException;
import com.jorgenota.utils.messaging.DestinationResolver;

import java.net.URI;
import java.net.URISyntaxException;

import static com.jorgenota.utils.base.Preconditions.notNull;

public class DynamicSqsUrlDestinationResolver implements DestinationResolver<String> {

    private final AmazonSQS amazonSqs;
    private boolean autoCreate;

    public DynamicSqsUrlDestinationResolver(AmazonSQS amazonSqs) {
        this.amazonSqs = notNull(amazonSqs, "amazonSqs must not be null");
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    @Override
    public String resolveDestination(String name) throws DestinationResolutionException {

        if (isValidQueueUrl(name)) {
            return name;
        }

        if (this.autoCreate) {
            //Auto-create is fine to be called even if the queue exists.
            CreateQueueResult createQueueResult = this.amazonSqs.createQueue(new CreateQueueRequest(name));
            return createQueueResult.getQueueUrl();
        } else {
            try {
                GetQueueUrlResult getQueueUrlResult = this.amazonSqs.getQueueUrl(new GetQueueUrlRequest(name));
                return getQueueUrlResult.getQueueUrl();
            } catch (QueueDoesNotExistException e) {
                throw new DestinationResolutionException(e.getMessage(), e);
            }
        }
    }

    private static boolean isValidQueueUrl(String name) {
        try {
            URI candidate = new URI(name);
            return ("http".equals(candidate.getScheme()) || "https".equals(candidate.getScheme()));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
