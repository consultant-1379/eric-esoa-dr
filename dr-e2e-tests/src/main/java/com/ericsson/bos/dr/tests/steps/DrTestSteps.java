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
package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.clients.dr.Job.COMPLETED_JOB_STATES;
import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ericsson.bos.dr.tests.clients.dr.DiscoveredObject;
import com.ericsson.bos.dr.tests.clients.dr.DiscoveredObjects;
import com.ericsson.bos.dr.tests.clients.dr.DrClient;
import com.ericsson.bos.dr.tests.clients.dr.FeaturePack;
import com.ericsson.bos.dr.tests.clients.dr.FeaturePacks;
import com.ericsson.bos.dr.tests.clients.dr.Job;
import com.ericsson.bos.dr.tests.clients.dr.JobSchedule;
import com.ericsson.bos.dr.tests.clients.dr.JobSchedules;
import com.ericsson.bos.dr.tests.clients.dr.Jobs;
import com.ericsson.bos.dr.tests.clients.dr.KafkaConsumerConfiguration;
import com.ericsson.bos.dr.tests.clients.dr.MessageSubscription;
import com.ericsson.bos.dr.tests.clients.dr.MessageSubscriptions;
import com.ericsson.bos.dr.tests.clients.dr.TriggerResult;
import com.ericsson.bos.dr.tests.datadriven.ScheduleData;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

public class DrTestSteps {

    private final DrClient drClient = new DrClient();
    private final FeaturePackArchiver featurePackArchiver = new FeaturePackArchiver();

    @Step("Upload Feature Pack {0}")
    public FeaturePack uploadFeaturePack(final String name, final String description, final String featurePackDir) {
        LOGGER.info("Uploading feature pack: name={}, description={}, path={}", name, description, featurePackDir);
        final String createdArchivePath = featurePackArchiver.createFeaturePackArchive(featurePackDir);
        final HttpResponse response = drClient.uploadFeaturePack(name, description, Paths.get(createdArchivePath).toFile());
        LOGGER.info("Upload feature pack response: {}", response);
        Allure.attachment("Upload Feature Pack Response", response.toString());
        Assertions.assertThat(response.code()).as("Upload feature pack").isEqualTo(201);
        return JsonUtils.read(response.body(), FeaturePack.class);
    }

    @Step("Start Discovery Job {0}")
    public String executeDiscovery(final String jobName, final String featurePackName, final String appName,
                                   final String appJobName, final Map<String, Object> inputs) {
        LOGGER.info("Executing discovery: jobName={}, fpName={}, inputs={}", jobName, featurePackName, inputs);
        final HttpResponse response = drClient.discover(jobName, featurePackName, appName, appJobName, inputs);
        LOGGER.info("Discovery response: {}", response);
        Allure.attachment("Start Discovery Response", response.toString());
        Assertions.assertThat(response.code()).as("Start discovery job").isEqualTo(202);
        final Map<String, String> jsonResponseBody = JsonUtils.read(response.body(), Map.class);
        final String jobId = jsonResponseBody.get("id");
        final Set<String> jobDoneStates = Stream.of("DISCOVERED", "DISCOVERY_FAILED").collect(Collectors.toSet());
        awaitJobInState(jobId, jobDoneStates);
        return jobId;
    }

    @Step("Start Reconcile for job {0}")
    public void executeReconcile(final String jobId, final Map<String, Object> inputs) {
        LOGGER.info("Executing reconcile: jobId={}, inputs={}", jobId, inputs);
        final HttpResponse response = drClient.reconcileAll(jobId, inputs);
        LOGGER.info("Reconcile response: {}", response);
        Allure.attachment("Reconcile Response", response.toString());
        Assertions.assertThat(response.code()).as("Start reconcile all").isEqualTo(202);
        final Set<String> jobDoneStates = Stream.of("COMPLETED", "RECONCILE_FAILED", "PARTIALLY_RECONCILED").collect(Collectors.toSet());
        awaitJobInState(jobId, jobDoneStates);
    }

    @Step("Get Job {0}")
    public Job getJob(final String jobId) {
        LOGGER.info("Get job: id={}", jobId);
        final HttpResponse response = drClient.getJob(jobId);
        LOGGER.info("Fetched job: {}", response);
        Allure.attachment("Get Job  Response", response.toString());
        return JsonUtils.read(response.body(), Job.class);
    }

