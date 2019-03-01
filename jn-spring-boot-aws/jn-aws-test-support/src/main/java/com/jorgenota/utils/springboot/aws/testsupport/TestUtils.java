package com.jorgenota.utils.springboot.aws.testsupport;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Jorge Alonso
 */
public class TestUtils {

    public static URI toURI(String configuredEndpoint) {
        try {
            return new URI(configuredEndpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Endpoint value is an invalid URI");
        }
    }
}
