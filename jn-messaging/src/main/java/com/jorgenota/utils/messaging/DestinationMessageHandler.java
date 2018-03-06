package com.jorgenota.utils.messaging;

import java.util.Set;

/**
 * @author Jorge Alonso
 */
public interface DestinationMessageHandler<T, U extends MessageHeaders> extends MessageHandler<T, U> {
    /**
     * @return the logical destination names from which this handler handles messages
     */
    Set<String> getDestinations();
}
