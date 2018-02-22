package com.jorgenota.utils.aws.utils;

import com.jorgenota.utils.aws.support.AbstractMessageChannelMessagingSendingTemplate;
import org.springframework.util.ClassUtils;

/**
 * @author Jorge Alonso
 */
public final class ConversionUtils {

    public static final boolean JACKSON_2_PRESENT = ClassUtils.isPresent(
        "com.fasterxml.jackson.databind.ObjectMapper", AbstractMessageChannelMessagingSendingTemplate.class.getClassLoader());


}
