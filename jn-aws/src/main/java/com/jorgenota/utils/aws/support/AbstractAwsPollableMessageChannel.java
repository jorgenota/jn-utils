package com.jorgenota.utils.aws.support;

import com.amazonaws.AmazonServiceException;
import com.jorgenota.utils.messaging.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @param <T> the type of the serialized payload that conforms the message. Can be {@code String}  or {@code byte[]}
 * @param <U> the type of the message attributes. Must be a subclass of MessageHeaders
 * @author Jorge Alonso
 */
public abstract class AbstractAwsPollableMessageChannel<T, U extends MessageHeaders> extends AbstractAwsMessageChannel<T, U> implements PollableChannel<T, U> {

    @Override
    public Message<T, U> receive(long timeout) throws MessagingException {
        try {
            return receiveInternal(timeout);
        } catch (AmazonServiceException e) {
            throw new MessageDeliveryException(e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new MessageDeliveryException(e.getMessage(), e.getCause());
        } catch (TimeoutException e) {
            throw new MessageTimeoutException(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessageDeliveryException("Thread was interrupted while receiving a message", e);
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                throw (MessagingException) e;
            }
            throw new MessageDeliveryException(e.getMessage());
        }
    }

    protected abstract Message<T, U> receiveInternal(long timeout) throws Exception;
}
