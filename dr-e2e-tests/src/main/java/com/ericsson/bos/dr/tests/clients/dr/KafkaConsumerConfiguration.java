package com.ericsson.bos.dr.tests.clients.dr;

import java.util.List;

public record KafkaConsumerConfiguration(String groupId, List<String> topicNames) {
}
