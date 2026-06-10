package dev.codesoapbox.backity.testing.mocking;

import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

class MockBeansContextCustomizer implements ContextCustomizer {

    private final AnnotationAttributes componentScanAttributes;

    MockBeansContextCustomizer(ComponentScan componentScan) {
        this.componentScanAttributes = MergedAnnotation.from(componentScan)
                .asAnnotationAttributes(MergedAnnotation.Adapt.ANNOTATION_TO_MAP);
    }

    @Override
    public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
        DefaultListableBeanFactory targetBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        DefaultListableBeanFactory mockCandidateBeanFactory =
                discoverMockCandidateBeans(context.getEnvironment(), context);

        int registeredMocks = 0;
        for (String name : mockCandidateBeanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = mockCandidateBeanFactory.getBeanDefinition(name);
            if (!isMockTarget(beanDefinition)) {
                continue;
            }
            Class<?> rawType = resolveBeanType(beanDefinition, targetBeanFactory.getBeanClassLoader());

            RootBeanDefinition mock = createMockBeanDefinition(rawType);
            targetBeanFactory.registerBeanDefinition(name, mock);
            registeredMocks++;
        }

        validateAtLeastOneMockWasRegistered(registeredMocks);
    }

    private DefaultListableBeanFactory discoverMockCandidateBeans(
            Environment environment, ResourceLoader resourceLoader) {
        var registry = new DefaultListableBeanFactory();
        registerTopLevelMockCandidateBeans(registry, environment, resourceLoader);
        registerBeanMethodMockCandidateBeans(registry, environment, resourceLoader);

        return registry;
    }

    /// Registers beans which are directly annotated using `@Component` and its derivatives
    private void registerTopLevelMockCandidateBeans(
            DefaultListableBeanFactory registry, Environment environment, ResourceLoader resourceLoader) {
        ClassPathBeanDefinitionScanner scanner = createBeanDefinitionScanner(environment, resourceLoader, registry);
        scanner.scan(getBasePackages(componentScanAttributes));
    }

    private ClassPathBeanDefinitionScanner createBeanDefinitionScanner(
            Environment environment, ResourceLoader resourceLoader, DefaultListableBeanFactory registry) {
        boolean useDefaultFilters = componentScanAttributes.getBoolean("useDefaultFilters");
        var scanner = new ClassPathBeanDefinitionScanner(registry, useDefaultFilters, environment, resourceLoader);

        for (AnnotationAttributes filter : componentScanAttributes.getAnnotationArray("includeFilters")) {
            TypeFilterUtils.createTypeFiltersFor(filter, environment, resourceLoader, registry)
                    .forEach(scanner::addIncludeFilter);
        }
        for (AnnotationAttributes filter : componentScanAttributes.getAnnotationArray("excludeFilters")) {
            TypeFilterUtils.createTypeFiltersFor(filter, environment, resourceLoader, registry)
                    .forEach(scanner::addExcludeFilter);
        }

        return scanner;
    }

    /// Registers beans which are defined in @Configuration classes
    private void registerBeanMethodMockCandidateBeans(
            DefaultListableBeanFactory registry, Environment environment, ResourceLoader resourceLoader) {
        ConfigurationClassPostProcessor parser = createConfigurationClassPostProcessor(environment, resourceLoader);
        parser.postProcessBeanDefinitionRegistry(registry);
    }

    private ConfigurationClassPostProcessor createConfigurationClassPostProcessor(
            Environment environment, ResourceLoader resourceLoader) {
        var parser = new ConfigurationClassPostProcessor();
        parser.setEnvironment(environment);
        parser.setResourceLoader(resourceLoader);
        parser.setBeanClassLoader(Objects.requireNonNull(resourceLoader.getClassLoader()));

        return parser;
    }

    private String[] getBasePackages(AnnotationAttributes attributes) {
        Set<String> packages = new LinkedHashSet<>();
        Collections.addAll(packages, attributes.getStringArray("basePackages"));
        for (Class<?> type : attributes.getClassArray("basePackageClasses")) {
            packages.add(ClassUtils.getPackageName(type));
        }
        return packages.toArray(String[]::new);
    }

    private boolean isMockTarget(BeanDefinition definition) {
        if (definition.getRole() == BeanDefinition.ROLE_INFRASTRUCTURE) {
            return false; // Drop Spring's own post-processors
        }
        if (!(definition instanceof AnnotatedBeanDefinition annotated)) {
            return true; // Keep plain classes (individual beans)
        }
        return isBeanMethod(annotated) // Keep @Bean instances
                || !hasBeanMethods(annotated); // Drop config classes
    }

    private boolean isBeanMethod(AnnotatedBeanDefinition annotated) {
        return annotated.getFactoryMethodMetadata() != null;
    }

    private boolean hasBeanMethods(AnnotatedBeanDefinition definition) {
        return !definition.getMetadata().getAnnotatedMethods(Bean.class.getName()).isEmpty();
    }

    @SneakyThrows
    private Class<?> resolveBeanType(BeanDefinition definition, ClassLoader classLoader) {
        String beanClassName = getBeanClassName(definition);
        return ClassUtils.forName(beanClassName, classLoader);
    }

    @SuppressWarnings("DataFlowIssue") // getFactoryMethodMetadata() is always non-null for bean methods
    private String getBeanClassName(BeanDefinition definition) {
        if (definition instanceof AnnotatedBeanDefinition annotated && isBeanMethod(annotated)) {
            return annotated.getFactoryMethodMetadata().getReturnTypeName();
        }
        return definition.getBeanClassName();
    }

    private RootBeanDefinition createMockBeanDefinition(Class<?> rawType) {
        var mock = new RootBeanDefinition(rawType);
        mock.setInstanceSupplier(() -> Mockito.mock(rawType, MockReset.withSettings(MockReset.AFTER)));

        return mock;
    }

    private void validateAtLeastOneMockWasRegistered(int registeredMocks) {
        if (registeredMocks == 0) {
            throw new IllegalStateException("@%s matched no beans for the given @ComponentScan: %s"
                    .formatted(MockBeansMatching.class.getSimpleName(), componentScanAttributes));
        }
    }

    // Necessary for context caching
    @Override
    public boolean equals(Object other) {
        return (other instanceof MockBeansContextCustomizer that)
                && this.componentScanAttributes.equals(that.componentScanAttributes);
    }

    // Necessary for context caching
    @Override
    public int hashCode() {
        return this.componentScanAttributes.hashCode();
    }
}