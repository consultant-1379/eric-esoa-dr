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
package com.ericsson.bos.dr.tests.clients.subsystem;

import java.util.HashMap;
import java.util.Map;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Environment;

public class SubsystemClient {

    private static final String SUBSYSTEMS_URL = Environment.SUBSYSTEM_HOST.concat("/subsystem-manager/v2/subsystems");
    private static final String SUBSYSTEM_URL = Environment.SUBSYSTEM_HOST.concat("/subsystem-manager/v2/subsystems/%s");
    private static final String SUBSYSTEM_TYPE_URL = Environment.SUBSYSTEM_HOST.concat("/subsystem-manager/v2/subsystem-types");

    public HttpResponse createSubsystemType(final String type) {
        final Map<String, String> body = new HashMap<>();
        body.put("type", type);
        body.put("alias", type);
        return HttpOperations.post(SUBSYSTEM_TYPE_URL, body);
    }

    public HttpResponse createSubsystem(final Map<String, Object> subsystem) {
        return HttpOperations.post(SUBSYSTEMS_URL, subsystem);
    }

    public HttpResponse getSubsystems() {
        return HttpOperations.get(SUBSYSTEMS_URL);
    }

    public HttpResponse deleteSubsystem(final long id) {
        return HttpOperations.delete(String.format(SUBSYSTEM_URL, id));
    }
}