package com.ericsson.bos.dr.tests.datadriven;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class RBACData {

    private String username;
    private String path;
    private String method;
    private int expectedResponseCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public void setExpectedResponseCode(int expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("username", username)
                .append("path", path)
                .append("method", method)
                .append("expectedResponseCode", expectedResponseCode)
                .toString();
    }
}
