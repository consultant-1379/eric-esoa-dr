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
package com.ericsson.bos.dr.tests.datadriven;

import java.util.Iterator;
import java.util.List;

import com.ericsson.bos.dr.tests.utils.JsonUtils;
import org.testng.annotations.DataProvider;

public class DiscoverAndReconcileDataProvider {

    @DataProvider(name = "discoverAndReconcileDataProvider")
    public Iterator<DiscoverAndReconcileData> readDiscoverAndReconcileData() {
        final List<DiscoverAndReconcileData> testData = JsonUtils.readList(
                this.getClass().getResourceAsStream("/datadriven/success.json"), DiscoverAndReconcileData.class);
        return testData.iterator();
    }
}