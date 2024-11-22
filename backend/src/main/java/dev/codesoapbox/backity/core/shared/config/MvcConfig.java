package dev.codesoapbox.backity.core.shared.config;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums.CaseInsensitiveEnumConverter;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums.LowercaseApiEnum;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums.LowercaseEnumFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private static final String API_BASE_PACKAGE = BackityApplication.class.getPackageName();

    private final ApplicationContext applicationContext;

    /*
    Add /api prefix to all controllers:
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api",
                HandlerTypePredicate.forBasePackage(API_BASE_PACKAGE)
                        .and(HandlerTypePredicate.forAnnotation(RestController.class)));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NonNull String resourcePath, @NonNull Resource location)
                            throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        if (resourcePath.startsWith("api/")) {
                            return super.getResource(resourcePath, location);
                        }

                        return requestedResource.exists() && requestedResource.isReadable()
                                ? requestedResource
                                : new ClassPathResource("/static/index.html");
                    }
                });
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        makeAnnotatedControllerEnumArgumentsCaseInsensitive(registry);
    }

    /**
     * @see LowercaseApiEnum
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void makeAnnotatedControllerEnumArgumentsCaseInsensitive(FormatterRegistry registry) {
        List<Class<?>> annotatedControllerEnums = new LowercaseEnumFinder(applicationContext)
                .getAnnotatedControllerEnums();

        for (Class<?> enumClass : annotatedControllerEnums) {
            Class<? extends Enum> aClass = castEnumClass(enumClass);
            registry.addConverter(String.class, enumClass, new CaseInsensitiveEnumConverter<>(aClass));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends Enum<?>> Class<? extends Enum> castEnumClass(Class<?> clazz) {
        return (Class<T>) clazz;
    }
}
