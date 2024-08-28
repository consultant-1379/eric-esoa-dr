/*******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package com.ericsson.bos.dr.tests.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import okhttp3.Response;

public abstract class JsonUtils {

    private JsonUtils() {
    }

    private static final ObjectMapper OM = new ObjectMapper();

    public static <T> T read(final String value, final Class<T> type) {
        try {
            return OM.readValue(value, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T read(final String value, final Class<T> type, final Class<?> paramaterizedType) {
        try {
            return (T) OM.readValue(value, TypeFactory.defaultInstance().constructParametricType(type, paramaterizedType));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T readClassPathResource(final String path, final Class<T> type) {
        try {
            return OM.readValue(JsonUtils.class.getResourceAsStream(path), type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T read(final byte[] value, final Class<T> type) {
        try {
            return OM.readValue(value, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readList(final String value, final Class<T> type) {
        try {
            return OM.readValue(value, OM.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readList(final InputStream inputStream, final Class<T> type) {
        try {
            return OM.readValue(inputStream, OM.getTypeFactory().constructCollectionType(List.class, type));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T convert(final Object value, Class<T> type) {
        return OM.convertValue(value, type);
    }

    public static String serialize(final Object value) {
        try {
            return OM.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
