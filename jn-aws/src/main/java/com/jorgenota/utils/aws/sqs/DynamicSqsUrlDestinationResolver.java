package com.jorgenota.utils.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.jorgenota.utils.aws.support.ResourceIdResolver;
import org.springframework.messaging.core.DestinationResolutionException;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.URISyntaxException;

public class DynamicSqsUrlDestinationResolver implements DestinationResolver<String> {

    private final AmazonSQS amazonSqs;
    private final ResourceIdResolver resourceIdResolver;
    private boolean autoCreate;

    public DynamicSqsUrlDestinationResolver(AmazonSQS amazonSqs, ResourceIdResolver resourceIdResolver) {
        Assert.notNull(amazonSqs, "amazonSqs must not be null");

        this.amazonSqs = amazonSqs;
        this.resourceIdResolver = resourceIdResolver;
    }

    public DynamicSqsUrlDestinationResolver(AmazonSQS amazonSqs) {
        this(amazonSqs, null);
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    @Override
    public String resolveDestination(String name) throws DestinationResolutionException {
        String queueName = name;

        if (this.resourceIdResolver != null) {
            queueName = this.resourceIdResolver.resolveToPhysicalResourceId(name);
        }

        if (isValidQueueUrl(queueName)) {
            return queueName;
        }

        if (this.autoCreate) {
            //Auto-create is fine to be called even if the queue exists.
            CreateQueueResult createQueueResult = this.amazonSqs.createQueue(new CreateQueueRequest(queueName));
            return createQueueResult.getQueueUrl();
        } else {
            try {
                GetQueueUrlResult getQueueUrlResult = this.amazonSqs.getQueueUrl(new GetQueueUrlRequest(queueName));
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
