package com.ericsson.bos.dr.tests.clients.dr;

import java.util.Map;

public class TriggerResult {

    private Job job;
    private Map<String, Object> outputs;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }
}
