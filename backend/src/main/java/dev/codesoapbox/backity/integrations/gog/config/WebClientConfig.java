package dev.codesoapbox.backity.integrations.gog.config;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "gogAuth")
    WebClient webClientAuth(WebClient.Builder webClientBuilder,
                            @Value("${backity.gog.auth.base-url}") String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl)
                .build();
    }

    @Bean(name = "gogEmbed")
    public WebClient webClientEmbed(WebClient.Builder webClientBuilder,
                                    @Value("${backity.gog.embed.base-url}") String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .compress(true)
                                .followRedirect((req, res) -> {
                                    res.responseHeaders().add("Final-location", req.uri());
                                    return HttpResponseStatus.FOUND.equals(res.status());
                                })
                ))
                .build();
    }
}
