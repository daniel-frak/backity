package dev.codesoapbox.backity.integrations.gog.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "webClientGeneral")
    WebClient webClientGeneral(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .build();
    }

    @Bean(name = "gogAuth")
    WebClient webClientAuth(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://auth.gog.com")
                .build();
    }

    @Bean(name = "gogEmbed")
    WebClient webClientEmbed(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl("https://embed.gog.com")
                .build();
    }
}
