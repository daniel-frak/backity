package dev.codesoapbox.backity.shared.infrastructure.config.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;

import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenericsFixingModelConverterTest {

    private ModelConverters modelConverters;

    @BeforeEach
    void setUp() {
        modelConverters = new ModelConverters();
    }

    @Test
    void shouldStillBeNecessary() {
        ResolvedSchema schemaForTestRecordWithoutAnnotation = getResolvedSchemaFor(TestRecordWithoutAnnotation.class);
        ResolvedSchema schemaForTestRecordWithAnnotation = getResolvedSchemaFor(TestRecordWithAnnotation.class);

        assertThat(schemaForTestRecordWithoutAnnotation.schema.getName())
                .isEqualTo("TestRecordWithoutAnnotationObject");

        assertThat(schemaForTestRecordWithAnnotation.schema.getName())
                .isEqualTo("MyRecord"); // Should be MyRecordString!
    }

    private ResolvedSchema getResolvedSchemaFor(Class<?> rawType) {
        AnnotatedType annotatedType = constructAnnotatedType(rawType);
        return modelConverters.resolveAsResolvedSchema(annotatedType);
    }

    private static AnnotatedType constructAnnotatedType(Class<?> rawType) {
        Type type = TypeFactory.defaultInstance().constructParametricType(rawType, Object.class);
        return new AnnotatedType(type);
    }

    @Test
    void shouldHandleGenericWithCustomSchemaName() {
        registerModelConverter();
        AnnotatedType annotatedType = constructAnnotatedType(TestRecordWithAnnotation.class);
        annotatedType.resolveAsRef(true);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        String expectedSchemaName = "MyRecordObject";
        assertThat(result.schema.get$ref())
                .isEqualTo("#/components/schemas/" + expectedSchemaName);
        assertThat(result.referencedSchemas)
                .extracting(s -> s.get(expectedSchemaName))
                .extracting(io.swagger.v3.oas.models.media.Schema::getName)
                .isEqualTo(expectedSchemaName);
    }

    private void registerModelConverter() {
        SpringDocConfigProperties springDocConfigProperties = new SpringDocConfigProperties();
        springDocConfigProperties.getApiDocs().setVersion(SpringDocConfigProperties.ApiDocs.OpenApiVersion.OPENAPI_3_1);
        ObjectMapperProvider springDocObjectMapper = new ObjectMapperProvider(springDocConfigProperties);
        GenericsFixingModelConverter converter = new GenericsFixingModelConverter(springDocObjectMapper);
        modelConverters.addConverter(converter);
    }

    @Test
    void shouldDoNothingWhenResolvedSchemaIsNull() {
        modelConverters.addConverter((annotatedType, modelConverterContext, iterator) -> null);
        registerModelConverter();
        AnnotatedType annotatedType = constructAnnotatedType(TestRecordWithoutAnnotation.class);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        assertThat(result.schema).isNull();
    }

    @Test
    void shouldDoNothingWhenResolvedSchemaTypeIsNotNull() {
        var schema = new io.swagger.v3.oas.models.media.Schema<>();
        schema.type("object");
        modelConverters.addConverter((annotatedType, modelConverterContext, iterator) -> schema);
        registerModelConverter();
        AnnotatedType annotatedType = constructAnnotatedType(TestRecordWithoutAnnotation.class);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        assertThat(result.schema).isEqualTo(schema);
    }

    @Test
    void shouldDoNothingWhenJavaTypeIsNull() {
        io.swagger.v3.oas.models.media.Schema<Object> schema = new io.swagger.v3.oas.models.media.Schema<>();
        modelConverters.addConverter((annotatedType, modelConverterContext, iterator) -> schema);
        registerModelConverterWithMockObjectMapperProvider();
        AnnotatedType annotatedType = constructAnnotatedType(TestRecordWithAnnotation.class);
        annotatedType.resolveAsRef(true);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        assertThat(result.schema).isEqualTo(schema);
    }

    private void registerModelConverterWithMockObjectMapperProvider() {
        ObjectMapperProvider springDocObjectMapper = mock(ObjectMapperProvider.class);
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(springDocObjectMapper.jsonMapper())
                .thenReturn(objectMapper);
        GenericsFixingModelConverter converter = new GenericsFixingModelConverter(springDocObjectMapper);
        modelConverters.addConverter(converter);
    }

    @Test
    void shouldDoNothingWhenClassHasNoGenericTypes() {
        io.swagger.v3.oas.models.media.Schema<Object> schema = new io.swagger.v3.oas.models.media.Schema<>();
        modelConverters.addConverter((annotatedType, modelConverterContext, iterator) -> schema);
        registerModelConverter();
        AnnotatedType annotatedType = new AnnotatedType(String.class);
        annotatedType.resolveAsRef(true);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        assertThat(result.schema).isEqualTo(schema);
    }

    @Test
    void shouldDoNothingWhenRefIsEmpty() {
        io.swagger.v3.oas.models.media.Schema<Object> schema = new io.swagger.v3.oas.models.media.Schema<>();
        modelConverters.addConverter((annotatedType, modelConverterContext, iterator) -> schema);
        registerModelConverter();
        AnnotatedType annotatedType = constructAnnotatedType(TestRecordWithAnnotation.class);

        ResolvedSchema result = modelConverters.resolveAsResolvedSchema(annotatedType);

        assertThat(result.schema).isEqualTo(schema);
    }

    private record TestRecordWithoutAnnotation<T>(
            List<T> content
    ) {
    }

    @Schema(name = "MyRecord")
    private record TestRecordWithAnnotation<T>(
            List<T> content
    ) {
    }
}