package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.converters.SchemaPropertyDeprecatingConverter;

import java.util.Iterator;

import static java.util.Collections.singletonList;

/**
 * Workaround needed for supporting @Deprecated on non-primitive fields in OpenAPI.
 *
 * @see <a href="https://codesoapbox.dev/how-to-fix-springdoc-openapi-ignoring-deprecated-on-non-primitive-fields/">
 * How to fix Springdoc-openapi ignoring @Deprecated on non-primitive fields - Code Soapbox</a>
 */
public class CustomSchemaPropertyDeprecatingConverter extends SchemaPropertyDeprecatingConverter {

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            Schema<?> resolvedSchema = chain.next().resolve(type, context, chain);
            if (type.isSchemaProperty() && containsDeprecatedAnnotation(type.getCtxAnnotations())) {
                if (resolvedSchema.get$ref() != null) {
                    // Sibling values alongside $ref are ignored in OpenAPI versions lower than 3.1. See:
                    // https://swagger.io/docs/specification/using-ref/#sibling
                    // To add properties to a $ref, it must be wrapped in allOf.
                    resolvedSchema = wrapInAllOf(resolvedSchema);
                }
                resolvedSchema.setDeprecated(true);
            }
            return resolvedSchema;
        }
        return null;
    }

    private Schema<?> wrapInAllOf(Schema<?> resolvedSchema) {
        Schema<?> wrapperSchema = new Schema<>();
        wrapperSchema.allOf(singletonList(resolvedSchema));
        return wrapperSchema;
    }
}
