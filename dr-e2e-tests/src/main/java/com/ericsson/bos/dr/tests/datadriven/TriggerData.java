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

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TriggerData {

    private String featurePack;
    private String listener;
    private Map<String, Object> event = Collections.emptyMap();
    private int expectedDiscoveryCount;
    private int expectedReconcileCount;

    public String getFeaturePack() {
        return featurePack;
    }

    public void setFeaturePack(String featurePack) {
        this.featurePack = featurePack;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public Map<String, Object> getEvent() {
        return event;
    }

    public void setEvent(Map<String, Object> event) {
        this.event = event;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("featurePack", featurePack)
                .append("listener", listener).append("event", event)
                .append("expectedDiscoveryCount", expectedDiscoveryCount)
                .append("expectedReconcileCount", expectedReconcileCount)
                .toString();
    }
}