    @Step("Get Jobs named {0} for Feature Pack {1}")
    public Jobs getJobsByFeaturePackAndName(final String featurePackName, final String jobName) {
        LOGGER.info("Get jobs, featurePack={}, jobName={}", featurePackName, jobName);
        final HttpResponse response = drClient.getJobs(
                String.format("filters=featurePackName==%s;name==%s",featurePackName, jobName));
        LOGGER.info("Fetched jobs: {}", response);
        Allure.attachment("Get Job  Response", response.toString());
        return JsonUtils.read(response.body(), Jobs.class);
    }

    @Step("Wait for {2} jobs to execute for feature pack {0} and job {1}")
    public Jobs awaitExecutedJobs(final String featurePackName, final String jobName,
                                   final int minimumJobCount, final Duration timeout) {
        try {
            Awaitility.await().pollInSameThread()
                    .timeout(timeout)
                    .ignoreExceptions()
                    .catchUncaughtExceptions()
                    .until(() -> getExecutedJobs(jobName, featurePackName).getTotalCount() >= minimumJobCount);
        } catch (final ConditionTimeoutException e) {
            LOGGER.warn("Timed out waiting for {} jobs to complete for feature pack {}", minimumJobCount, featurePackName);
        }
        return getExecutedJobs(jobName, featurePackName);
    }

    @Step("Delete Job {0}")
    public void deleteJob(final String jobId, final boolean force) {
        LOGGER.info("Deleting job: id={}, force={}", jobId, force);
        HttpResponse response = drClient.deleteJob(jobId, force);
        LOGGER.info("Delete job response: {}", response);
        Allure.attachment("Delete Job  Response", response.toString());
        Assertions.assertThat(response.code()).as("Delete job %s", jobId).isEqualTo(204);
    }

    @Step("Get Discovered Objects for job {0}")
    public List<DiscoveredObject> getDiscoveredObjects(final String jobId) {
        LOGGER.info("Get discovered objects: jobId={}", jobId);
        final HttpResponse response = drClient.getDiscoveredObjects(jobId);
        LOGGER.info("Fetched discovered objects: {}", response);
        Allure.attachment("Discovered Objects  Response", response.toString());
        Assertions.assertThat(response.code()).as("Get discovered objects for job %s", jobId).isEqualTo(200);
        return JsonUtils.read(response.body(), DiscoveredObjects.class).getItems();
    }

    @Step("Delete Feature Packs with prefix {0}")
    public void deleteFeaturePacks(final String prefix) {
        LOGGER.info("Deleting feature packs with prefix: {}", prefix);
        final HttpResponse response = drClient.getFeaturePacks();
        LOGGER.info("Get feature packs response: {}", response);
        if (!response.isSuccessful()) {
            LOGGER.warn("Error fetching feature packs. Skip cleanup.");
            return;
        }
        final FeaturePacks featurePacks = JsonUtils.read(response.body(), FeaturePacks.class);
        featurePacks.getItems().stream().filter(fp -> fp.getName().startsWith(prefix))
                .forEach(fp -> {
                    LOGGER.info("Deleting feature pack: {}", fp.getName());
                    HttpResponse deleteResponse = drClient.deleteFeaturePack(fp.getId());
                    LOGGER.info("Delete feature pack response: {}", deleteResponse);
                    Allure.attachment(String.format("Delete Feature Pack  %s Response", fp.getName()), deleteResponse.toString());
                });

    }

    @Step("Trigger Discovery and Reconciliation for feature pack {0} and listener {1}")
    public TriggerResult triggerDiscoveryAndReconciliation(String featurePackName, final String listenerName,
                                                           final Map<String, Object> event) {
        LOGGER.info("Trigger discovery and reconciliation: fpName={}, listener={}, event={}", featurePackName, listenerName, event);
        final HttpResponse response = drClient.trigger(featurePackName, listenerName, event);
        LOGGER.info("Trigger response: {}", response);
        Allure.attachment("Trigger  Response", response.toString());
        Assertions.assertThat(response.code()).as("Trigger discovery and reconcile").isEqualTo(200);
        return JsonUtils.read(response.body(), TriggerResult.class);
    }

