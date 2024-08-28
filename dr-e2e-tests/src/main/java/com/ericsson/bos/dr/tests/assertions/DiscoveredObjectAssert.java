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

import com.ericsson.bos.dr.tests.clients.dr.DiscoveredObject;
import com.ericsson.bos.dr.tests.clients.dr.Filter;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class DiscoveredObjectAssert extends AbstractAssert<DiscoveredObjectAssert, DiscoveredObject> {

    public DiscoveredObjectAssert(DiscoveredObject discoveredObject) {
        super(discoveredObject, DiscoveredObjectAssert.class);
    }

    public static DiscoveredObjectAssert assertThat(DiscoveredObject actual) {
        return new DiscoveredObjectAssert(actual);
    }

    public DiscoveredObjectAssert isDiscovered() {
        Assertions.assertThat(actual.getStatus())
                .as("Check Discovered Object state is Completed")
                .isEqualTo("DISCOVERED");
        return this;
    }

    public DiscoveredObjectAssert isReconciled() {
        Assertions.assertThat(actual.getStatus())
                .as("Check Discovered Object state is Reconciled")
                .isEqualTo("RECONCILED");
        return this;
    }

    public DiscoveredObjectAssert hasAllActionsCompleted() {
        for (final Filter filter: actual.getFilters()) {
            final Filter.ReconcileAction reconcileAction = filter.getReconcileAction();
            Assertions.assertThat(reconcileAction.getStatus())
                    .as(String.format("Check action %s is completed for filter %s", reconcileAction.getName(), filter.getName()))
                    .isEqualTo("COMPLETED");
            Assertions.assertThat(reconcileAction.getCommand())
                    .as(String.format("Check command for action %s is not empty", reconcileAction.getName()))
                    .isNotEmpty();
            Assertions.assertThat(reconcileAction.getCommandOutput())
                    .as(String.format("Check command output for action %s is not empty", reconcileAction.getName()))
                    .isNotEmpty();
        }
        return this;
    }
}