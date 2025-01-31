package dev.codesoapbox.backity.integrations.gog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("backity.gog")
public record GogProperties(
        @Value("${client-secret}") String clientSecret,
        @Value("${client-id}") String clientId,
        @Value("${auth}") AuthProperties auth,
        @Value("${embed}") EmbedProperties embed

) {

    public record AuthProperties(
            @Value("${base-url}") String baseUrl,
            @Value("${redirect-uri}") String redirectUri
    ) {
    }

    public record EmbedProperties(
            @Value("${base-url}") String baseUrl
    ) {
    }
}
