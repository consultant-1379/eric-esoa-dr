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

import com.ericsson.bos.dr.tests.clients.keycloak.AuthorizedUser;
import com.ericsson.bos.dr.tests.env.Environment;
import com.ericsson.bos.dr.tests.env.Session;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

public class AuthenticationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();

        if (StringUtils.isEmpty(Environment.IAM_HOST)) {
            return chain.proceed(request);
        }
        final AuthorizedUser authorizedUser = request.url().toString().startsWith(Environment.IAM_HOST) ?
                Session.getAdminUser() : Session.getTestUser();

        if (authorizedUser == null) {
            return chain.proceed(chain.request());
        }

        final Request modifiedRequest = request.newBuilder()
                .header("Authorization", "Bearer " + authorizedUser.getAccessToken())
                .build();

        return chain.proceed(modifiedRequest);
    }
}