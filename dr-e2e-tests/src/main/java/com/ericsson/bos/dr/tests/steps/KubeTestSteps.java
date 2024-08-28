package com.ericsson.bos.dr.tests.steps;

import static com.ericsson.bos.dr.tests.env.Environment.LOGGER;
import java.io.IOException;
import java.util.Base64;

import com.ericsson.bos.dr.tests.clients.kubernestes.KubeClient;
import com.ericsson.bos.dr.tests.env.Environment;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Secret;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;

public class KubeTestSteps {

    private static final String CA_CERT_PATH = "/dr-stub-mtls/ca.p12";
    private static final String CLIENT_CERT_PATH = "/dr-stub-mtls/client.p12";
    private static final String TRUSTSTORE_SECRET_PATH = "/dr-stub-mtls/truststore_secret.yaml";
    private static final String KEYSTORE_SECRET_PATH = "/dr-stub-mtls/keystore_secret.yaml";
    private static final String CA_CERT_PLACEHOLDER = "%CACERT%";
    private static final String CLIENT_CERT_PLACEHOLDER = "%CLIENT_CERT%";

    private final KubeClient kubeClient = new KubeClient();

    @Step("Create truststore and keystore secrets for MTLS")
    public void createMtlsSecrets() {
        LOGGER.info("Creating MTLS truststore secret {} in namespace {}", TRUSTSTORE_SECRET_PATH, Environment.NAMESPACE);
        createSecret(readMtlsSecret(TRUSTSTORE_SECRET_PATH, CA_CERT_PATH, CA_CERT_PLACEHOLDER));
        LOGGER.info("Creating MTLS keystore secret {} in namespace {}", KEYSTORE_SECRET_PATH, Environment.NAMESPACE);
        createSecret(readMtlsSecret(KEYSTORE_SECRET_PATH, CLIENT_CERT_PATH, CLIENT_CERT_PLACEHOLDER));
    }

    private void createSecret(final byte[] secret) {
        final V1Secret v1Secret;
        try {
            v1Secret = kubeClient.createSecret(Environment.NAMESPACE, secret);
            Allure.attachment("Created secret", v1Secret.toString());
            LOGGER.info("Created secret: {}", v1Secret.getMetadata().getName());
        } catch (ApiException e) {
            Allure.attachment("Create  Secret error response", e.getResponseBody());
            if (e.getCode() != 409) {
                throw new IllegalStateException("Error creating secret: " + e.getResponseBody());
            } else {
                LOGGER.info("Secret already exists");
            }
        }
    }

    @Step("Delete keystore and truststore secrets")
    public void deleteMtlsSecrets() {
        LOGGER.info("Deleting MTLS truststore secret");
        deleteSecret(readMtlsSecret(TRUSTSTORE_SECRET_PATH, CA_CERT_PATH, CA_CERT_PLACEHOLDER));
        LOGGER.info("Deleting MTLS keystore secret");
        deleteSecret(readMtlsSecret(KEYSTORE_SECRET_PATH, CLIENT_CERT_PATH, CLIENT_CERT_PLACEHOLDER));
    }

    private void deleteSecret(final byte[] secret) {
        try {
            kubeClient.deleteSecret(Environment.NAMESPACE, secret);
        } catch (ApiException e) {
            Allure.attachment("Delete  Secret error response", e.getResponseBody());
            LOGGER.warn("Error deleting secret: {}", e.getResponseBody().getBytes());
        }
    }

    private byte[] readMtlsSecret(final String secretPath, final String certPath, final String certPlaceholder) {
        final String secret;
        try {
            final byte[] cert = this.getClass().getResourceAsStream(certPath).readAllBytes();
            final byte[] secretTemplate = this.getClass().getResourceAsStream(secretPath).readAllBytes();
            secret = new String(secretTemplate)
                    .replace(certPlaceholder, Base64.getEncoder().encodeToString(cert));
            LOGGER.info("Read secret: {}", secret);
            return secret.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Error reading secret: " + secretPath, e);
        }
    }
}