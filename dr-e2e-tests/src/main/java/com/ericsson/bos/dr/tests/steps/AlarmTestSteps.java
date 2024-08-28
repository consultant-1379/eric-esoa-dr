package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;

import com.ericsson.bos.dr.tests.clients.alarms.Alarm;
import com.ericsson.bos.dr.tests.clients.alarms.AlarmsClient;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;

public class AlarmTestSteps {

    private final AlarmsClient alarmsClient = new AlarmsClient();

    @Step("Get alarms")
    public List<Alarm> getAlarms() {
        LOGGER.info("Get Alarms");
        final HttpResponse response = alarmsClient.getAlarms();
        LOGGER.info("Fetched alarms: {}", response);
        Allure.attachment("Get Alarms Response", response.toString());
        Assertions.assertThat(response.code()).as("Get Alarms response").isEqualTo(200);
        return JsonUtils.readList(response.body(), Alarm.class);
    }

    @Step("Wait until alarm lis contains DRServiceJobFailed Alarm for job {0}")
    public List<Alarm> awaitAlarmListContainsDrServiceJobFailedAlarm(final String jobId) {
        Awaitility.await().pollInSameThread()
                .timeout(Duration.ofSeconds(15))
                .ignoreExceptions()
                .catchUncaughtExceptions()
                .until(() -> getAlarms().stream().anyMatch(isDrServiceJobFailedAlarm(jobId)));
        return getAlarms();
    }

    private Predicate<Alarm> isDrServiceJobFailedAlarm(final String jobId) {
        return alarm -> alarm.alarmName().equals("DRServiceJobFailed") &&
                alarm.faultyResource().contains(jobId);
    }
}