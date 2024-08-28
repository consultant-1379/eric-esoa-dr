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

import com.ericsson.bos.dr.tests.clients.dr.JobSchedule;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class JobScheduleAssert extends AbstractAssert<JobScheduleAssert, JobSchedule> {

    public JobScheduleAssert(JobSchedule jobSchedule) {
        super(jobSchedule, JobScheduleAssert.class);
    }

    public static JobScheduleAssert assertThat(JobSchedule actual) {
        return new JobScheduleAssert(actual);
    }

    public JobScheduleAssert hasPropertiesSet() {
        Assertions.assertThat(actual.getId()).as("Check id is set").isNotEmpty();
        Assertions.assertThat(actual.getName()).as("Check name is set").isNotEmpty();
        Assertions.assertThat(actual.getExpression()).as("Check expression is set").isNotEmpty();
        Assertions.assertThat(actual.getDescription()).as("Check description is set").isNotEmpty();
        Assertions.assertThat(actual.getCreatedAt()).as("Check createdAt is set").isNotEmpty();
        Assertions.assertThat(actual.getEnabled()).as("Check enabled is set").isTrue();
        Assertions.assertThat(actual.getJobSpecification().getName()).as("Check job name is set").isNotEmpty();
        Assertions.assertThat(actual.getJobSpecification().getFeaturePackId()).as("Check feature pack id is set").isNotEmpty();
        Assertions.assertThat(actual.getJobSpecification().getFeaturePackName()).as("Check feature pack name is set").isNotEmpty();
        Assertions.assertThat(actual.getJobSpecification().getApplicationId()).as("Check application id is set").isNotEmpty();
        Assertions.assertThat(actual.getJobSpecification().getApplicationName()).as("Check application name is set").isNotEmpty();
        Assertions.assertThat(actual.getJobSpecification().getApplicationJobName()).as("Check application job name is set").isNotEmpty();
        return this;
    }

    public JobScheduleAssert isDisabled() {
        Assertions.assertThat(actual.getEnabled()).as("Check schedule is disabled").isFalse();
        return this;
    }

    public JobScheduleAssert isEnabled() {
        Assertions.assertThat(actual.getEnabled()).as("Check schedule is enabled").isTrue();
        return this;
    }
}