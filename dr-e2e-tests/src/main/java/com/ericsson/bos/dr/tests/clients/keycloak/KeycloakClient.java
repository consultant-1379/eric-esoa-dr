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
package com.ericsson.bos.dr.tests.clients.keycloak;

import static com.ericsson.bos.dr.tests.env.Environment.IAM_CLIENT_SECRET;
import static com.ericsson.bos.dr.tests.env.Environment.IAM_HOST;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;

public class KeycloakClient {

    private static final String IAM_TOKEN_URL = IAM_HOST + "/auth/realms/master/protocol/openid-connect/token";
    private static final String IAM_USER_URL = IAM_HOST + "/auth/admin/realms/master/users";
    private static final String IAM_PWD_URL = IAM_HOST + "/auth/admin/realms/master/users/%s/reset-password";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String ADMIN_CLIENT = "admin-cli";
    private static final String AUTH_CLIENT = "authn-proxy";

    public HttpResponse adminLogin(final String adminUser, final String adminPassword) {
        final Map<String, String> formData = new HashMap<>();
        formData.put(CLIENT_ID, ADMIN_CLIENT);
        formData.put(USERNAME, adminUser);
        formData.put(PASSWORD, adminPassword);
        formData.put(GRANT_TYPE, PASSWORD);
        return HttpOperations.postForm(IAM_TOKEN_URL, formData);
    }

    public HttpResponse authLogin(final String user, final String password) {
        final Map<String, String> formData = new HashMap<>();
        formData.put(USERNAME, user);
        formData.put(PASSWORD, password);
        formData.put(CLIENT_ID, AUTH_CLIENT);
        formData.put(GRANT_TYPE, PASSWORD);
        formData.put(CLIENT_SECRET, IAM_CLIENT_SECRET);
        return HttpOperations.postForm(IAM_TOKEN_URL, formData);
    }

    public HttpResponse createUser(final String username, final List<String> groups) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("enabled", true);
        userData.put(USERNAME, username);
        userData.put("emailVerified", true);
        userData.put("email", username.concat("@ericsson.com"));
        userData.put("groups", groups);
        return HttpOperations.post(IAM_USER_URL, userData);
    }

    public HttpResponse setUserPassword(final String userId, final String password) {
        final Map<String, Object> userData = new HashMap<>();
        userData.put("type", PASSWORD);
        userData.put("value", password);
        userData.put("temporary", false);
        return HttpOperations.put(String.format(IAM_PWD_URL, userId), userData);
    }

    public HttpResponse getUsers() {
        return HttpOperations.get(IAM_USER_URL);
    }

    public HttpResponse deleteUser(final String userId) {
        return HttpOperations.delete(IAM_USER_URL.concat("/").concat(userId));
    }
}