package dev.codesoapbox.backity.testing.mocking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.MergedContextConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

class MockBeansContextCustomizerTest {

    private static final String PACKAGE_1 = "some.package1";
    private static final String PACKAGE_2 = "some.package2";

    private String[] getBeanNames(GenericApplicationContext context, Class<?> beanType) {
        return context.getBeanFactory().getBeanNamesForType(beanType);
    }

    private MockBeansContextCustomizer createCustomizer(Class<?> configClass) {
        return new MockBeansContextCustomizer(getSynthesizedComponentScan(configClass));
    }

    private ComponentScan getSynthesizedComponentScan(Class<?> configClass) {
        return MergedAnnotations.from(configClass)
                .get(ComponentScan.class)
                .synthesize();
    }

    private boolean isMock(GenericApplicationContext context, Class<?> beanType) {
        return mockingDetails(context.getBeanFactory().getBean(beanType)).isMock();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface MockCandidate {
    }

    @MockCandidate
    @TestConfiguration(proxyBeanMethods = false)
    static class MatchedConfiguration {

        @Bean
        MethodBean methodBean() {
            return new MethodBean();
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        InfrastructureMethodBean infrastructureMethodBean() {
            return new InfrastructureMethodBean();
        }

        static class MethodBean {
        }

        static class InfrastructureMethodBean {
        }
    }

    @MockCandidate
    @TestConfiguration(proxyBeanMethods = false)
    static class EmptyMatchedConfiguration {
    }

    @ComponentScan(
            basePackageClasses = MockBeansContextCustomizerTest.class,
            useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = MockCandidate.class
            )
    )
    private static final class ScanningMockCandidates {
    }

    @ComponentScan(
            basePackageClasses = MockBeansContextCustomizerTest.class,
            useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = MockCandidate.class
            ),
            excludeFilters = @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = ScanningMockCandidatesExceptOne.ExcludedAnnotatedComponent.class
            )
    )
    private static final class ScanningMockCandidatesExceptOne {

        @MockCandidate
        static class MatchedAnnotatedComponent {
        }

        @MockCandidate
        static class ExcludedAnnotatedComponent {
        }
    }

    @ComponentScan(basePackages = PACKAGE_1)
    private static final class Basic {
    }

    @ComponentScan(basePackages = PACKAGE_1)
    private static final class BasicCopy {
    }

    @ComponentScan(PACKAGE_1)
    private static final class BasicViaValueAlias {
    }

    @ComponentScan(basePackages = PACKAGE_2)
    private static final class OtherPackage {
    }

    @ComponentScan(basePackageClasses = Service.class)
    private static final class ByClass {
    }

    @ComponentScan(basePackageClasses = Service.class)
    private static final class ByClassCopy {
    }

    @ComponentScan(basePackageClasses = Component.class)
    private static final class OtherClass {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false)
    private static final class NoDefaultFilters {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
    private static final class IncludingServices {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
    private static final class IncludingServicesCopy {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Component.class))
    private static final class IncludingComponents {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
    private static final class ExcludingServices {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Service.class))
    private static final class ExcludingServicesCopy {
    }

    @ComponentScan(basePackages = PACKAGE_1, useDefaultFilters = false,
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Component.class))
    private static final class ExcludingComponents {
    }

    @Nested
    class CustomizeContext {

        @Test
        void shouldMockMatchedComponents() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidates.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(isMock(context, ScanningMockCandidatesExceptOne.MatchedAnnotatedComponent.class)).isTrue();
        }

        @Test
        void shouldMockBeanMethodsOfMatchedConfiguration() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidates.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(isMock(context, MatchedConfiguration.MethodBean.class)).isTrue();
        }

        @Test
        void shouldNotRegisterMatchedConfigurationClassItself() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidates.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(getBeanNames(context, MatchedConfiguration.class)).isEmpty();
        }

        @Test
        void shouldNotMockInfrastructureBeans() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidates.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(getBeanNames(context, MatchedConfiguration.InfrastructureMethodBean.class)).isEmpty();
        }

        @Test
        void shouldNotMockComponentsMatchingExcludeFilters() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidatesExceptOne.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(isMock(context, ScanningMockCandidatesExceptOne.MatchedAnnotatedComponent.class))
                    .isTrue();
            assertThat(getBeanNames(context, ScanningMockCandidatesExceptOne.ExcludedAnnotatedComponent.class))
                    .isEmpty();
        }

        @Test
        void shouldThrowGivenNoBeansMatched() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(NoDefaultFilters.class);
            MergedContextConfiguration mockContextConfiguration = mock(MergedContextConfiguration.class);

            assertThatThrownBy(() -> customizer.customizeContext(context, mockContextConfiguration))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageStartingWith(
                            "@MockBeansMatching matched no beans for the given @ComponentScan:");
        }

        @Test
        void shouldNotRegisterMatchedConfigurationClassWithoutBeanMethods() {
            var context = new GenericApplicationContext();
            MockBeansContextCustomizer customizer = createCustomizer(ScanningMockCandidates.class);
            MergedContextConfiguration contextConfiguration = mock(MergedContextConfiguration.class);

            customizer.customizeContext(context, contextConfiguration);

            assertThat(getBeanNames(context, EmptyMatchedConfiguration.class)).isEmpty();
        }
    }

    @Nested
    class EqualsAndHashCode {

        @Nested
        class Identity {

            @SuppressWarnings("EqualsWithItself")
            @Test
            void shouldEqualItself() {
                MockBeansContextCustomizer result = createCustomizer(Basic.class);

                assertThat(result).isEqualTo(result);
            }

            @Test
            void shouldNotEqualNull() {
                MockBeansContextCustomizer result = createCustomizer(Basic.class);

                assertThat(result).isNotEqualTo(null);
            }

            @SuppressWarnings("AssertBetweenInconvertibleTypes")
            @Test
            void shouldNotEqualDifferentType() {
                MockBeansContextCustomizer result = createCustomizer(Basic.class);

                assertThat(result).isNotEqualTo("not a customizer");
            }

            @Test
            void shouldTreatUnsynthesizedAndSynthesizedComponentScansAsEquivalent() {
                ComponentScan unsynthesized = BasicViaValueAlias.class
                        .getDeclaredAnnotation(ComponentScan.class);
                ComponentScan synthesized = getSynthesizedComponentScan(BasicViaValueAlias.class);

                MockBeansContextCustomizer customizerFromUnsynthesized =
                        new MockBeansContextCustomizer(unsynthesized);
                MockBeansContextCustomizer customizerFromSynthesized =
                        new MockBeansContextCustomizer(synthesized);

                assertThat(customizerFromUnsynthesized)
                        .isEqualTo(customizerFromSynthesized)
                        .hasSameHashCodeAs(customizerFromSynthesized);
            }
        }

        @Nested
        class EquivalentComponentScans {

            static Stream<Arguments> equivalentConfigs() {
                return Stream.of(
                        arguments("same basePackages", Basic.class, BasicCopy.class),
                        arguments("basePackages vs. value alias", Basic.class, BasicViaValueAlias.class),
                        arguments("same basePackageClasses", ByClass.class, ByClassCopy.class),
                        arguments("same includeFilters", IncludingServices.class, IncludingServicesCopy.class),
                        arguments("same excludeFilters", ExcludingServices.class, ExcludingServicesCopy.class)
                );
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("equivalentConfigs")
            void shouldBeEqualSymmetricallyWithSameHashCode(String description, Class<?> config1, Class<?> config2) {
                MockBeansContextCustomizer customizer1 = createCustomizer(config1);
                MockBeansContextCustomizer customizer2 = createCustomizer(config2);

                assertThat(customizer1).isEqualTo(customizer2);
                assertThat(customizer2).isEqualTo(customizer1);
                assertThat(customizer1).hasSameHashCodeAs(customizer2);
            }
        }

        @Nested
        class DifferentComponentScans {

            static Stream<Arguments> differentConfigs() {
                return Stream.of(
                        arguments("different basePackages", Basic.class, OtherPackage.class),
                        arguments("different basePackageClasses", ByClass.class, OtherClass.class),
                        arguments("different useDefaultFilters", Basic.class, NoDefaultFilters.class),
                        arguments("different includeFilters", IncludingServices.class, IncludingComponents.class),
                        arguments("different excludeFilters", ExcludingServices.class, ExcludingComponents.class),
                        arguments("includeFilters present vs. absent", NoDefaultFilters.class, IncludingServices.class),
                        arguments("same filter as include vs. exclude",
                                IncludingServices.class, ExcludingServices.class)
                );
            }

            @ParameterizedTest(name = "{0}")
            @MethodSource("differentConfigs")
            void shouldNotBeEqualSymmetrically(String description, Class<?> config1, Class<?> config2) {
                MockBeansContextCustomizer customizer1 = createCustomizer(config1);
                MockBeansContextCustomizer customizer2 = createCustomizer(config2);

                assertThat(customizer1).isNotEqualTo(customizer2);
                assertThat(customizer2).isNotEqualTo(customizer1);
            }
        }
    }
}
