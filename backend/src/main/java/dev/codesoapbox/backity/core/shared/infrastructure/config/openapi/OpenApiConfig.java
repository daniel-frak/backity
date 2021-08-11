package dev.codesoapbox.backity.core.shared.infrastructure.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@OpenAPIDefinition
@AllArgsConstructor
public class OpenApiConfig {

    private static final String CODE_GENERATION_PROFILE = "angular";

    static {
        io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
    }

    private final Environment environment;

    @Bean
    public OpenAPI customOpenAPI(OpenApiProperties properties) {
        var openApi = new OpenAPI()
                .info(getInfo(properties))
                .components(new Components());

        if (Arrays.asList(environment.getActiveProfiles()).contains(CODE_GENERATION_PROFILE)) {
            var server = getLocalhostServer();
            openApi.setServers(Collections.singletonList(server));
        }

        return openApi;
    }

    private Info getInfo(OpenApiProperties properties) {
        return new Info()
                .title(properties.getProjectTitle())
                .description(properties.getProjectDescription())
                .version(properties.getProjectVersion()).license(getLicense());
    }

    private License getLicense() {
        return new License()
                .name("Unlicense")
                .url("https://unlicense.org/");
    }

    private Server getLocalhostServer() {
        var server = new Server();
        server.setUrl("http://localhost:8080");
        return server;
    }

    @Bean
    public OpenApiCustomiser addAdditionalClasses(Reflections reflections) {
        return new OpenApiAdditionalSchemaProvider(reflections);
    }
}
