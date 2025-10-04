package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InfrastructureUtilityBeanConfiguration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.FilterBuilder;
import org.springframework.context.annotation.Bean;

@InfrastructureUtilityBeanConfiguration
public class ReflectionsConfig {

    @Bean
    Reflections reflections() {
        return new Reflections(BackityApplication.class.getPackageName(),
                Scanners.SubTypes.filterResultsBy(new FilterBuilder()));
    }
}
