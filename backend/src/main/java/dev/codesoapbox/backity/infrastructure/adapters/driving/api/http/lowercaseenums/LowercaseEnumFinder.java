package dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.lowercaseenums;

import dev.codesoapbox.backity.shared.adapters.driving.api.http.lowercaseenums.openapi.LowercaseApiEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Finds Enums annotated with {@link LowercaseApiEnum}.
 */
@RequiredArgsConstructor
public class LowercaseEnumFinder {

    private final ApplicationContext applicationContext;

    /*
    Calling applicationContext::getBean from within the context of a WebMvcConfigurer leads to a circular dependency
    error. At the same time, ::getType returns a CGLib class, which loses annotation information.
    This uses a weak workaround to retrieve the original controller class by trimming the CGLib suffix from the
    retrieved class.
     */
    // This cast is safe because it is guaranteed the elements of type '? extends Class<?>' are of type 'Class<?>'.
    @SuppressWarnings("unchecked")
    public List<Class<?>> getAnnotatedControllerEnums() {
        String[] controllerBeanNames = applicationContext.getBeanNamesForAnnotation(RestController.class);
        return (List<Class<?>>)(List<?>) Arrays.stream(controllerBeanNames)
                .map(applicationContext::getType)
                .filter(Objects::nonNull)
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .flatMap(m -> Arrays.stream(m.getParameters()))
                .map(Parameter::getType)
                .filter(Class::isEnum)
                .filter(t -> t.isAnnotationPresent(LowercaseApiEnum.class))
                .distinct()
                .toList();
    }
}
