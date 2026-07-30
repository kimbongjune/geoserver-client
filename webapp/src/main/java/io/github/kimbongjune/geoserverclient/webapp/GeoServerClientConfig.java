package io.github.kimbongjune.geoserverclient.webapp;

import io.github.kimbongjune.geoserverclient.GeoServerClient;
import io.github.kimbongjune.geoserverclient.serialization.DataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A single {@link GeoServerClient} shared as a Spring singleton bean — this is exactly the usage
 * pattern the library is designed for (thread-safe, one client per GeoServer endpoint, reused
 * across every request rather than constructed per-call).
 */
@Configuration
public class GeoServerClientConfig {

    @Bean(destroyMethod = "close") // Spring calls close() on shutdown to release pooled connections
    public GeoServerClient geoServerClient(
            @Value("${geoserver.url}") String url,
            @Value("${geoserver.username}") String username,
            @Value("${geoserver.password}") String password) {
        return GeoServerClient.builder()
                .url(url)
                .credentials(username, password)
                .defaultFormat(DataFormat.JSON)
                .build();
    }
}
