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

package com.ericsson.bos.dr.tests.clients.kubernestes;

import java.io.FileReader;
import java.io.IOException;

import com.ericsson.bos.dr.tests.env.Environment;
import com.ericsson.bos.dr.tests.utils.YamlUtils;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Secret;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import org.apache.commons.lang.StringUtils;

public class KubeClient {

    static {
        if (StringUtils.isNotEmpty(Environment.KUBECONFIG)) {
            try {
                final ApiClient client = ClientBuilder.kubeconfig(
                        KubeConfig.loadKubeConfig(new FileReader(Environment.KUBECONFIG))).build();
                Configuration.setDefaultApiClient(client);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public V1Secret createSecret(final String namespace, final byte[] secretYaml) throws ApiException {
        final V1Secret v1Secret = YamlUtils.read(secretYaml, V1Secret.class);
        return new CoreV1Api().createNamespacedSecret(
                namespace, v1Secret, null, null, null, null);
    }

    public void deleteSecret(final String namespace, final byte[] secretYaml) throws ApiException {
        final V1Secret v1Secret = YamlUtils.read(secretYaml, V1Secret.class);
        new CoreV1Api().deleteNamespacedSecret(v1Secret.getMetadata().getName(), namespace, null, null, null, null, null, null);
    }

    public byte[] getSecretData(final String namespace, final String secretName, final String dataKey) throws ApiException {
        final V1Secret secret = new CoreV1Api().readNamespacedSecret(secretName, namespace, null);
        if (secret.getData().containsKey(dataKey)) {
            return secret.getData().get(dataKey);
        }
        throw new IllegalArgumentException("No data with key " + dataKey + " found in secret " + secretName);
    }
}