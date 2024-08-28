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
package com.ericsson.bos.dr.tests.clients.dr;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Job {

    public static final List<String> COMPLETED_JOB_STATES = Collections.unmodifiableList(
            Stream.of("DISCOVERY_FAILED", "RECONCILE_FAILED", "COMPLETED").toList());

    private String id;
    private String name;
    private String description;
    private String featurePackId;
    private String featurePackName;
    private String applicationId;
    private String applicationName;
    private String applicationJobName;
    private String startDate;
    private String completedDate;
    private String status;
    private Map<String, Object> inputs;
    private Map<String, Object> executionOptions;
    private long discoveredObjectsCount;
    private long reconciledObjectsCount;
    private long reconciledObjectsErrorCount;
    private String errorMessage;
    private String jobScheduleId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeaturePackId() {
        return featurePackId;
    }

    public void setFeaturePackId(String featurePackId) {
        this.featurePackId = featurePackId;
    }

    public String getFeaturePackName() {
        return featurePackName;
    }

    public void setFeaturePackName(String featurePackName) {
        this.featurePackName = featurePackName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationJobName() {
        return applicationJobName;
    }

    public void setApplicationJobName(String applicationJobName) {
        this.applicationJobName = applicationJobName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public long getDiscoveredObjectsCount() {
        return discoveredObjectsCount;
    }

    public void setDiscoveredObjectsCount(long discoveredObjectsCount) {
        this.discoveredObjectsCount = discoveredObjectsCount;
    }

    public long getReconciledObjectsCount() {
        return reconciledObjectsCount;
    }

    public void setReconciledObjectsCount(long reconciledObjectsCount) {
        this.reconciledObjectsCount = reconciledObjectsCount;
    }

    public long getReconciledObjectsErrorCount() {
        return reconciledObjectsErrorCount;
    }

    public void setReconciledObjectsErrorCount(long reconciledObjectsErrorCount) {
        this.reconciledObjectsErrorCount = reconciledObjectsErrorCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getJobScheduleId() {
        return jobScheduleId;
    }

    public void setJobScheduleId(String jobScheduleId) {
        this.jobScheduleId = jobScheduleId;
    }

    public Map<String, Object> getExecutionOptions() {
        return executionOptions;
    }

    public void setExecutionOptions(Map<String, Object> executionOptions) {
        this.executionOptions = executionOptions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("description", description)
                .append("featurePackId", featurePackId)
                .append("featurePackName", featurePackName)
                .append("applicationId", applicationId)
                .append("applicationName", applicationName)
                .append("applicationJobName", applicationJobName)
                .append("startDate", startDate)
                .append("completedDate", completedDate)
                .append("status", status)
                .append("inputs", inputs)
                .append("discoveredObjectsCount", discoveredObjectsCount)
                .append("reconciledObjectsCount", reconciledObjectsCount)
                .append("reconciledObjectsErrorCount", reconciledObjectsErrorCount)
                .append("errorMessage", errorMessage)
                .append("jobScheduleId", jobScheduleId).toString();
    }
}
