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
package com.ericsson.bos.dr.tests.clients.restservice;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Environment;

public class RestServiceClient {

    private static final String REST_SERVICE_RESOURCE_URL = Environment.REST_SERVICE_HOST.concat("/rest-service/v1/resource-configurations");

    public HttpResponse uploadResource(final String name, final byte[] contents) {
        return HttpOperations.postFile(REST_SERVICE_RESOURCE_URL, name, contents);
    }

    public HttpResponse deleteResource(final String name) {
        return HttpOperations.delete(REST_SERVICE_RESOURCE_URL.concat("/").concat(name));
    }
}