    @Step("Create Job Schedule")
    public JobSchedule createJobSchedule(final ScheduleData scheduleData, final FeaturePack featurePack) {
        LOGGER.info("Create Job schedule: name={}, expression={}, featurePack={}", scheduleData.getName(), scheduleData.getExpression(),
                featurePack.getName());
        final Job job = new Job();
        job.setName(scheduleData.getName() + "-job");
        job.setFeaturePackName(featurePack.getName());
        job.setApplicationName(scheduleData.getApp());
        job.setApplicationJobName(scheduleData.getJob());
        job.setInputs(scheduleData.getInputs());
        job.setExecutionOptions(Collections.singletonMap("autoReconcile", scheduleData.isAutoReconcile()));
        final String jobScheduleName = scheduleData.getName() + "-" + System.currentTimeMillis();
        final HttpResponse response = drClient.createJobSchedule(jobScheduleName, scheduleData.getExpression(), job);
        LOGGER.info("Create Job schedule response: {}", response);
        Allure.attachment("Create Schedule Response", response.toString());
        Assertions.assertThat(response.code()).as("Create Job Schedule").isEqualTo(201);
        return JsonUtils.read(response.body(), JobSchedule.class);
    }

    @Step("Create Job Schedule {0}")
    public void deleteJobSchedule(final String jobScheduleId) {
        LOGGER.info("Deleting job schedule: {}", jobScheduleId);
        HttpResponse response = drClient.deleteJobSchedule(jobScheduleId);
        LOGGER.info("Delete job schedule response: {}", response);
        Allure.attachment("Delete Job Schedule  Response", response.toString());
        Assertions.assertThat(response.code()).as("Delete job schedule %s", jobScheduleId).isEqualTo(204);
    }

    @Step("Get Job Schedules")
    public JobSchedules getJobSchedules() {
        LOGGER.info("Get job schedules");
        final HttpResponse response = drClient.getJobSchedules();
        LOGGER.info("Fetched job schedules: {}", response);
        Allure.attachment("Get Job Schedules Response", response.toString());
        return JsonUtils.read(response.body(), JobSchedules.class);
    }

    @Step("Get Job Schedules with names")
    public JobSchedules getNamedJobSchedules(String... names) {
        final String filter = "filters=" + Arrays.stream(names).map(n -> "name==" + n).collect(Collectors.joining(","));
        LOGGER.info("Get named job schedules: {}", filter);
        final HttpResponse response = drClient.getJobSchedules(filter);
        LOGGER.info("Fetched named job schedules: {}", response);
        Allure.attachment("Get Job Schedules  Response", response.toString());
        return JsonUtils.read(response.body(), JobSchedules.class);
    }

    @Step("Get Job Schedule {0}")
    public JobSchedule getJobSchedule(final String jobScheduleId) {
        LOGGER.info("Get job schedule: id={}", jobScheduleId);
        final HttpResponse response = drClient.getJobSchedule(jobScheduleId);
        LOGGER.info("Fetched job schedule: {}", response);
        Allure.attachment("Get Job Schedule  Response", response.toString());
        return JsonUtils.read(response.body(), JobSchedule.class);
    }

    @Step("Enable Job Schedule {0}")
    public void enableJobSchedule(final String jobScheduleId) {
        LOGGER.info("Enable job schedule: id={}", jobScheduleId);
        final HttpResponse response = drClient.enableJobSchedule(jobScheduleId, true);
        LOGGER.info("Enable job schedule response: {}", response);
        Allure.attachment("Enable Job Schedule  Response", response.toString());
        Assertions.assertThat(response.code()).as("Enable job schedule %s", jobScheduleId).isEqualTo(204);
    }

    @Step("Disable Job Schedule {0}")
    public void disableJobSchedule(final String jobScheduleId) {
        LOGGER.info("Disable job schedule: id={}", jobScheduleId);
        final HttpResponse response = drClient.enableJobSchedule(jobScheduleId, false);
        LOGGER.info("Disable job schedule response: {}", response);
        Allure.attachment("Disable Job Schedule Response", response.toString());
        Assertions.assertThat(response.code()).as("Disable job schedule %s", jobScheduleId).isEqualTo(204);
    }

    @Step("Get Jobs for Schedule {0}")
    public Jobs getScheduledJobs(final String jobScheduleId) {
        LOGGER.info("Get jobs for schedule: id={}", jobScheduleId);
        final HttpResponse response = drClient.getScheduledJobs(jobScheduleId);
        LOGGER.info("Fetched scheduled jobs: {}", response);
        Allure.attachment("Get Scheduled Jobs  Response", response.toString());
        return JsonUtils.read(response.body(), Jobs.class);
    }

