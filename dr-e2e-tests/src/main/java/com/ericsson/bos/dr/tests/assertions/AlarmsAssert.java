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
package com.ericsson.bos.dr.tests.assertions;

import java.util.List;

import com.ericsson.bos.dr.tests.clients.alarms.Alarm;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;

public class AlarmsAssert extends AbstractAssert<AlarmsAssert, List<Alarm>> {

    private static final String SERVICE_NAME = "eric-esoa-dr-service";
    private static final String JOB_FAILED_ALARM_NAME = "DRServiceJobFailed";
    private static final String JOB_FAILED_FAULTY_RESOURCE_PREFIX = "/discovery-and-reconciliation/v1/jobs/";

    public AlarmsAssert(List<Alarm> alarms) {
        super(alarms, AlarmsAssert.class);
    }

    public static AlarmsAssert assertThat(List<Alarm> actual) {
        return new AlarmsAssert(actual);
    }

    public AlarmsAssert hasJobFailedAlarm(final String jobId) {
        final String faultyResource = JOB_FAILED_FAULTY_RESOURCE_PREFIX.concat(jobId);
        final Condition<Alarm> condition = new Condition<>(a -> a.alarmName().equals(JOB_FAILED_ALARM_NAME) &&
                a.faultyResource().contains(faultyResource) &&
                a.serviceName().equals(SERVICE_NAME), "DRServiceJobFailed Alarm for job " + jobId);
        Assertions.assertThat(actual).as("Check Alarm list contains job failed alarm")
                .haveExactly(1, condition);
        return this;
    }
}