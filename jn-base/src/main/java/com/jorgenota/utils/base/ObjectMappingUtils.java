package com.jorgenota.utils.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class ObjectMappingUtils {

    public static final ObjectMapper OBJECT_MAPPER = configureObjectMapper();

    private ObjectMappingUtils() {
    }

    @SuppressWarnings("unchecked")
    public static ObjectMapper configureObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);

        ClassLoader classLoader = ObjectMappingUtils.class.getClassLoader();

        // Java 8 java.util.Optional class present?
        if (ClassUtils.isPresent("java.util.Optional", classLoader)) {
            try {
                Class<? extends Module> jdk8Module = (Class<? extends Module>)
                    ClassUtils.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module", classLoader);
                objectMapper.registerModule(instantiateClass(jdk8Module));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jdk8 not available
            }
        }

        // Java 8 java.time package present?
        if (ClassUtils.isPresent("java.time.LocalDate", classLoader)) {
            try {
                Class<? extends Module> javaTimeModule = (Class<? extends Module>)
                    ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule", classLoader);
                objectMapper.registerModule(instantiateClass(javaTimeModule));
            } catch (ClassNotFoundException ex) {
                // jackson-datatype-jsr310 not available
            }
        }

        // Kotlin present?
        if (ClassUtils.isPresent("kotlin.Unit", classLoader)) {
            try {
                Class<? extends Module> kotlinModule = (Class<? extends Module>)
                    ClassUtils.forName("com.fasterxml.jackson.module.kotlin.KotlinModule", classLoader);
                objectMapper.registerModule(instantiateClass(kotlinModule));
            } catch (ClassNotFoundException ex) {
                // jackson-module-kotlin not available
            }
        }

        return objectMapper;
    }

    private static <T> T instantiateClass(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getDeclaredConstructor();
            return ctor.newInstance();
        } catch (NoSuchMethodException var2) {
            throw new RuntimeException("No default constructor found", var2);
        } catch (InstantiationException var3) {
            throw new RuntimeException("Is it an abstract class?", var3);
        } catch (IllegalAccessException var4) {
            throw new RuntimeException("Is the constructor accessible?", var4);
        } catch (IllegalArgumentException var5) {
            throw new RuntimeException("Illegal arguments for constructor", var5);
        } catch (InvocationTargetException var6) {
            throw new RuntimeException("Constructor threw exception", var6.getTargetException());
        }
    }
}