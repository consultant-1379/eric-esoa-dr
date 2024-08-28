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
package com.ericsson.bos.dr.tests.clients.okhttp3;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.ericsson.bos.dr.tests.env.Environment;
import okhttp3.OkHttpClient;
import org.apache.commons.lang.StringUtils;

public class HttpClientConfiguration {

    public OkHttpClient createHttpClient() {
        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new AuthenticationInterceptor())
                .hostnameVerifier((hostname, session) -> true);
        configureSocks5Proxy().ifPresent(httpClientBuilder::proxy);
        try {
            final TrustManager trustManager = createTrustManager();
            final SSLContext sslContext = configureTrustAllSsl(trustManager);
            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManager);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new SecurityException("Error configuring ssl", e);
        }
        return httpClientBuilder.build();
    }

    private Optional<Proxy> configureSocks5Proxy() {
        if (StringUtils.isNotEmpty(Environment.SOCK5_PROXY)) {
            final String[] proxyParts = Environment.SOCK5_PROXY.split(":");
            final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyParts[0], Integer.valueOf(proxyParts[1])));
            return Optional.of(proxy);
        }
        return Optional.empty();
    }

    private SSLContext configureTrustAllSsl(TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());
        return sslContext;
    }

    private TrustManager createTrustManager() {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        // do nothing
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        // do nothing
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        return trustAllCerts[0];
    }
}