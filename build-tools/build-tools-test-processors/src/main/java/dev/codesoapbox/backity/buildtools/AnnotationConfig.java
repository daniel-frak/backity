package dev.codesoapbox.backity.buildtools;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record AnnotationConfig(
        List<AnnotationValue> concreteAnnotations,
        String stripFromClassNameRegex
) {

    private static final String CONCRETE_ANNOTATIONS_PROPERTY = "value";
    private static final String STRIP_FROM_CLASS_NAME_REGEX_PROPERTY = "stripFromClassNameRegex";

    @SuppressWarnings("unchecked")
    public static AnnotationConfig create(Elements elements, AnnotationMirror processorAnnotation) {
        Map<String, AnnotationValue> values = elements
                .getElementValuesWithDefaults(processorAnnotation)
                .entrySet().stream()
                .collect(Collectors.toMap(
                        e ->
                                e.getKey().getSimpleName().toString(),
                        Map.Entry::getValue
                ));

        List<AnnotationValue> concreteAnnotations =
                (List<AnnotationValue>) values.get(CONCRETE_ANNOTATIONS_PROPERTY).getValue();
        String stripFromClassNameRegex = (String) values.get(STRIP_FROM_CLASS_NAME_REGEX_PROPERTY).getValue();

        return new AnnotationConfig(concreteAnnotations, stripFromClassNameRegex);
    }
}
