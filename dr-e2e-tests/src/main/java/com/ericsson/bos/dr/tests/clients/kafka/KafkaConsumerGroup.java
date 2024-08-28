package com.ericsson.bos.dr.tests.clients.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KafkaConsumerGroup(@JsonProperty("cluster_id") String clusterId, @JsonProperty("consumer_group_id") String consumerGroupId,
                                 String state) {
}
