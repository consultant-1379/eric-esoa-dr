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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeaturePack {

    private String id;
    private String name;
    private String description;
    private String createdAt;
    private String modifiedAt;
    private List<ConfigurationSummary> applications;
    private List<ConfigurationSummary> listeners;
    private List<ConfigurationSummary> assets;
    private List<ConfigurationSummary> inputs;
    private List<Map<String, Object>> properties;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public List<ConfigurationSummary> getApplications() {
        return applications;
    }

    public void setApplications(List<ConfigurationSummary> applications) {
        this.applications = applications;
    }

    public List<ConfigurationSummary> getListeners() {
        return listeners;
    }

    public void setListeners(List<ConfigurationSummary> listeners) {
        this.listeners = listeners;
    }

    public List<ConfigurationSummary> getAssets() {
        return assets;
    }

    public void setAssets(List<ConfigurationSummary> assets) {
        this.assets = assets;
    }

    public List<ConfigurationSummary> getInputs() {
        return inputs;
    }

    public void setInputs(List<ConfigurationSummary> inputs) {
        this.inputs = inputs;
    }

    public List<Map<String, Object>> getProperties() {
        return properties;
    }

    public void setProperties(List<Map<String, Object>> properties) {
        this.properties = properties;
    }
}
