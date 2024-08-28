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
package com.ericsson.bos.dr.tests;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.ericsson.bos.dr.tests.assertions.AlarmsAssert;
import com.ericsson.bos.dr.tests.assertions.DiscoveredObjectAssert;
import com.ericsson.bos.dr.tests.assertions.JobAssert;
import com.ericsson.bos.dr.tests.assertions.JobScheduleAssert;
import com.ericsson.bos.dr.tests.assertions.JobSchedulesAssert;
import com.ericsson.bos.dr.tests.clients.alarms.Alarm;
import com.ericsson.bos.dr.tests.clients.dr.DiscoveredObject;
import com.ericsson.bos.dr.tests.clients.dr.FeaturePack;
import com.ericsson.bos.dr.tests.clients.dr.Job;
import com.ericsson.bos.dr.tests.clients.dr.JobSchedule;
import com.ericsson.bos.dr.tests.clients.dr.JobSchedules;
import com.ericsson.bos.dr.tests.clients.dr.Jobs;
import com.ericsson.bos.dr.tests.clients.dr.MessageSubscriptions;
import com.ericsson.bos.dr.tests.clients.dr.TriggerResult;
import com.ericsson.bos.dr.tests.datadriven.DiscoverAndReconcileData;
import com.ericsson.bos.dr.tests.datadriven.DiscoverAndReconcileDataProvider;
import com.ericsson.bos.dr.tests.datadriven.KafkaTriggerData;
import com.ericsson.bos.dr.tests.datadriven.ScheduleData;
import com.ericsson.bos.dr.tests.datadriven.TriggerData;
import com.ericsson.bos.dr.tests.datadriven.TriggerDataProvider;
import com.ericsson.bos.dr.tests.env.Environment;
import com.ericsson.bos.dr.tests.steps.AlarmTestSteps;
import com.ericsson.bos.dr.tests.steps.DrTestSteps;
import com.ericsson.bos.dr.tests.steps.KafkaTestSteps;
import com.ericsson.bos.dr.tests.steps.KeycloakTestSteps;
import com.ericsson.bos.dr.tests.steps.KubeTestSteps;
import com.ericsson.bos.dr.tests.steps.RestServiceTestSteps;
import com.ericsson.bos.dr.tests.steps.SubsystemTestSteps;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test end to end Discovery and Reconciliation flow.
 */
public class DrTest {

    private static final String FP_NAME_PREFIX = "FT-fp-";

    private final SubsystemTestSteps subsystemTestSteps = new SubsystemTestSteps();
    private final DrTestSteps drTestSteps = new DrTestSteps();
    private final RestServiceTestSteps restServiceTestSteps = new RestServiceTestSteps();
    private final KeycloakTestSteps keycloakTestSteps = new KeycloakTestSteps();
    private final KubeTestSteps kubeTestSteps = new KubeTestSteps();
    private final KafkaTestSteps kafkaTestSteps = new KafkaTestSteps();
    private final AlarmTestSteps alarmTestSteps = new AlarmTestSteps();

    @BeforeClass
    public void setup() {
        if (StringUtils.isNotEmpty(Environment.IAM_HOST)) {
            keycloakTestSteps.createDrTestUser();
            keycloakTestSteps.loginWithDrTestUser();
        }
        subsystemTestSteps.createSubsystemTypes();
        subsystemTestSteps.createSubsystems();
        restServiceTestSteps.uploadResources();
        if (Environment.MTLS_ENABLED) {
            kubeTestSteps.createMtlsSecrets();
        }
    }

    @AfterClass
    public void cleanup() {
        if (!Environment.SKIP_CLEANUP) {
            subsystemTestSteps.deleteSubsystems();
            restServiceTestSteps.deleteResources();
            drTestSteps.deleteFeaturePacks(FP_NAME_PREFIX);
//            if (StringUtils.isNotEmpty(Environment.IAM_HOST)) {
//                keycloakTestSteps.deleteDrTestUser();
//            }
            if (Environment.MTLS_ENABLED) {
                kubeTestSteps.deleteMtlsSecrets();
            }
        }
    }

