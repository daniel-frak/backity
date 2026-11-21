package dev.codesoapbox.backity.testing.mocking;

import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.TestContextAnnotationUtils;

import java.util.List;

class MockBeansContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(
            Class<?> testClass, List<ContextConfigurationAttributes> configAttributes) {
        MockBeansMatching annotation = findMockBeansMatchingAnnotation(testClass);

        if (annotation == null) {
            return null;
        }
        return new MockBeansContextCustomizer(annotation.value());
    }

    private MockBeansMatching findMockBeansMatchingAnnotation(Class<?> testClass) {
        return TestContextAnnotationUtils.findMergedAnnotation(testClass, MockBeansMatching.class);
    }
}
