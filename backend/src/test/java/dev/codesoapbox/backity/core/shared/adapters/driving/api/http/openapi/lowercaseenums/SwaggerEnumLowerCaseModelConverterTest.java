package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums;

import com.fasterxml.jackson.databind.type.SimpleType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Parameter;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwaggerEnumLowerCaseModelConverterTest {

    private SwaggerEnumLowerCaseModelConverter modelConverter;

    @Mock
    private Iterator<ModelConverter> iterator;

    @Mock
    private ModelConverterContext modelConverterContext;

    private static Stream<Arguments> annotatedTypes() {
        return Stream.of(
                Arguments.of("simple type",
                        getAnnotatedTypeFromArgumentAsSimpleType("testMethodWithAnnotation",
                                EnumWithAnnotation.class), "EnumWithAnnotation"),
                Arguments.of("simple type",
                        getAnnotatedTypeFromArgumentAsSimpleType("testMethodWithAnnotationAndSchemaName",
                                EnumWithAnnotationAndSchemaName.class), "RenamedEnum"),
                Arguments.of("class",
                        getAnnotatedTypeFromArgumentAsClass("testMethodWithAnnotationAndEmptySchema",
                                EnumWithAnnotationAndEmptySchema.class), "EnumWithAnnotationAndEmptySchema"),
                Arguments.of("class",
                        getAnnotatedTypeFromArgumentAsClass("testMethodWithAnnotation",
                                EnumWithAnnotation.class), "EnumWithAnnotation")
        );
    }

    @SneakyThrows
    private static AnnotatedType getAnnotatedTypeFromArgumentAsSimpleType(String methodName, Class<?> type) {
        Parameter parameter = TestController.class.getDeclaredMethod(methodName, type)
                .getParameters()[0];
        var annotatedType = new AnnotatedType(SimpleType.constructUnsafe(type));
        annotatedType.setCtxAnnotations(parameter.getAnnotations());
        return annotatedType;
    }

    @SneakyThrows
    private static AnnotatedType getAnnotatedTypeFromArgumentAsClass(String methodName, Class<?> type) {
        Parameter parameter = TestController.class.getDeclaredMethod(methodName, type)
                .getParameters()[0];
        var annotatedType = new AnnotatedType(type);
        annotatedType.setCtxAnnotations(parameter.getAnnotations());
        return annotatedType;
    }

    @BeforeEach
    void setUp() {
        modelConverter = new SwaggerEnumLowerCaseModelConverter();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("annotatedTypes")
    void shouldResolveWithAnnotationGiven(String testName, AnnotatedType annotatedType, String schemaName) {
        Schema<?> result = modelConverter.resolve(annotatedType, modelConverterContext, iterator);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(StringSchema.class)
                .extracting(r -> ((StringSchema) r).getEnum())
                .asInstanceOf(InstanceOfAssertFactories.LIST)
                .hasSize(2)
                .contains("value1", "value2");
        assertThat(result.getName()).isEqualTo(schemaName);
    }

    @Test
    void shouldDelegateResolutionWithoutAnnotation() {
        AnnotatedType annotatedType = getAnnotatedTypeFromArgumentAsSimpleType("testMethodWithoutAnnotation",
                PlainEnum.class);

        Schema<?> expectedResult = mock(Schema.class);
        mockDelegation(annotatedType, expectedResult);

        Schema<?> result = modelConverter.resolve(annotatedType, modelConverterContext, iterator);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
    }

    private void mockDelegation(AnnotatedType annotatedType, Schema<?> expectedResult) {
        when(iterator.hasNext()).thenReturn(true);
        ModelConverter nextModelConverterMock = mock(ModelConverter.class);
        when(iterator.next()).thenReturn(nextModelConverterMock);
        when(nextModelConverterMock.resolve(annotatedType, modelConverterContext, iterator))
                .thenReturn(expectedResult);
    }

    @Test
    void shouldDelegateResolutionWhenNotEnum() {
        AnnotatedType annotatedType = getAnnotatedTypeFromArgumentAsSimpleType(
                "testMethodWithoutEnumWithAnnotation", TestRecord.class);

        Schema<?> expectedResult = mock(Schema.class);
        mockDelegation(annotatedType, expectedResult);

        Schema<?> result = modelConverter.resolve(annotatedType, modelConverterContext, iterator);

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnNullWhenIteratorEmpty() {
        AnnotatedType annotatedType = getAnnotatedTypeFromArgumentAsSimpleType(
                "testMethodWithoutEnumWithoutAnnotation", Object.class);

        when(iterator.hasNext()).thenReturn(false);

        Schema<?> result = modelConverter.resolve(annotatedType, modelConverterContext, iterator);

        assertThat(result)
                .isNull();
    }

    @LowercaseApiEnum
    private enum EnumWithAnnotation {
        VALUE1,
        VALUE2
    }

    private enum PlainEnum {
        VALUE1,
        VALUE2
    }

    @io.swagger.v3.oas.annotations.media.Schema(name = "RenamedEnum")
    @LowercaseApiEnum
    private enum EnumWithAnnotationAndSchemaName {
        VALUE1,
        VALUE2
    }

    @io.swagger.v3.oas.annotations.media.Schema
    @LowercaseApiEnum
    private enum EnumWithAnnotationAndEmptySchema {
        VALUE1,
        VALUE2
    }

    @LowercaseApiEnum
    private record TestRecord(String text) {
    }

    @SuppressWarnings("unused")
    private static class TestController {

        public void testMethodWithAnnotation(EnumWithAnnotation enumParam) {
            // Implementation not necessary
        }

        public void testMethodWithAnnotationAndSchemaName(EnumWithAnnotationAndSchemaName enumParam) {
            // Implementation not necessary
        }

        public void testMethodWithAnnotationAndEmptySchema(EnumWithAnnotationAndEmptySchema enumParam) {
            // Implementation not necessary
        }

        public void testMethodWithoutAnnotation(PlainEnum enumParam) {
            // Implementation not necessary
        }

        public void testMethodWithoutEnumWithAnnotation(TestRecord objectParam) {
            // Implementation not necessary
        }

        public void testMethodWithoutEnumWithoutAnnotation(Object objectParam) {
            // Implementation not necessary
        }
    }
}