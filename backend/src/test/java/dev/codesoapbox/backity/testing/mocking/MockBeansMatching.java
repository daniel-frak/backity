package dev.codesoapbox.backity.testing.mocking;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextCustomizerFactories;

import java.lang.annotation.*;

/// Registers Mockito mocks into a Spring test context for beans discovered via [ComponentScan].
/// Works like a dynamic alternative to `@MockitoBean`.
///
/// For a matched configuration class, its `@Bean` methods are mocked;
/// any matched non-configuration class is mocked directly.
///
/// Example:
/// ```java
/// @SpringJUnitConfig(Foo.class)
/// @MockBeansMatching(
///     @ComponentScan(
///         basePackageClasses = MyApplication.class,
///         includeFilters = @ComponentScan.Filter(
///                 type = FilterType.ANNOTATION,
///                 classes = BarSliceBeanConfiguration.class // BarBeanConfiguration is annotated with it
///         ),
///         useDefaultFilters = false
///     )
/// )
/// class FooTest {
///
///     @Autowired Foo foo;
///
///     @Autowired Bar barMock; // declared as a real bean in BarBeanConfiguration
/// }
/// ```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ContextCustomizerFactories(factories = MockBeansContextCustomizerFactory.class,
        mergeMode = ContextCustomizerFactories.MergeMode.MERGE_WITH_DEFAULTS)
public @interface MockBeansMatching {

    ComponentScan value();
}