    @Description("This tests successful execution Discovery and Reconciliation jobs.")
    @Feature("Jobs")
    @Test(dataProvider = "discoverAndReconcileDataProvider", dataProviderClass = DiscoverAndReconcileDataProvider.class)
    public void discoverAndReconcileAllCompletesSuccessfully(DiscoverAndReconcileData data) {
        if (data.isRequiresMtlsEnabled() && !Environment.MTLS_ENABLED) {
            throw new SkipException("Test requires MTLS but not enabled.");
        }
        final String fpName = FP_NAME_PREFIX + System.currentTimeMillis();
        drTestSteps.uploadFeaturePack(fpName, data.getDescription(), data.getFeaturePack());

        final String jobName = fpName + "-job";
        final String jobId = drTestSteps.executeDiscovery(jobName, fpName, data.getApp(), data.getJob(), data.getInputs());
        final Job job = drTestSteps.getJob(jobId);
        JobAssert.assertThat(job).isDiscovered().hasDiscoveredObjectsCount(data.getExpectedDiscoveryCount());

        final List<DiscoveredObject> discoveredObjectsAfterDiscovery = drTestSteps.getDiscoveredObjects(jobId);
        discoveredObjectsAfterDiscovery.forEach(object -> DiscoveredObjectAssert.assertThat(object).isDiscovered());

        drTestSteps.executeReconcile(jobId, Collections.emptyMap());
        final List<DiscoveredObject> discoveredObjectsAfterReconcile = drTestSteps.getDiscoveredObjects(jobId);
        discoveredObjectsAfterReconcile.forEach(object -> DiscoveredObjectAssert.assertThat(object).isReconciled().hasAllActionsCompleted());

        final Job jobAfterReconcile = drTestSteps.getJob(jobId);
        JobAssert.assertThat(jobAfterReconcile).isCompleted().hasReconciledObjectsCount(data.getExpectedReconcileCount())
                .hasReconciledObjectsErrorCount(0);
    }

    @Description("This tests successful discovery and auto-reconciliation triggered by a listener.")
    @Test(dataProvider = "triggerDataProvider", dataProviderClass = TriggerDataProvider.class)
    @Feature("Listeners")
    public void triggerDiscoveryAndReconcileCompletesSuccessfully(TriggerData data) {
        final String fpName = FP_NAME_PREFIX + System.currentTimeMillis();
        drTestSteps.uploadFeaturePack(fpName, "", data.getFeaturePack());

        final TriggerResult triggerResult = drTestSteps.triggerDiscoveryAndReconciliation(fpName, data.getListener(), data.getEvent());
        JobAssert.assertThat(triggerResult.getJob()).isCompleted()
                .hasDiscoveredObjectsCount(data.getExpectedDiscoveryCount())
                .hasReconciledObjectsCount(data.getExpectedReconcileCount())
                .hasReconciledObjectsErrorCount(0);
    }

