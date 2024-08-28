package com.ericsson.bos.dr.tests.clients.dr;

public record MessageSubscription(String id, String name, String description, String subsystemName, String messageBrokerType,
                                  KafkaConsumerConfiguration messageConsumerConfiguration) {
}
