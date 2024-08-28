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
package com.ericsson.bos.dr.tests.clients.okhttp3;

import java.io.IOException;

import okhttp3.Response;

public class HttpResponse {

    private final Response response;
    private String body;

    private HttpResponse(final Response response) {
        this.response = response;
        try {
            this.body = response.body().string();
            response.body().close();
        } catch (IOException e) {
            throw new IllegalStateException("Error reading http response body", e);
        }
    }

    public static HttpResponse wrap(Response response) {
        return new HttpResponse(response);
    }

    public String body() {
        return body;
    }

    public int code() {
        return response.code();
    }

    public boolean isSuccessful() {
        return response.isSuccessful();
    }

    public String header(String name) {
        return response.header(name);
    }

    @Override
    public String toString() {
        return new StringBuilder().append("code=")
                .append(response.code())
                .append(",")
                .append("body=")
                .append(body)
                .toString();
    }
}