    /**
     * Test create, delete, get, enable and execution of job schedules.
     * <p>
     * The job schedules are configured to execute every 3 seconds to enable the test to execute speedily. This
     * requires that the job acquisition interval in the target environment is configured to 3 seconds rather
     * than the default 15 seconds. The test will execute successfully without changing the interval but will be slower.
     * </p>
     * <ol>
     *     <li>Create Job Schedule for both fp-1 and fp-2.</li>
     *     <li>Get Job Schedules and verify the named schedules exist.</li>
     *     <li>Get each Job Schedule and verify properties are set.</li>
     *     <li>Poll until at least 2 jobs have completed for each of the schedules.</li>
     *     <li>Disable the job schedules and verify enabled is then false.</li>
     *     <li>Enable job schedule for fp-1 and poll until at least one more job is executed.</li>
     *     <li>Delete feature pack fp-1 and verify the associated job schedule is deleted</li>
     *     <li>Delete job schedule for fp-2 and verify it is deleted.</li>
     *     <li>Delete all jobs associated with each of the job schedules and verify they are deleted.</li>
     * </ol>
     */
    @Description("This tests successful execution of job schedules.")
    @Test
    @Feature("Job Schedules")
    public void scheduledJobsExecuteSuccessfully() {
        final ScheduleData schedule1Data = JsonUtils.readClassPathResource("/schedules/schedule1.json", ScheduleData.class);
        final ScheduleData schedule2Data = JsonUtils.readClassPathResource("/schedules/schedule2.json", ScheduleData.class);

        final FeaturePack featurePack1 = drTestSteps.uploadFeaturePack( FP_NAME_PREFIX + "1-" + System.currentTimeMillis(),
                schedule1Data.getDescription(), schedule1Data.getFeaturePack());
        final FeaturePack featurePack2 = drTestSteps.uploadFeaturePack(FP_NAME_PREFIX + "2-" + System.currentTimeMillis(),
                schedule2Data.getDescription(), schedule2Data.getFeaturePack());

        // Create and get job schedules.
        final JobSchedule jobSchedule1 = drTestSteps.createJobSchedule(schedule1Data, featurePack1);
        final JobSchedule jobSchedule2 = drTestSteps.createJobSchedule(schedule2Data, featurePack2);
        final JobSchedules jobSchedules = drTestSteps.getJobSchedules();
        JobSchedulesAssert.assertThat(jobSchedules.getItems()).hasNamedSchedules(new String[] {jobSchedule1.getName(), jobSchedule2.getName()});

        // Get individual schedules and jobs. Check at least 2 jobs completed for each schedule.
        Stream.of(jobSchedule1, jobSchedule2).forEach(js -> {
            final JobSchedule jobSchedule = drTestSteps.getJobSchedule(js.getId());
            JobScheduleAssert.assertThat(jobSchedule).hasPropertiesSet();
            final Jobs executedJobs = drTestSteps.awaitExecutedScheduledJobs(js.getId(), 2, Duration.ofSeconds(60));
            Assertions.assertThat(executedJobs.getTotalCount()).as("Check at least 2 jobs executed").isGreaterThanOrEqualTo(2);
            executedJobs.getItems().forEach(j -> JobAssert.assertThat(j).hasJobScheduleIdEquals(js.getId()));
            executedJobs.getItems().forEach(j -> JobAssert.assertThat(j).isCompleted());
        });

        // Disable schedules.
        jobSchedules.getItems().forEach(js -> {
            drTestSteps.disableJobSchedule(js.getId());
            JobScheduleAssert.assertThat(drTestSteps.getJobSchedule(js.getId())).isDisabled();
        });

        // Re-enable schedule for fp-1 and verify at least two more jobs are executed.
        final int schedule1JobExecutionCount = drTestSteps.getExecutedScheduledJobs(jobSchedule1.getId()).getTotalCount();
        drTestSteps.enableJobSchedule(jobSchedule1.getId());
        JobScheduleAssert.assertThat(drTestSteps.getJobSchedule(jobSchedule1.getId())).isEnabled();
        final Jobs executedJobs = drTestSteps.awaitExecutedScheduledJobs(
                jobSchedule1.getId(), schedule1JobExecutionCount + 2, Duration.ofSeconds(60));
        Assertions.assertThat(executedJobs.getTotalCount()).isGreaterThanOrEqualTo(schedule1JobExecutionCount + 2);

        // Delete schedules and jobs.
        drTestSteps.deleteFeaturePacks(featurePack1.getName());
        Assertions.assertThat(drTestSteps.getNamedJobSchedules(jobSchedule1.getName(), jobSchedule2.getName()).getTotalCount()).isEqualTo(1);
        drTestSteps.deleteJobSchedule(jobSchedule2.getId());
        Assertions.assertThat(drTestSteps.getNamedJobSchedules(jobSchedule1.getName(), jobSchedule2.getName()).getTotalCount()).isEqualTo(0);
        drTestSteps.getScheduledJobs(jobSchedule1.getId()).getItems().forEach(j -> drTestSteps.deleteJob(j.getId(), true));
        Assertions.assertThat(drTestSteps.getScheduledJobs(jobSchedule1.getId()).getTotalCount()).isEqualTo(0);
        drTestSteps.getScheduledJobs(jobSchedule2.getId()).getItems().forEach(j -> drTestSteps.deleteJob(j.getId(), true));
        Assertions.assertThat(drTestSteps.getScheduledJobs(jobSchedule2.getId()).getTotalCount()).isEqualTo(0);
    }

