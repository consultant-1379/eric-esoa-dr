package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.ericsson.bos.dr.tests.clients.kafka.KafkaClient;
import com.ericsson.bos.dr.tests.clients.kafka.KafkaConsumerGroup;
import com.ericsson.bos.dr.tests.clients.kafka.KafkaError;
import com.ericsson.bos.dr.tests.clients.kafka.KafkaResponse;
import com.ericsson.bos.dr.tests.utils.JsonUtils;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

public class KafkaTestSteps {

    private final KafkaClient kafkaClient = new KafkaClient();

    @Step("Send message to topic {0}")
    public void sendMessage(final String topic, final Map<String, Object> event) {
        LOGGER.info("Sending message '{}' to topic {}", event, topic);
        final HttpResponse response = kafkaClient.sendMessage(topic, event);
        Allure.attachment("Send Message Response", response.toString());
        Assertions.assertThat(response.code()).as("Send message to topic " + topic).isEqualTo(200);
    }

    @Step("Wait for consumer group {0} to be stable")
    public void awaitConsumerGroupIsStable(final String groupName) {
        LOGGER.info("Waiting for consumer group '{}' state to be stable", groupName);
        try {
            Awaitility.await().pollInSameThread()
                    .timeout(Duration.ofSeconds(30))
                    .pollInterval(Duration.ofSeconds(1))
                    .ignoreExceptions()
                    .catchUncaughtExceptions()
                    .until(() -> listStableConsumerGroups().stream()
                            .anyMatch(cg -> cg.consumerGroupId().equals(groupName)));
        } catch (final ConditionTimeoutException e) {
            LOGGER.warn("Timed out waiting for consumer group {} to exist", groupName);
        }
    }

    private List<KafkaConsumerGroup> listStableConsumerGroups() {
        LOGGER.info("Listing consumer groups");
        final HttpResponse response = kafkaClient.listStableConsumerGroups();
        Allure.attachment("Consume Groups Response", response.toString());
        Assertions.assertThat(response.code()).as("List consumer groups").isEqualTo(200);
        final KafkaResponse<KafkaConsumerGroup> kafkaConsumerGroups = JsonUtils.read(response.body(), KafkaResponse.class,
                KafkaConsumerGroup.class);
        LOGGER.info("Consumer groups: {}", kafkaConsumerGroups.data());
        return kafkaConsumerGroups.data().stream().filter(kcg -> "STABLE".equals(kcg.state())).toList();
    }

    @Step("Create topics {0}")
    public void createTopics(final List<String> topics) {
        topics.forEach(topic -> {
            LOGGER.info("Creating topic {}", topic);
            final HttpResponse response = kafkaClient.createTopic(topic);
            Allure.attachment(String.format("Create topic %s response", topic), response.toString());
            if (response.isSuccessful()) {
                LOGGER.info("Created topic {}", topic);
            } else {
                final KafkaError kafkaError = JsonUtils.read(response.body(), KafkaError.class);
                if (kafkaError.errorCode() != 40002) {
                    throw new AssertionError(String.format("Expected topic to be created or already existing but got error - %s", kafkaError));
                }
                LOGGER.info("Topic {} already exists", topic);
            }
        });
    }
}