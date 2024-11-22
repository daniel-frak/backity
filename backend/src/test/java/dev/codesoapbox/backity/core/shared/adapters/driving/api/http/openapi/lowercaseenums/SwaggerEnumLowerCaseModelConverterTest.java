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
                                TestEnum1.class), "TestEnum1"),
                Arguments.of("simple type",
                        getAnnotatedTypeFromArgumentAsSimpleType("testMethodWithAnnotationWithSchemaName",
                                TestEnum3.class), "RenamedEnum"),
                Arguments.of("class",
                        getAnnotatedTypeFromArgumentAsClass("testMethodWithAnnotation",
                                TestEnum1.class), "TestEnum1")
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
                TestEnum2.class);

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
    private enum TestEnum1 {
        VALUE1,
        VALUE2
    }

    private enum TestEnum2 {
        VALUE1,
        VALUE2
    }

    @io.swagger.v3.oas.annotations.media.Schema(name = "RenamedEnum")
    @LowercaseApiEnum
    private enum TestEnum3 {
        VALUE1,
        VALUE2
    }

    @LowercaseApiEnum
    private record TestRecord(String text) {
    }

    @SuppressWarnings("unused")
    private static class TestController {

        public void testMethodWithAnnotation(TestEnum1 enumParam) {
        }

        public void testMethodWithAnnotationWithSchemaName(TestEnum3 enumParam) {
        }

        public void testMethodWithoutAnnotation(TestEnum2 enumParam) {
        }

        public void testMethodWithoutEnumWithAnnotation(TestRecord objectParam) {
        }

        public void testMethodWithoutEnumWithoutAnnotation(Object objectParam) {
        }
    }
}