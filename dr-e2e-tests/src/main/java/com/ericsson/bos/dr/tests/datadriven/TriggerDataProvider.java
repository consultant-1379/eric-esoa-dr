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

public class TriggerDataProvider {

    @DataProvider(name = "triggerDataProvider")
    public Iterator<TriggerData> readTriggerData() {
        final List<TriggerData> testData = JsonUtils.readList(
                this.getClass().getResourceAsStream("/datadriven/trigger.json"), TriggerData.class);
        return testData.iterator();
    }

    @DataProvider(name = "kafkaTriggerDataProvider")
    public Iterator<KafkaTriggerData> readKafkaTriggerData() {
        final List<KafkaTriggerData> testData = JsonUtils.readList(
                this.getClass().getResourceAsStream("/datadriven/kafka_trigger.json"), KafkaTriggerData.class);
        return testData.iterator();
    }
}