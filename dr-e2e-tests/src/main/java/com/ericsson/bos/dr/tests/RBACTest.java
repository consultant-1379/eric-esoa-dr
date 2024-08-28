package com.ericsson.bos.dr.tests;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;

import java.util.Arrays;
import java.util.Collections;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.datadriven.RBACData;
import com.ericsson.bos.dr.tests.datadriven.RBACDataProvider;
import com.ericsson.bos.dr.tests.env.Environment;
import com.ericsson.bos.dr.tests.steps.KeycloakTestSteps;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test access to D&R REST API is restricted to authorized users only.
 */
public class RBACTest {

    private static final String DR_BASE_URL = Environment.DR_HOST;

    private static final String RO_USER = "dr-read-only-user";
    private static final String RW_USER = "dr-read-write-user";
    private static final String ADMIN_USER = "dr-admin-user";
    private static final String UNAUTHORIZED_USER = "unauthorized-user";

    private final KeycloakTestSteps keycloakTestSteps = new KeycloakTestSteps();

    @BeforeClass
    public void setup() {
        keycloakTestSteps.createUser(RO_USER, Arrays.asList("eric-bos-dr:reader"));
        keycloakTestSteps.createUser(RW_USER, Arrays.asList("eric-bos-dr:reader", "eric-bos-dr:writer"));
        keycloakTestSteps.createUser(ADMIN_USER, Arrays.asList("eric-bos-dr:reader", "eric-bos-dr:writer","eric-bos-dr:admin"));
        keycloakTestSteps.createUser(UNAUTHORIZED_USER, Collections.emptyList());
    }

    @AfterClass
    public void cleanup() {
        keycloakTestSteps.deleteUser(RO_USER);
        keycloakTestSteps.deleteUser(RW_USER);
        keycloakTestSteps.deleteUser(ADMIN_USER);
        keycloakTestSteps.deleteUser(UNAUTHORIZED_USER);
    }

    @Test(dataProvider = "rbacDataProvider", dataProviderClass = RBACDataProvider.class)
    @Description("This tests dr-service and rest-service APIs can only be accessed by users with the required roles.")
    @Feature("RBAC")
    public void testUserAccessControl(RBACData data) {
        keycloakTestSteps.loginWithTestUser(data.getUsername());
        final String url = DR_BASE_URL.concat(data.getPath());
        LOGGER.info("RBAC Request for user {}: {}:{}", data.getUsername(), data.getMethod(), data.getPath());
        final HttpResponse response = executeHttpRequest(url, data.getMethod());
        LOGGER.info("Response: {}", response);
        Allure.attachment("Http Response", response.toString());
        Assertions.assertThat(response.code()).as("Check response code").isEqualTo(data.getExpectedResponseCode());
    }

    @Step("Execute {1}:{0}")
    private HttpResponse executeHttpRequest(final String url, final String method) {
        final HttpResponse response;
        if ("GET".equals(method)) {
            response = HttpOperations.get(url);
        } else if ("DELETE".equals(method)) {
            response = HttpOperations.delete(url);
        } else if ("PUT".equals(method)) {
            response = HttpOperations.put(url, Collections.emptyMap());
        } else if ("POST".equals(method)) {
            response = HttpOperations.post(url, Collections.emptyMap());
        } else if ("PATCH".equals(method)) {
            response = HttpOperations.patch(url, Collections.emptyMap());
        } else {
            throw new IllegalArgumentException("Unknown method: " + method);
        }
        return response;
    }
}