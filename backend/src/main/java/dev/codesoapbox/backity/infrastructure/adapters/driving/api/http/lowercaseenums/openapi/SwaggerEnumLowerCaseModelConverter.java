package dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.lowercaseenums.openapi;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;
import dev.codesoapbox.backity.shared.adapters.driving.api.http.lowercaseenums.openapi.LowercaseApiEnum;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;

/**
 * Shows all Enum values for Enums annotated with {@link LowercaseApiEnum} as lowercase in OpenAPI.
 */
public class SwaggerEnumLowerCaseModelConverter implements ModelConverter {

    @Override
    public Schema<?> resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext,
                             Iterator<ModelConverter> iterator) {
        JavaType javaType = Json.mapper().constructType(annotatedType.getType());
        if (isAnnotatedEnum(annotatedType, javaType)) {
            Enum<?>[] enumConstants = getEnumConstants(javaType);
            String name = getName(javaType);

            return createSchema(name, enumConstants);
        }
        return delegate(annotatedType, modelConverterContext, iterator);
    }

    private String getName(JavaType javaType) {
        Class<?> rawClass = javaType.getRawClass();
        io.swagger.v3.oas.annotations.media.Schema schemaAnnotation =
                rawClass.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);

        if (schemaAnnotation != null && !schemaAnnotation.name().isEmpty()) {
            return schemaAnnotation.name();
        }
        return rawClass.getSimpleName();
    }

    private boolean isAnnotatedEnum(AnnotatedType annotatedType, JavaType javaType) {
        return javaType.isEnumType() && containsLowerCaseApiAnnotation(annotatedType);
    }

    private boolean containsLowerCaseApiAnnotation(AnnotatedType annotatedType) {
        Type type = annotatedType.getType();
        Class<?> rawClass = getRawClass(type);
        return rawClass.isAnnotationPresent(LowercaseApiEnum.class);
    }

    private Class<?> getRawClass(Type type) {
        if (type instanceof SimpleType simpleType) {
            return simpleType.getRawClass();
        } else {
            return (Class<?>) type;
        }
    }

    @SuppressWarnings("unchecked")
    private Enum<?>[] getEnumConstants(JavaType javaType) {
        Class<Enum<?>> enumClass = (Class<Enum<?>>) javaType.getRawClass();
        return enumClass.getEnumConstants();
    }

    private StringSchema createSchema(String name, Enum<?>[] enumConstants) {
        var stringSchema = new StringSchema();
        stringSchema.name(name);
        for (var enumConstant : enumConstants) {
            String enumValue = enumConstant.name().toLowerCase(Locale.getDefault());
            stringSchema.addEnumItem(enumValue);
        }
        return stringSchema;
    }

    private Schema<?> delegate(AnnotatedType annotatedType, ModelConverterContext modelConverterContext,
                               Iterator<ModelConverter> iterator) {
        return iterator.hasNext()
                ? iterator.next().resolve(annotatedType, modelConverterContext, iterator)
                : null;
    }
}
