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
import java.util.stream.Stream;

import com.ericsson.bos.dr.tests.utils.JsonUtils;
import org.testng.annotations.DataProvider;

public class RBACDataProvider {

    @DataProvider(name = "rbacDataProvider")
    public Iterator<RBACData> readDiscoverAndReconcileData() {
        final List<RBACData> drServiceData = JsonUtils.readList(
                this.getClass().getResourceAsStream("/datadriven/rbac_dr_service.json"), RBACData.class);
        final List<RBACData> restServiceData = JsonUtils.readList(
                this.getClass().getResourceAsStream("/datadriven/rbac_rest_service.json"), RBACData.class);
        final List<RBACData> testData = Stream.concat(drServiceData.stream(), restServiceData.stream()).toList();
        return testData.iterator();
    }
}