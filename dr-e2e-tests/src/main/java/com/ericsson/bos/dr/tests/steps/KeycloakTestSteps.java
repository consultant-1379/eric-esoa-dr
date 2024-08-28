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
package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.IAM_ADMIN_PWD;
import static com.ericsson.bos.dr.tests.env.Environment.IAM_ADMIN_USER;
import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import static com.ericsson.bos.dr.tests.env.Environment.TEST_USER;
import static com.ericsson.bos.dr.tests.env.Environment.TEST_USER_GROUPS;
import static com.ericsson.bos.dr.tests.env.Environment.TEST_USER_PWD;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.bos.dr.tests.clients.keycloak.AuthorizedUser;
import com.ericsson.bos.dr.tests.clients.keycloak.KeycloakClient;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Session;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;

public class KeycloakTestSteps {

    private final KeycloakClient keycloakClient = new KeycloakClient();

    @Step("Login with Keycloak Admin User")
    public void loginWithAdminUser() {
        LOGGER.info("Login with admin user: user={}", IAM_ADMIN_USER);
        final HttpResponse response = keycloakClient.adminLogin(IAM_ADMIN_USER, IAM_ADMIN_PWD);
        final AuthorizedUser authorizedUser = checkLoginResponse(response);
        Session.setAdminUser(authorizedUser);
    }

    public void loginWithDrTestUser() {
        loginWithTestUser(TEST_USER);
    }

    @Step("Login with D&R Test User {0}")
    public void loginWithTestUser(String username) {
        LOGGER.info("Login with test user: user={}", username);
        final HttpResponse response = keycloakClient.authLogin(username, TEST_USER_PWD);
        final AuthorizedUser authorizedUser = checkLoginResponse(response);
        Session.setTestUser(authorizedUser);
    }

    private AuthorizedUser checkLoginResponse(final HttpResponse response) {
        LOGGER.info("Login response: {}", response);
        Allure.attachment("Login  Response", response.toString());
        Assertions.assertThat(response.code()).as("Login").isEqualTo(200);
        return JsonUtils.read(response.body(), AuthorizedUser.class);
    }

    @Step("Create D&R Test User")
    public void createDrTestUser() {
        createUser(TEST_USER, TEST_USER_GROUPS);
    }

    @Step("Create User {0}")
    public void createUser(final String username, final List<String> groups) {
        loginWithAdminUser();
        LOGGER.info("Create user: {}", username);
        HttpResponse userResponse = keycloakClient.createUser(username, groups);
        LOGGER.info("Create user response: {}", userResponse);
        Assertions.assertThat(userResponse.code()).as("Create user").isIn(201, 409);
        if (userResponse.isSuccessful()) {
            setUserPassword(userResponse);
        }
    }

    private void setUserPassword(final HttpResponse createUserResponse) {
        final String userLocation = createUserResponse.header("location");
        final String userId = userLocation.substring(userLocation.lastIndexOf('/') + 1);
        LOGGER.info("Set user password");
        HttpResponse pwdResponse = keycloakClient.setUserPassword(userId, TEST_USER_PWD);
        LOGGER.info("Set user password response: {}", pwdResponse);
        Assertions.assertThat(pwdResponse.code()).as("Set user password").isEqualTo(204);
    }

    @Step("Delete User")
    public void deleteDrTestUser() {
        deleteUser(TEST_USER);
    }

    @Step("Delete User {1}")
    public void deleteUser(final String username) {
        LOGGER.info("Get all users");
        final HttpResponse userResponse = keycloakClient.getUsers();
        LOGGER.info("Get all users response : {}", userResponse);
        if (userResponse.isSuccessful()) {
            final Map<String, String> userIdByName = JsonUtils.readList(userResponse.body(), Map.class).stream()
                    .collect(Collectors.toMap(m -> m.get("username").toString(), m -> m.get("id").toString()));
            if (userIdByName.containsKey(username)) {
                LOGGER.info("Deleting user: {}", username);
                final HttpResponse deleteResponse = keycloakClient.deleteUser(userIdByName.get(username));
                LOGGER.info("Delete user response: {}", deleteResponse);
            }
        }
    }
}