    @Step("Get executed Jobs for job {0} and feature pack {1}")
    public Jobs getExecutedJobs(final String jobName, final String featurePackName) {
        LOGGER.info("Get executed jobs with name {} for feature pack {}", jobName, featurePackName);
        final String filter = String.format("filters=name==%s;featurePackName==%s;(%s)", jobName, featurePackName,
                COMPLETED_JOB_STATES.stream().map(s -> "status==" + s ).collect(Collectors.joining(",")));
        final HttpResponse response = drClient.getJobs(filter);
        LOGGER.info("Fetched executed jobs: {}", response);
        Allure.attachment("Get Jobs Response", response.toString());
        return JsonUtils.read(response.body(), Jobs.class);
    }

    @Step("Get executed Jobs for  Schedule {0}")
    public Jobs getExecutedScheduledJobs(final String jobScheduleId) {
        LOGGER.info("Get executed jobs for schedule: id={}", jobScheduleId);
        final HttpResponse response = drClient.getScheduledJobs(jobScheduleId, COMPLETED_JOB_STATES);
        LOGGER.info("Fetched executed scheduled jobs: {}", response);
        Allure.attachment("Get Scheduled Jobs Response", response.toString());
        return JsonUtils.read(response.body(), Jobs.class);
    }

    @Step("Create Message Subscription")
    public String createMessageSubscription(final FeaturePack featurePack, final String listenerName,
                                                 final String subsystemName,
                                                 final String groupId,
                                                 final List<String> topicNames) {
        final MessageSubscription messageSubscription = new MessageSubscription(null, featurePack.getName(), listenerName,
                subsystemName, "KAFKA", new KafkaConsumerConfiguration(groupId, topicNames));
        LOGGER.info("Create message subscription: {}", messageSubscription);
        final HttpResponse response = 
                drClient.createMessageSubscription(featurePack.getName(), listenerName, messageSubscription);
        LOGGER.info("Create message subscription response: {}", response);
        Allure.attachment("Create Message Subscription  Response", response.toString());
        Assertions.assertThat(response.code()).as("Create Message Subscription").isEqualTo(201);
        final Map<String, String> jsonResponseBody = JsonUtils.read(response.body(), Map.class);
        return jsonResponseBody.get("id");
    }

    @Step("Get Message Subscriptions")
    public MessageSubscriptions getMessageSubscriptions(final String featurePackName, final String listenerName) {
        LOGGER.info("Get message subscriptions: featurePack={}, listener={}", featurePackName, listenerName);
        final HttpResponse response = drClient.getMessageSubscriptions(featurePackName, listenerName);
        LOGGER.info("Fetched message subscriptions: {}", response);
        Allure.attachment("Get Message Subscriptions  Response", response.toString());
        return JsonUtils.read(response.body(), MessageSubscriptions.class);
    }

    @Step("Delete Message Subscription {2}")
    public void deleteMessageSubscription(final String featurePackName, final String listenerName, final String id) {
        LOGGER.info("Delete message subscription: featurePack={}, listener={}, id={}", featurePackName, listenerName, id);
        final HttpResponse response = drClient.deleteMessageSubscription(featurePackName, listenerName, id);
        LOGGER.info("Delete message subscription response: {}", response);
        Allure.attachment("Delete Message Subscription  Response", response.toString());
        Assertions.assertThat(response.code()).as("Delete message schedule subscription", id).isEqualTo(204);
    }

    @Step("Wait for at least {1} jobs to execute for schedule {0}")
    public Jobs awaitExecutedScheduledJobs(final String jobScheduleId, final int minimumJobCount, final Duration timeout) {
        try {
            Awaitility.await().pollInSameThread()
                    .timeout(timeout)
                    .ignoreExceptions()
                    .catchUncaughtExceptions()
                    .until(() -> getExecutedScheduledJobs(jobScheduleId).getTotalCount() >= minimumJobCount);
        } catch (final ConditionTimeoutException e) {
            LOGGER.warn("Timed out waiting for at least {} scheduled jobs to complete for schedule {}", minimumJobCount, jobScheduleId);
        }
        return getExecutedScheduledJobs(jobScheduleId);
    }

    @Step("Wait for job {0} to complete")
    private void awaitJobInState(final String jobId, final Set<String> jobDoneStates) {
        Awaitility.await().pollInSameThread()
                .until(() -> getJob(jobId), j -> jobDoneStates.contains(j.getStatus()));
    }
}