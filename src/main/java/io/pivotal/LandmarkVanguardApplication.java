package io.pivotal;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class LandmarkVanguardApplication {

    private final static Logger logger = LoggerFactory.getLogger(LandmarkVanguardApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(LandmarkVanguardApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (chain, authType) -> true)
                    .build();
            clientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext));
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.warn(e.getMessage(), e);
        }
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(clientBuilder.build());
        return new RestTemplate(requestFactory);
    }
}