    /**
     * Test automatic triggering of Discovery and Reconciliation jobs using kafka events.
     */
    @Description("This tests successful discovery and reconciliation triggered by a kafka event")
    @Feature("Listeners")
    @Test(dataProvider = "kafkaTriggerDataProvider", dataProviderClass = TriggerDataProvider.class)
    public void triggerDiscoveryAndReconcileUsingKafkaEvents(final KafkaTriggerData data) {
        if (data.getSubsystem().getKafkaConnectionProperties().isSslEnabled() && !Environment.MTLS_ENABLED) {
            throw new SkipException("Test requires MTLS but not enabled.");
        }
        final FeaturePack featurePack1 = drTestSteps.uploadFeaturePack( FP_NAME_PREFIX + "1-" + System.currentTimeMillis(),
                "test kafka events", data.getFeaturePack());
        kafkaTestSteps.createTopics(data.getTopics());

        final String messageSubscriptionId = drTestSteps.createMessageSubscription(featurePack1, data.getListener(),
                data.getSubsystem().getName(), data.getGroupId(), data.getTopics());

        kafkaTestSteps.awaitConsumerGroupIsStable(data.getGroupId());
        kafkaTestSteps.sendMessage(data.getTopics().get(0), data.getEvent());
        kafkaTestSteps.sendMessage(data.getTopics().get(0), data.getEvent());

        final Jobs jobs = drTestSteps.awaitExecutedJobs(featurePack1.getName(), data.getTriggeredJobName(), 2,
                Duration.ofSeconds(30));
        Assertions.assertThat(jobs.getTotalCount()).as("Check job executed for kafka event").isEqualTo(2);

        drTestSteps.deleteMessageSubscription(featurePack1.getName(), data.getListener(), messageSubscriptionId);
        final MessageSubscriptions messageSubscriptions = drTestSteps.getMessageSubscriptions(featurePack1.getName(), "listener_1");
        Assertions.assertThat(messageSubscriptions.totalCount()).isEqualTo(0);
    }

    @Description("This tests that an alarm is generated when a scheduled job fails.")
    @Test
    @Feature("Job Schedules")
    public void alarmGeneratedWhenScheduledJobFails() {
        final ScheduleData scheduleData = JsonUtils.readClassPathResource("/schedules/schedule3.json", ScheduleData.class);
        final FeaturePack featurePack = drTestSteps.uploadFeaturePack(FP_NAME_PREFIX + "1-" + System.currentTimeMillis(),
                scheduleData.getDescription(), scheduleData.getFeaturePack());
        final JobSchedule jobSchedule = drTestSteps.createJobSchedule(scheduleData, featurePack);
        final Jobs executedJobs = drTestSteps.awaitExecutedScheduledJobs(jobSchedule.getId(), 1, Duration.ofSeconds(30));
        Assertions.assertThat(executedJobs.getTotalCount()).as("Check at least 1 job executed").isGreaterThanOrEqualTo(1);
        JobAssert.assertThat(executedJobs.getItems().get(0)).isDiscoveryFailed();

        final String jobId = executedJobs.getItems().get(0).getId();
        final List<Alarm> alarms = alarmTestSteps.awaitAlarmListContainsDrServiceJobFailedAlarm(jobId);
        AlarmsAssert.assertThat(alarms).hasJobFailedAlarm(jobId);
    }

    @Description("This test an alarm is generated when a job triggered by a kafka event fails")
    @Feature("Listeners")
    @Test
    public void alarmGeneratedWhenKafkaTriggeredJobFails() {
        final KafkaTriggerData data = JsonUtils.readClassPathResource("/kafka/kafka_trigger_1.json", KafkaTriggerData.class);
        final FeaturePack featurePack1 = drTestSteps.uploadFeaturePack( FP_NAME_PREFIX + "1-" + System.currentTimeMillis(),
                "test kafka events", data.getFeaturePack());
        kafkaTestSteps.createTopics(data.getTopics());

        final String messageSubscriptionId = drTestSteps.createMessageSubscription(featurePack1, data.getListener(),
                data.getSubsystem().getName(), data.getGroupId(), data.getTopics());

        kafkaTestSteps.awaitConsumerGroupIsStable(data.getGroupId());
        kafkaTestSteps.sendMessage(data.getTopics().get(0), data.getEvent());

        final Jobs jobs = drTestSteps.awaitExecutedJobs(
                featurePack1.getName(), data.getTriggeredJobName(), 1, Duration.ofSeconds(30));
        JobAssert.assertThat(jobs.getItems().get(0)).isDiscoveryFailed();

        final String jobId = jobs.getItems().get(0).getId();
        final List<Alarm> alarms = alarmTestSteps.awaitAlarmListContainsDrServiceJobFailedAlarm(jobId);
        AlarmsAssert.assertThat(alarms).hasJobFailedAlarm(jobId);

        drTestSteps.deleteMessageSubscription(featurePack1.getName(), data.getListener(), messageSubscriptionId);
    }
}