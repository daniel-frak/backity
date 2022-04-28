package dev.codesoapbox.backity.core.shared.config;

import dev.codesoapbox.backity.BackityApplication;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReflectionsConfig {

    @Bean
    public Reflections reflections() {
        return new Reflections(BackityApplication.class.getPackageName(), new SubTypesScanner(false));
    }
}
