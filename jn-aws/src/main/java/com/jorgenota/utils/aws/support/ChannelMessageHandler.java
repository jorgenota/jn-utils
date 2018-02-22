package com.jorgenota.utils.aws.support;

import org.springframework.messaging.MessageHandler;

import java.util.Set;

/**
 * @author Jorge Alonso
 */
public interface ChannelMessageHandler extends MessageHandler {
    Set<String> getChannels();
}
