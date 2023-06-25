package dev.codesoapbox.backity.core.shared.config.openapi;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.core.util.RefUtils;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.providers.ObjectMapperProvider;

import java.util.Iterator;

/**
 * io.swagger.v3.core.jackson.ModelResolver does not handle generics properly if class is annotated
 * with @Schema(name="...").
 * <p>
 * This class provides a workaround that corrects the behavior.
 *
 * @see <a href="https://github.com/swagger-api/swagger-core/issues/4440">Github issue</a>
 */
@RequiredArgsConstructor
public class GenericsFixingModelConverter implements ModelConverter {

    private static final TypeNameResolver TYPE_NAME_RESOLVER = TypeNameResolver.std;
    private final ObjectMapperProvider springDocObjectMapper;

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context,
                             Iterator<ModelConverter> chain) {
        Schema<?> resolvedSchema = chain.next().resolve(type, context, chain);
        if (resolvedSchema != null && resolvedSchema.getType() == null) {
            JavaType javaType = springDocObjectMapper.jsonMapper().constructType(type.getType());
            if (javaType != null && javaType.hasGenericTypes()) {
                String ref = resolvedSchema.get$ref();
                if (ref != null) {
                    String referencedSchemaKey = ref.substring(AnnotationsUtils.COMPONENTS_REF.length());
                    Schema<?> referencedSchema = context.getDefinedModels().get(referencedSchemaKey);
                    String genericName = TYPE_NAME_RESOLVER.nameForType(javaType);
                    context.defineModel(genericName, referencedSchema, type, referencedSchema.getName());
                    referencedSchema.setName(genericName);
                    resolvedSchema.set$ref(RefUtils.constructRef(referencedSchema.getName()));
                }
            }
        }
        return resolvedSchema;
    }
}
