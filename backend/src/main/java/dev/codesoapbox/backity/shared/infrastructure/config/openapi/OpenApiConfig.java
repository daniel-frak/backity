package dev.codesoapbox.backity.shared.infrastructure.config.openapi;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.lowercaseenums.openapi.SwaggerEnumLowerCaseModelConverter;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.OpenApiBeanConfiguration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

import static java.util.Collections.singletonList;

@OpenApiBeanConfiguration
@OpenAPIDefinition
@AllArgsConstructor
public class OpenApiConfig {

    private static final String CODE_GENERATION_PROFILE = "angular-client-code-gen";

    static {
        // Generate enums on frontend
        io.swagger.v3.core.jackson.ModelResolver.enumsAsRef = true;
    }

    private final Environment environment;

    @Bean
    OpenAPI customOpenAPI(OpenApiProperties properties) {
        var openApi = new OpenAPI()
                .info(getInfo(properties))
                .components(new Components());

        if (Arrays.asList(environment.getActiveProfiles()).contains(CODE_GENERATION_PROFILE)) {
            var server = getLocalhostServer();
            openApi.setServers(singletonList(server));
        }

        return openApi;
    }

    private Info getInfo(OpenApiProperties properties) {
        return new Info()
                .title(properties.getProjectTitle())
                .description(properties.getProjectDescription())
                .version(properties.getProjectVersion());
    }

    private Server getLocalhostServer() {
        var server = new Server();
        server.setUrl("http://localhost:8080");
        return server;
    }

    @Bean
    OpenApiCustomizer addAdditionalClasses(Reflections reflections) {
        return new OpenApiAdditionalSchemaProvider(reflections);
    }

    @Bean
    SwaggerEnumLowerCaseModelConverter swaggerEnumLowerCaseModelConverter() {
        return new SwaggerEnumLowerCaseModelConverter();
    }
}
