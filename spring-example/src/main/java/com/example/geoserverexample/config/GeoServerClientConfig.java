package com.example.geoserverexample.config;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoServerClientConfig {

    @Value("${geoserver.url}")
    private String url;

    @Value("${geoserver.username}")
    private String username;

    @Value("${geoserver.password}")
    private String password;

    @Bean(destroyMethod = "close")
    public GeoServerClient geoServerClient() {
        return GeoServerClient.builder()
                .url(url)
                .credentials(username, password)
                .defaultFormat(DataFormat.JSON)
                .build();
    }
}
