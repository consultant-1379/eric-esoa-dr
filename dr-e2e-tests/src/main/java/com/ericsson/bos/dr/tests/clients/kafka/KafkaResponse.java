package com.ericsson.bos.dr.tests.clients.kafka;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KafkaResponse<T> (@JsonProperty("data") List<T> data) {
}
