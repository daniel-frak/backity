package dev.codesoapbox.backity.archunit.production.rules.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dev.codesoapbox.backity.archunit.ArchUnitMetaAnnotation;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Annotation;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.GeneralCodingRules.BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION;

/// Low-abstraction rules about how Spring should be used.
@SuppressWarnings("unused")
public class SpringUsageRules {

    @ArchTest
    static final ArchRule SPRING_CONFIGURATION_ANNOTATION_SHOULD_NOT_BE_USED_DIRECTLY = noClasses()
            .that().areNotInterfaces()
            .should().beAnnotatedWith(Configuration.class)
            .because("""
                    using the @Configuration annotation only via meta-annotations provides clarity \
                    and makes slice testing easier.
                    
                    Context:
                    When using @Configuration classes through meta-annotations, these meta-annotations can be used \
                    to provide context around where the configured beans fit within the architecture of the application.
                    Additionally, these meta-annotations can be used to automatically configure slice tests.
                    
                    Positive consequences:
                    - Improved clarity due to meta-annotations providing context for the beans.
                    - Auto-configuring slice tests is possible due to meta-annotations on which @ComponentScan can rely.
                    
                    Negative consequences:
                    - Slightly more ceremony when defining bean factories.
                    """);

    @ArchTest
    static final ArchRule FIELD_INJECTION_SHOULD_NOT_BE_USED = noFields().that()
            .areDeclaredInClassesThat(new DescribedPredicate<>(
                    "don't have configuration annotation on themselves or on parent classes") {

                @Override
                public boolean test(JavaClass javaClass) {
                    while (javaClass != null) {
                        if (isMetaAnnotated(Configuration.class, javaClass)
                                || isMetaAnnotated(ConfigurationProperties.class, javaClass)) {
                            return false;
                        }
                        javaClass = javaClass.getEnclosingClass().orElse(null);
                    }
                    return true;
                }

                private <T extends Annotation> boolean isMetaAnnotated(
                        Class<T> annotationType, JavaClass javaClass) {
                    return ArchUnitMetaAnnotation.tryGet(annotationType, javaClass.getAnnotations()).isPresent();
                }
            })
            .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
            .as("no classes should use field injection")
            .because("""
                    field injection is considered harmful; \
                    constructor injection or setter injection should be used instead.
                    
                    Context:
                    Field injection (`@Autowired`, `@Inject`) directly assigns dependencies to class fields.
                    While convenient, this approach has multiple downsides:
                    - It bypasses constructor-based dependency injection, \
                      making it harder to enforce required dependencies.
                    - It hinders testability by preventing easy instantiation of objects \
                    without Spring or dependency injection frameworks, creating a tight coupling with them.
                    - It makes makes dependencies less visible, reducing clarity in class design.
                    - It makes it impossible to create immutable objects, such as with constructor injection.
                    - It makes it easy to create a class with many dependencies, encouraging bad design practices.
                    
                    Positive consequences:
                    - Encourages constructor injection, which makes dependencies explicit and mandatory.
                    - Improves testability by allowing easier instantiation of objects without requiring a framework.
                    - Enhances clarity by making dependencies visible in constructors rather than hidden in fields.
                    - Makes immutable dependencies possible, reducing potential runtime errors \
                    and unintended reassignments.
                    
                    Negative consequences:
                    - May introduce boilerplate in cases where many dependencies need to be injected.
                    """);

    @ArchTest
    static final ArchRule BEANS_SHOULD_BE_DEFINED_IN_CONFIGURATION_CLASSES = noClasses()
            .that().areNotMetaAnnotatedWith(Configuration.class)
            .and().areNotAnnotatedWith(RestController.class)
            .and().areNotAnnotatedWith(ControllerAdvice.class)
            .and().areNotAnnotatedWith(RestControllerAdvice.class)
            .should().beAnnotatedWith(Component.class)
            .because("""
                    defining beans outside a configuration class leads to inconsistent dependency management \
                    and reduced clarity in bean instantiation.
                    
                    Context:
                    - Using more than one way of bean instantiation can be confusing.
                    - It should be easy to locate bean definitions in the codebase.
                    - Domain code should not be polluted with a dependency on Spring, yet some domain classes must
                    be instantiated as beans.
                    - It's a good practice to reduce coupling with the framework wherever possible.
                    - Services often need additional logic like @Profile, which can make it difficult to tell which \
                    beans will be instantiated if this logic is kept on the classes themselves.
                    - @Configuration classes, as a bird's eye view of the bean configuration, \
                    can provide clarity around what gets instantiated when.
                    
                    Therefore, it is a good practice to instantiate all services using @Configuration classes, which
                    should be located in a configuration subpackage.
                    
                    Some advanced Spring annotations are themselves meta-annotated with @Component and there is no
                    reliable way to avoid using them. These should be allowed.
                    
                    Positive consequences:
                    - Decouples large parts of the codebase from Spring, making it easier to work with and maintain.
                    - Provides a centralized view of bean instantiation, \
                    making runtime conditions (`@Profile`) easier to manage.
                    - Reduces confusion by standardizing bean definition practices across the codebase.
                    
                    Negative consequences:
                    - May introduce many configuration classes, requiring careful organization to avoid fragmentation.
                    - Developers may be used to annotating beans directly, making it difficult to switch to the new \
                    approach.
                    """);
}
