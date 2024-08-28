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

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Environment;
import okhttp3.Response;

public class DrClient {

    private static final String DR_URL = Environment.DR_HOST.concat("/discovery-and-reconciliation/v1");
    private static final String FP_URL = DR_URL.concat("/feature-packs");
    private static final String JOB_URL = DR_URL.concat("/jobs");
    private static final String JOB_SCHEDULE_URL = DR_URL.concat("/job-schedules");
    private static final String DISCOVERED_OBJECTS_URL = JOB_URL.concat("/%s/discovered-objects");
    private static final String RECONCILE_URL = JOB_URL.concat("/%s/reconciliations");
    private static final String TRIGGER_URL = DR_URL.concat("/feature-packs/%s/listener/%s/trigger");
    private static final String MESSAGE_SUBSCRIPTIONS_URL = DR_URL.concat("/feature-packs/%s/listener/%s/message-subscriptions");

    public HttpResponse uploadFeaturePack(final String name, final String description, final File featurepackFile) {
        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("description", description);
        return HttpOperations.postFile(FP_URL, featurepackFile, queryParams);
    }

    public HttpResponse deleteFeaturePack(String featurePackId) {
        return HttpOperations.delete(FP_URL.concat("/").concat(featurePackId));
    }

    public HttpResponse discover(final String jobName, final String featurePackName, final String applicationName,
                                      final String applicationJobName, final Map<String, Object> inputs) {
        final Map<String, Object> body = new HashMap<>();
        body.put("name", jobName);
        body.put("featurePackName", featurePackName);
        body.put("applicationName", applicationName);
        body.put("applicationJobName", applicationJobName);
        body.put("inputs", inputs);
        body.put("executionOptions", Collections.singletonMap("autoReconcile", false));
        return HttpOperations.post(JOB_URL, body);
    }

    public HttpResponse reconcileAll(final String jobId, final Map<String, Object> inputs) {
        final Map<String, Object> body = new HashMap<>();
        body.put("inputs", inputs);
        return HttpOperations.post(String.format(RECONCILE_URL, jobId), body);
    }

    public HttpResponse trigger(String featurePackName, final String listenerName, final Map<String, Object> body) {
        return HttpOperations.post(String.format(TRIGGER_URL, featurePackName, listenerName), body);
    }

    public HttpResponse getJob(final String jobId) {
        return HttpOperations.get(JOB_URL.concat("/").concat(jobId));
    }

    public HttpResponse getJobs(final String queryParams) {
        return HttpOperations.get(JOB_URL.concat("?".concat(queryParams)));
    }

    public HttpResponse deleteJob(String jobId, final boolean force) {
        final Map<String, String> headers = Collections.singletonMap("force", String.valueOf(force));
        return HttpOperations.delete(JOB_URL.concat("/").concat(jobId), headers);
    }

    public HttpResponse getFeaturePacks() {
        return HttpOperations.get(FP_URL);
    }

    public HttpResponse deleteFeaturePack(Long id) {
        return HttpOperations.delete(FP_URL.concat("/").concat(id.toString()));
    }

    public HttpResponse getDiscoveredObjects(final String jobId) {
        return HttpOperations.get(String.format(DISCOVERED_OBJECTS_URL, jobId));
    }

    public HttpResponse createJobSchedule(final String name, final String expression, final Job jobSpecification) {
        final Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", "test job schedule");
        body.put("expression", expression);
        body.put("jobSpecification", jobSpecification);
        return HttpOperations.post(JOB_SCHEDULE_URL, body);
    }

    public HttpResponse deleteJobSchedule(String jobScheduleId) {
        return HttpOperations.delete(JOB_SCHEDULE_URL.concat("/").concat(jobScheduleId));
    }

    public HttpResponse getJobSchedules() {
        return HttpOperations.get(JOB_SCHEDULE_URL);
    }

    public HttpResponse getJobSchedules(final String filter) {
        return HttpOperations.get(JOB_SCHEDULE_URL.concat("?").concat(filter));
    }

    public HttpResponse getJobSchedule(final String jobScheduleId) {
        return HttpOperations.get(JOB_SCHEDULE_URL.concat("/").concat(jobScheduleId));
    }

    public HttpResponse enableJobSchedule(final String jobScheduleId, final boolean enabled) {
        final Map<String, Object> body = new HashMap<>();
        body.put("enabled", enabled);
        return HttpOperations.patch(JOB_SCHEDULE_URL.concat("/").concat(jobScheduleId), body);
    }

    public HttpResponse getScheduledJobs(final String jobScheduleId) {
        final String query = "?filters=jobScheduleId==" + jobScheduleId;
        return HttpOperations.get(JOB_URL.concat(query));
    }

    public HttpResponse getScheduledJobs(final String jobScheduleId, final List<String> statuses) {
        final String statusQuery = statuses.stream().map(s -> "status==" + s).collect(Collectors.joining(","));
        final String query = "?filters=jobScheduleId==" + jobScheduleId + ";(" + statusQuery + ")";
        return HttpOperations.get(JOB_URL.concat(query));
    }

    public HttpResponse createMessageSubscription(final String featurePackName, final String listenerName,
                                              final MessageSubscription messageSubscription) {
        final String url = String.format(MESSAGE_SUBSCRIPTIONS_URL, featurePackName, listenerName);
        return HttpOperations.post(url, messageSubscription);
    }

    public HttpResponse getMessageSubscriptions(final String featurePackName, final String listenerName) {
        final String url = String.format(MESSAGE_SUBSCRIPTIONS_URL, featurePackName, listenerName);
        return HttpOperations.get(url);
    }

    public HttpResponse deleteMessageSubscription(final String featurePackName, final String listenerName,
                                           final String messageSubscriptionId) {
        final String url = String.format(MESSAGE_SUBSCRIPTIONS_URL, featurePackName, listenerName)
                .concat("/").concat(messageSubscriptionId);
        return HttpOperations.delete(url);
    }
}