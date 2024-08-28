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

import com.ericsson.bos.dr.tests.clients.dr.JobSchedule;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class JobSchedulesAssert extends AbstractAssert<JobSchedulesAssert, List<JobSchedule>> {

    public JobSchedulesAssert(List<JobSchedule> jobSchedules) {
        super(jobSchedules, JobSchedulesAssert.class);
    }

    public static JobSchedulesAssert assertThat(List<JobSchedule> actual) {
        return new JobSchedulesAssert(actual);
    }

    public JobSchedulesAssert hasNamedSchedules(final String[] schedulesNames) {
        final List<String> actualScheduleNames = actual.stream().map(JobSchedule::getName).toList();
        Assertions.assertThat(actualScheduleNames).as("Check Schedule names").contains(schedulesNames);
        return this;
    }
}