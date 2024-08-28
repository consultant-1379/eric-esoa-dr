package com.ericsson.bos.dr.tests.clients.kafka;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.bos.dr.tests.clients.okhttp3.HttpOperations;
import com.ericsson.bos.dr.tests.clients.okhttp3.HttpResponse;
import com.ericsson.bos.dr.tests.env.Environment;
import com.ericsson.bos.dr.tests.utils.JsonUtils;

public class KafkaClient {

    private static final String CLUSTER_V3_URL = "/v3/clusters";
    private static final String CONSUMER_GROUPS_V3_URL = CLUSTER_V3_URL.concat("/%s/consumer-groups");
    private static final String TOPICS_V3_URL = CLUSTER_V3_URL.concat("/%s/topics");
    private static final String TOPICS_URL = "/topics";

    public HttpResponse listStableConsumerGroups() {
        final String clusterId = getClusterId();
        return HttpOperations.get(Environment.KAFKA_HOST.concat(String.format(CONSUMER_GROUPS_V3_URL, clusterId)));
    }

    public HttpResponse sendMessage(final String topic, final Map<String, Object> event) {
        final String url = Environment.KAFKA_HOST.concat(TOPICS_URL).concat("/").concat(topic);
        final Map<String, Object> body = new HashMap<>();
        body.put("records", Collections.singletonList(Collections.singletonMap("value", event)));
        return HttpOperations.post(url, body, "application/vnd.kafka.json.v2+json");
    }

    public HttpResponse createTopic(final String topic) {
        final String clusterId = getClusterId();
        final String url = Environment.KAFKA_HOST.concat(String.format(TOPICS_V3_URL, clusterId));
        final Map<String, String> body = Collections.singletonMap("topic_name", topic);
        return HttpOperations.post(url, body);
    }

    private String getClusterId() {
        final HttpResponse getClustersResponse = HttpOperations.get(Environment.KAFKA_HOST.concat(CLUSTER_V3_URL));
        final KafkaResponse<KafkaCluster> kafkaResponse = JsonUtils.read(getClustersResponse.body(), KafkaResponse.class, KafkaCluster.class);
        return kafkaResponse.data().get(0).clusterId();
    }
}