package com.ericsson.bos.dr.tests.clients.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KafkaError(@JsonProperty("error_code") int errorCode, String message) {
}
