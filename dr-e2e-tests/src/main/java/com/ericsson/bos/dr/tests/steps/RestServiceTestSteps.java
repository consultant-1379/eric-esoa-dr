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
package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;

import com.ericsson.bos.dr.tests.clients.restservice.RestServiceClient;
import com.ericsson.bos.dr.tests.utils.ResourceUtils;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.utils.YamlUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class RestServiceTestSteps {

    private final RestServiceClient restServiceClient = new RestServiceClient();

    @Step("Upload REST resources")
    public void uploadResources() {
        LOGGER.info("Uploading resources defined in rest-resources dir");
        ResourceUtils.walkClasspathDir("/rest-resources", (filename, contents) -> {
            LOGGER.info("Uploading resource from file {}", filename);
            final HttpResponse response = restServiceClient.uploadResource("test", contents);
            LOGGER.info("Upload resource response: {}", response);
            Allure.attachment(String.format("Upload Resource %s response", filename), response.toString());
            assertThat(response.code()).as("Rest resource failed to be created").isIn(201, 409);
        });
    }

    @Step("Delete REST resources")
    public void deleteResources() {
        LOGGER.info("Deleting resources defined in rest-resources dir");
        ResourceUtils.walkClasspathDir("/rest-resources", (filename, contents) -> {
            LOGGER.info("Deleting resource from file {}", filename);
            final String resourceName = YamlUtils.read(contents, Map.class).get("name").toString();
            final HttpResponse response = restServiceClient.deleteResource(resourceName);
            LOGGER.info("Delete resource response: {}", response);
            Allure.attachment(String.format("Delete Resource %s response", filename), response.toString());
        });
    }
}