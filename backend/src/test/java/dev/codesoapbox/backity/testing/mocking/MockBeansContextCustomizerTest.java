package dev.codesoapbox.backity.testing.mocking;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MockBeansContextCustomizerTest {

    private static final String PACKAGE_1 = "some.package1";
    private static final String PACKAGE_2 = "some.package2";

    private static MockBeansContextCustomizer createCustomizer(Class<?> configClass) {
        ComponentScan componentScan = MergedAnnotations.from(configClass)
                .get(ComponentScan.class)
                .synthesize();
        
        return new MockBeansContextCustomizer(componentScan);
    }

    @Nested
    class EqualsAndHashCode {

        @Nested
        class Identity {

            @SuppressWarnings("EqualsWithItself")
            @Test
            void shouldEqualItself() {
                MockBeansContextCustomizer customizer = createCustomizer(Basic.class);

                assertThat(customizer).isEqualTo(customizer);
            }

            @Test
            void shouldNotEqualNull() {
                assertThat(createCustomizer(Basic.class)).isNotEqualTo(null);
            }

            @SuppressWarnings("AssertBetweenInconvertibleTypes")
            @Test
            void shouldNotEqualDifferentType() {
                assertThat(createCustomizer(Basic.class)).isNotEqualTo("not a customizer");
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
}
