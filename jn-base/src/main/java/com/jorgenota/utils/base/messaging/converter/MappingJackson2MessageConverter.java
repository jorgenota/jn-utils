/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jorgenota.utils.base.messaging.converter;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.jorgenota.utils.base.messaging.Message;
import com.jorgenota.utils.base.messaging.MessageHeaders;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static com.jorgenota.utils.base.Preconditions.notNull;

/**
 * A Jackson 2 based {@link MessageConverter} implementation.
 * <p>
 * <p>It customizes Jackson's default properties with the following ones:
 * <ul>
 * <li>{@link MapperFeature#DEFAULT_VIEW_INCLUSION} is disabled</li>
 * <li>{@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} is disabled</li>
 * </ul>
 */
@Slf4j
public class MappingJackson2MessageConverter<T, U extends MessageHeaders, V> extends AbstractMessageConverter<T, U, V> {

    private final JsonEncoding charset;
    private ObjectMapper objectMapper;
    private Boolean prettyPrint;


    /**
     * Construct a {@code MappingJackson2MessageConverter} supporting
     * the {@code application/json} MIME type with {@code UTF-8} character set.
     */
    public MappingJackson2MessageConverter() {
        this(JsonEncoding.UTF8);
    }


    public MappingJackson2MessageConverter(JsonEncoding charset) {
        super();
        this.charset = charset;
        initObjectMapper();
    }


    private void initObjectMapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Return the underlying {@code ObjectMapper} for this converter.
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    /**
     * Set the {@code ObjectMapper} for this converter.
     * If not set, a default {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>Setting a custom-configured {@code ObjectMapper} is one way to take further
     * control of the JSON serialization process. For example, an extended
     * {@link com.fasterxml.jackson.databind.ser.SerializerFactory} can be
     * configured that provides custom serializers for specific types. The other
     * option for refining the serialization process is to use Jackson's provided
     * annotations on the types to be serialized, in which case a custom-configured
     * ObjectMapper is unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = notNull(objectMapper, "ObjectMapper must not be null");
        configurePrettyPrint();
    }

    /**
     * Whether to use the {@link DefaultPrettyPrinter} when writing JSON.
     * This is a shortcut for setting up an {@code ObjectMapper} as follows:
     * <pre class="code">
     * ObjectMapper mapper = new ObjectMapper();
     * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
     * converter.setObjectMapper(mapper);
     * </pre>
     */
    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
        }
    }

    @Override
    protected V convertFromInternal(Message<T, U> message, Class<V> targetClass) throws IOException {
        JavaType javaType = getJavaType(targetClass);
        Object payload = message.getPayload();

        if (payload instanceof byte[]) {
            return this.objectMapper.readValue((byte[]) payload, javaType);
        } else {
            return this.objectMapper.readValue(payload.toString(), javaType);
        }
    }

    private JavaType getJavaType(Class<?> targetClass) {
        return this.objectMapper.constructType(targetClass);
    }

    @Override
    protected T convertToInternal(V payload, U attributes) throws IOException {
        if (byte[].class == getSerializedPayloadClass()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            JsonGenerator generator = this.objectMapper.getFactory().createGenerator(out, this.charset);
            this.objectMapper.writeValue(generator, payload);
            return (T) out.toByteArray();
        } else {
            Writer writer = new StringWriter();
            this.objectMapper.writeValue(writer, payload);
            return (T) writer.toString();
        }
    }

    private Class<?> extractViewClass(JsonView annotation, Object conversionHint) {
        Class<?>[] classes = annotation.value();
        if (classes.length != 1) {
            throw new IllegalArgumentException(
                    "@JsonView only supported for handler methods with exactly 1 class argument: " + conversionHint);
        }
        return classes[0];
    }
}
