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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpOperations {

    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    private HttpOperations() {}

    private static final OkHttpClient httpClient = new HttpClientConfiguration().createHttpClient();

    public static HttpResponse get(final String url) {
        try {
            final Request request = new Request.Builder().url(url).get().build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }    }

    public static HttpResponse post(final String url, final Object body) {
        return post(url, body, "application/json");
    }

    public static HttpResponse post(final String url, final Object body, final String contentType) {
        try {
            final String contents = new ObjectMapper().writeValueAsString(body);
            final RequestBody requestBody = RequestBody.create(MediaType.get(contentType), contents);
            final Request request = new Request.Builder().url(url).post(requestBody).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse put(final String url, final Object body) {
        try {
            final String contents = new ObjectMapper().writeValueAsString(body);
            final RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, contents);
            final Request request = new Request.Builder().url(url).put(requestBody).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse patch(final String url, final Object body) {
        try {
            final String contents = new ObjectMapper().writeValueAsString(body);
            final RequestBody requestBody = RequestBody.create(JSON_MEDIA_TYPE, contents);
            final Request request = new Request.Builder().url(url).patch(requestBody).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse postFile(final String url, final File file,
                                    final Map<String, String> queryParams) {
        try {
            final HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            queryParams.forEach(urlBuilder::addQueryParameter);
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            final Request request = new Request.Builder().url(urlBuilder.build()).post(requestBody).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse postFile(final String url, final String fileName, final byte[] fileContents) {
        try {
            final HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                            RequestBody.create(MediaType.parse("application/octet-stream"), fileContents))
                    .build();
            final Request request = new Request.Builder().url(urlBuilder.build()).post(requestBody).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse postForm(final String url, final Map<String, String> formData) {
        try {
            final FormBody.Builder formBuilder = new FormBody.Builder();
            formData.entrySet().forEach(e -> formBuilder.add(e.getKey(), e.getValue()));
            final Request request = new Request.Builder().url(url).post(formBuilder.build()).build();
            return HttpResponse.wrap(httpClient.newCall(request).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse delete(final String url) {
        return delete(url, Collections.emptyMap());
    }

    public static HttpResponse delete(final String url, final Map<String, String> headers) {
        try {
            final Request.Builder request = new Request.Builder().url(url).delete();
            headers.entrySet().forEach(e -> request.addHeader(e.getKey(), e.getValue()));
            return HttpResponse.wrap(httpClient.newCall(request.build()).execute());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}