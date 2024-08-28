package com.ericsson.bos.dr.tests.datadriven;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ScheduleData {

    private String name;
    private String description;
    private String expression;
    private String featurePack;
    private String app;
    private String job;
    private Map<String, Object> inputs;
    private boolean autoReconcile;

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

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

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

    public boolean isAutoReconcile() {
        return autoReconcile;
    }

    public void setAutoReconcile(boolean autoReconcile) {
        this.autoReconcile = autoReconcile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name)
                .append("description", description)
                .append("expression", expression)
                .append("featurePack", featurePack)
                .append("app", app).append("job", job)
                .append("inputs", inputs)
                .append("autoReconcile", autoReconcile)
                .toString();
    }
}
