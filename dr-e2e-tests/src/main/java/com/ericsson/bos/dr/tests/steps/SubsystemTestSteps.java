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
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.ericsson.bos.dr.tests.clients.subsystem.Subsystem;
import com.ericsson.bos.dr.tests.clients.subsystem.SubsystemClient;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import com.ericsson.bos.dr.tests.utils.ResourceUtils;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class SubsystemTestSteps {

    private static final String[] SUBSYSTEM_TYPES = {"REST", "KAFKA"};

    private final SubsystemClient subsystemClient = new SubsystemClient();

    @Step("Create REST and KAFKA subsystem types")
    public void createSubsystemTypes() {
        Arrays.stream(SUBSYSTEM_TYPES).forEach(type -> {
            LOGGER.info("Create {} subsystem type", type);
            HttpResponse response = subsystemClient.createSubsystemType(type);
            LOGGER.info("Create {} subsystem type response: {}", type, response);
            Allure.attachment(String.format("Create Subsystem Type  %s response", type), response.toString());
            assertThat(response.code()).as("Check subsystem type is created").isIn(201, 409);
        });
    }

    @Step("Create Subsystems")
    public void createSubsystems() {
        LOGGER.info("Create subsystems defined in subsystems resources dir");
        ResourceUtils.walkClasspathDir("/subsystems", (filename, content) -> {
            LOGGER.info("Create subsystem from file {}", filename);
            final Map<String, Object> subsystem = JsonUtils.read(content, Map.class);
            final HttpResponse response = subsystemClient.createSubsystem(subsystem);
            LOGGER.info("Create subsystem response: {}", response);
            Allure.attachment(String.format("Create Subsystem %s response", filename), response.toString());
            assertThat(response.code()).as("Check subsystem is created").isIn(201, 409);
        });
    }

    @Step("Delete Subsystems")
    public void deleteSubsystems() {
        LOGGER.info("Delete subsystems defined in subsystems resources dir");
        final HttpResponse response = subsystemClient.getSubsystems();
        LOGGER.info("Get subsystems response: {}", response);
        if (!response.isSuccessful()) {
            LOGGER.warn("Error fetching subsystems. Skip cleanup.");
            return;
        }
        final Map<String, Long> subsystemIdsByName = JsonUtils.readList(response.body(), Subsystem.class).stream()
                        .collect(Collectors.toMap(Subsystem::getName, Subsystem::getId));
        ResourceUtils.walkClasspathDir("/subsystems", (filename, content) -> {
            LOGGER.info("Delete subsystem from file {},  if existing", filename);
            final Subsystem subsystem = JsonUtils.read(content, Subsystem.class);
            if (subsystemIdsByName.containsKey(subsystem.getName())) {
                deleteSubsystem(subsystemIdsByName.get(subsystem.getName()));
            }
        });
    }

    @Step("Delete Subsystem {0}")
    public void deleteSubsystem(final long id) {
        final HttpResponse deleteResponse = subsystemClient.deleteSubsystem(id);
        LOGGER.info("Delete subsystem response: {}", deleteResponse);
        Allure.attachment(String.format("Delete Subsystem %s response", id), deleteResponse.toString());
    }
}