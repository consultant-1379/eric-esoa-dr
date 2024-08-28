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
import java.util.function.BiConsumer;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ResourceUtils {

    private ResourceUtils() {}

    public static void walkClasspathDir(final String dirPath, BiConsumer<String, byte[]> consumer) {
        try {
            final String locationPattern = "classpath:".concat(dirPath).concat("/").concat("*.*");
            // Using spring here as Class#getResourceAsStream() worked when run in IDE but not when running the jar.
            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(ResourceUtils.class.getClassLoader());
            final Resource[] resources = resolver.getResources(locationPattern);
            for (final Resource r : resources) {
                consumer.accept(r.getFilename(), r.getContentAsByteArray());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading classpath dir: " + dirPath, e);
        }
    }
}