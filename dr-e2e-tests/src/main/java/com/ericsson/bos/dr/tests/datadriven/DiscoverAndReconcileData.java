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
package com.ericsson.bos.dr.tests.datadriven;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DiscoverAndReconcileData {

    private String featurePack;
    private String app;
    private String job;
    private String description;
    private Map<String, Object> inputs;
    private int expectedDiscoveryCount;
    private int expectedReconcileCount;
    @JsonSetter(nulls = Nulls.SKIP)
    private boolean requiresMtlsEnabled = false;

    public String getFeaturePack() {
        return featurePack;
    }

    public void setFeaturePack(String featurePack) {
        this.featurePack = featurePack;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getExpectedDiscoveryCount() {
        return expectedDiscoveryCount;
    }

    public void setExpectedDiscoveryCount(int expectedDiscoveryCount) {
        this.expectedDiscoveryCount = expectedDiscoveryCount;
    }

    public int getExpectedReconcileCount() {
        return expectedReconcileCount;
    }

    public void setExpectedReconcileCount(int expectedReconcileCount) {
        this.expectedReconcileCount = expectedReconcileCount;
    }

    public boolean isRequiresMtlsEnabled() {
        return requiresMtlsEnabled;
    }

    public void setRequiresMtlsEnabled(boolean requiresMtlsEnabled) {
        this.requiresMtlsEnabled = requiresMtlsEnabled;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("featurePack", featurePack)
                .append("app", app)
                .append("job", job)
                .append("description", description)
                .append("inputs", inputs)
                .append("expectedDiscoveryCount", expectedDiscoveryCount)
                .append("expectedReconcileCount", expectedReconcileCount)
                .append("requiresMtlsEnabled", requiresMtlsEnabled)
                .toString();
    }
}