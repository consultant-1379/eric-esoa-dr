package com.ericsson.bos.dr.tests.env;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.ericsson.bos.dr.tests.clients.kubernestes.KubeClient;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang.StringUtils;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private Environment() {}

    /**
     * The URL to the DAR ingress (eric-esoa-dr-root-ingress), e.g https://dr-anvil-haber003.ews.gic.ericsson.se.
     * Mandatory parameter.
     */
    public static final String DR_HOST;
    /**
     * The URL to the IAM ingress (eric-sec-access-mgmt), e.g https://eric-sec-access-mgmt.anvil-haber003.ews.gic.ericsson.se.
     * If testing in local docker environment then IAM_HOST can be omitted since there will be no authentication.
     */
    public static final String IAM_HOST;
    /**
     * The IAM client secret required for getting an auth token.
     * The value will be automatically resolved if both KUBECONFIG and NAMESPACE variables are supplied, otherwise
     * it needs to be provided.
     * If testing in local docker environment then IAM_CLIENT_SECRET can be omitted since there will be no used authentication.
     */
    public static final String IAM_CLIENT_SECRET;
    /**
     * The IAM admin username required for getting an auth token.
     * The value will be automatically resolved if both KUBECONFIG and NAMESPACE variables are supplied, otherwise
     * it needs to be provided.
     * If testing in local docker environment then IAM_ADMIN_USER can be omitted since there will be no used authentication.
     */
    public static final String IAM_ADMIN_USER;
    /**
     * The IAM admin password required for getting an auth token.
     * The value will be automatically resolved if both KUBECONFIG and NAMESPACE variables are supplied, otherwise
     * it needs to be provided.
     * If testing in local docker environment then IAM_ADMIN_PWD can be omitted since there will be no used authentication.
     */
    public static final String IAM_ADMIN_PWD;
    /**
     * The URL to the rest-service. Only required if it differs from the DR_HOST. Generally this would only be the
     * case when testing locally using docker.
     */
    public static final String REST_SERVICE_HOST;
    /**
     * The URL to the subsystem service. Only required if it differs from the DR_HOST. Generally this would only be the
     * case when testing locally using docker.
     */
    public static final String SUBSYSTEM_HOST;
    /**
     * The URL to the eric-bos-dr-stub-kafka service. Defaults to http://eric-bos-dr-stub-kafka.<DR_HOST_DOMAIN>,
     * e.g http://eric-fh-alarm-handler-rbac-proxy.anvil-haber003.ews.gic.ericsson.se.
     */
    public static final String KAFKA_HOST;
    /**
     * The URL to the eric-fh-alarm-handler service. Defaults to http://eric-fh-alarm-handler-rbac-proxy.<DR_HOST_DOMAIN>,
     * e.g http://eric-bos-dr-stub-kafka.anvil-haber003.ews.gic.ericsson.se.
     */
    public static final String ALARM_HANDLER_HOST;
    /**
     * The sock5 proxy host and port used to access to DAR And IAM hosts, e.g localhost:5005.
     * Generally only used for local testing where direct access if not possible.
     */
    public static final String SOCK5_PROXY;
    /**
     * Execute tests requiring MTLS communication towards dr-stub. If not enabled then these tests are skipped.
     * Requires both KUBECONFIG and NAMESPACE variable are also set in order to create the required cert secrets
     * in the deployment.
     */
    public static final boolean MTLS_ENABLED;
    /**
     * The namespace of the target DAR deployment.
     * Required if executing tests with MTLS towards dr-stub or if the IAM admin user, password and
     * client secret are to be automatically resolved.
     */
    public static final String NAMESPACE;
    /**
     * The path to the kubeconfig file for accessing the target deployment.
     * Required if executing tests with MTLS towards dr-stub or if the IAM admin user, password and
     * client secret are to be automatically resolved.
     */
    public static final String KUBECONFIG;
    /**
     * Optionally skip test cleanup. Cleanup will be performed by default.
     */
    public static final boolean SKIP_CLEANUP;

    public static final String TEST_USER = "dr-user";
    public static final String TEST_USER_PWD = "Ericsson123!";
    public static final List<String> TEST_USER_GROUPS = Collections.unmodifiableList(Stream.of("ESOA_SubsystemAdmin",
            "eric-bos-dr:reader",
            "eric-bos-dr:writer",
            "eric-bos-dr:admin",
            "eric-fh-alarm-handler:reader")
            .toList());

    public static final Logger LOGGER = LoggerFactory.getLogger("DR_TEST");

    static {
        DR_HOST = Objects.requireNonNull(System.getenv("DR_HOST"), "DR_HOST not set");
        IAM_HOST = System.getenv().getOrDefault("IAM_HOST", DR_HOST.replace("bss-oam-gui", "eric-sec-access-mgmt"));
        REST_SERVICE_HOST = System.getenv().getOrDefault("REST_SERVICE_HOST", DR_HOST);
        SUBSYSTEM_HOST = System.getenv().getOrDefault("SUBSYSTEM_HOST", DR_HOST);
        URI.create(DR_HOST).getHost();
        KAFKA_HOST = System.getenv().getOrDefault("KAFKA_HOST",
                "http://eric-bos-dr-stub-kafka." + StringUtils.substringAfter(DR_HOST, "."));
        ALARM_HANDLER_HOST = System.getenv().getOrDefault("ALARM_HANDLER_HOST",
                "https://eric-fh-alarm-handler-rbac-proxy." + StringUtils.substringAfter(DR_HOST, "."));
        NAMESPACE = System.getenv().get("NAMESPACE");
        KUBECONFIG = System.getenv().get("KUBECONFIG");
        IAM_CLIENT_SECRET = Optional.ofNullable(System.getenv("IAM_CLIENT_SECRET"))
                .orElseGet(() -> readSecretData("eric-bss-bam-authn-proxy-client-secret", "authnproxyclientsecret"));
        IAM_ADMIN_USER = Optional.ofNullable(System.getenv("IAM_ADMIN_USER"))
                .orElseGet(() -> readSecretData("eric-sec-access-mgmt-creds", "kcadminid"));
        IAM_ADMIN_PWD = Optional.ofNullable(System.getenv("IAM_ADMIN_PWD"))
                .orElseGet(() -> readSecretData("eric-sec-access-mgmt-creds", "kcpasswd"));
        SOCK5_PROXY = System.getenv().getOrDefault("SOCKS5_PROXY", "");
        MTLS_ENABLED = Boolean.parseBoolean(System.getenv().getOrDefault("MTLS_ENABLED", "false"));
        SKIP_CLEANUP = Boolean.parseBoolean(System.getenv().getOrDefault("SKIP_CLEANUP", "false"));

        logEnvironment();

        Awaitility.setDefaultPollInterval(Duration.ofSeconds(1));
        Awaitility.setDefaultPollDelay(Duration.ZERO);
        Awaitility.setDefaultTimeout(Duration.ofSeconds(60));
    }

    private static String readSecretData(final String secretName, final String dataKey) {
        if (StringUtils.isNotEmpty(KUBECONFIG) && StringUtils.isNotEmpty(NAMESPACE)) {
            try {
                return new String(new KubeClient().getSecretData(NAMESPACE, secretName, dataKey));
            } catch (ApiException e) {
                throw new IllegalStateException("Error reading secret: " + secretName, e);
            }
        }
        return null;
    }

    private static void logEnvironment() {
        final String env = new StringBuilder()
                .append("\nDR_HOST=").append(DR_HOST)
                .append("\nIAM_HOST=").append(IAM_HOST)
                .append("\nKUBECONFIG=").append(KUBECONFIG)
                .append("\nNAMESPACE=").append(NAMESPACE)
                .append("\nIAM_CLIENT_SECRET=").append(IAM_CLIENT_SECRET)
                .append("\nIAM_ADMIN_USER=").append(IAM_ADMIN_USER)
                .append("\nIAM_ADMIN_PWD=").append(IAM_ADMIN_PWD)
                .append("\nREST_SERVICE_HOST=").append(REST_SERVICE_HOST)
                .append("\nSUBSYSTEM_HOST=").append(SUBSYSTEM_HOST)
                .append("\nKAFKA_HOST=").append(KAFKA_HOST)
                .append("\nALARM_HANDLER_HOST=").append(ALARM_HANDLER_HOST)
                .append("\nSOCK5_PROXY=").append(SOCK5_PROXY)
                .append("\nMTLS_ENABLED=").append(MTLS_ENABLED)
                .append("\nSKIP_CLEANUP=").append(SKIP_CLEANUP)
                .toString();
        LOGGER.info("Environment: {}", env);
    }
}