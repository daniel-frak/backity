package dev.codesoapbox.backity.testing.mocking;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.List;

class MockBeansContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(
            Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        MergedAnnotation<MockBeansMatching> annotation = findMockBeansMatchingAnnotation(testClass);

        if (!annotation.isPresent()) {
            return null;
        }
        ComponentScan componentScan = annotation.synthesize().value();
        return new MockBeansContextCustomizer(componentScan);
    }

    private MergedAnnotation<MockBeansMatching> findMockBeansMatchingAnnotation(Class<?> testClass) {
        return MergedAnnotations
                .from(testClass, SearchStrategy.TYPE_HIERARCHY)
                .get(MockBeansMatching.class);
    }
}