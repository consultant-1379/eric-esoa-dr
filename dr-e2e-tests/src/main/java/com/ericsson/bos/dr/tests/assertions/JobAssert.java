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

import com.ericsson.bos.dr.tests.clients.dr.Job;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class JobAssert extends AbstractAssert<JobAssert, Job> {

    public JobAssert(Job job) {
        super(job, JobAssert.class);
    }

    public static JobAssert assertThat(Job actual) {
        return new JobAssert(actual);
    }

    public JobAssert hasDiscoveredObjectsCount(final int count) {
        Assertions.assertThat(actual.getDiscoveredObjectsCount()).as("Check Discovered objects count").isEqualTo(count);
        return this;
    }

    public JobAssert hasReconciledObjectsCount(final int count) {
        Assertions.assertThat(actual.getReconciledObjectsCount()).as("Check Reconciled objects count").isEqualTo(count);
        return this;
    }

    public JobAssert hasReconciledObjectsErrorCount(final int count) {
        Assertions.assertThat(actual.getReconciledObjectsErrorCount()).as("Check Reconciled objects error count").isEqualTo(count);
        return this;
    }

    public JobAssert isDiscovered() {
        Assertions.assertThat(actual.getStatus()).as("Check Discovery completed").isEqualTo("DISCOVERED");
        return this;
    }


    public JobAssert isDiscoveryFailed() {
        Assertions.assertThat(actual.getStatus()).as("Check Discovery failed").isEqualTo("DISCOVERY_FAILED");
        return this;
    }

    public JobAssert isCompleted() {
        Assertions.assertThat(actual.getStatus()).as("Check job is Completed").isEqualTo("COMPLETED");
        return this;
    }

    public JobAssert hasJobScheduleIdEquals(final String jobScheduleId) {
        Assertions.assertThat(actual.getJobScheduleId()).as("Check job schedule id").isEqualTo(jobScheduleId);
        return this;
    }
}