package dev.codesoapbox.backity.integrations.gog.config;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class GogWebClientConfig {

    private final GogProperties gogProperties;

    @Bean(name = "gogAuth")
    WebClient webClientAuth(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(gogProperties.auth().baseUrl())
                .build();
    }

    @Bean(name = "gogEmbed")
    public WebClient webClientEmbed(WebClient.Builder webClientBuilder) {
        return webClientBuilder.baseUrl(gogProperties.embed().baseUrl())
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
