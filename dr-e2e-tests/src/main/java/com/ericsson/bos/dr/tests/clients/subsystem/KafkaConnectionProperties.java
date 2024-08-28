package com.ericsson.bos.dr.tests.clients.subsystem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaConnectionProperties {

    @JsonProperty("bootstrapServer")
    private String bootstrapServer;
    @JsonProperty("ssl.enabled")
    private boolean sslEnabled;
    @JsonProperty("ssl.trustStoreSecretName")
    private String trustStoreSecretName;
    @JsonProperty("ssl.trustStorePassword")
    private String trustStorePassword;
    @JsonProperty("ssl.keyStoreSecretName")
    private String keyStoreSecretName;
    @JsonProperty("ssl.keyStorePassword")
    private String keyStorePassword;
    @JsonProperty("ssl.keyPassword")
    private String keyPassword;

    public String getBootstrapServer() {
        return bootstrapServer;
    }

    public void setBootstrapServer(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }

    public String getTrustStoreSecretName() {
        return trustStoreSecretName;
    }

    public void setTrustStoreSecretName(String trustStoreSecretName) {
        this.trustStoreSecretName = trustStoreSecretName;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getKeyStoreSecretName() {
        return keyStoreSecretName;
    }

    public void setKeyStoreSecretName(String keyStoreSecretName) {
        this.keyStoreSecretName = keyStoreSecretName;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
}
