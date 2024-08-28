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
package com.ericsson.bos.dr.tests.env;

import com.ericsson.bos.dr.tests.clients.keycloak.AuthorizedUser;

public class Session {

    private static final ThreadLocal<AuthorizedUser> testUserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AuthorizedUser> adminUserThreadLocal = new ThreadLocal<>();

    private Session() {}

    public static void setTestUser(final AuthorizedUser authorizedUser) {
        testUserThreadLocal.set(authorizedUser);
    }

    public static AuthorizedUser getTestUser() {
        return testUserThreadLocal.get();
    }

    public static void resetTestUser() {
        testUserThreadLocal.remove();
    }

    public static void setAdminUser(final AuthorizedUser authorizedUser) {
        adminUserThreadLocal.set(authorizedUser);
    }

    public static AuthorizedUser getAdminUser() {
        return adminUserThreadLocal.get();
    }

    public static void resetAdminUser() {
        adminUserThreadLocal.remove();
    }
}