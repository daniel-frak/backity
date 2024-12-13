package dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.lowercaseenums;

import dev.codesoapbox.backity.shared.adapters.driving.api.http.lowercaseenums.openapi.LowercaseApiEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LowercaseEnumFinderTest {

    @Mock
    private ApplicationContext applicationContext;

    private LowercaseEnumFinder enumFinder;

    @BeforeEach
    void setUp() {
        enumFinder = new LowercaseEnumFinder(applicationContext);
    }

    @Test
    void testGetAnnotatedControllerEnums() {
        when(applicationContext.getBeanNamesForAnnotation(RestController.class))
                .thenReturn(new String[]{"testController"});
        doReturn(TestController.class)
                .when(applicationContext).getType("testController");

        List<Class<?>> result = enumFinder.getAnnotatedControllerEnums();

        assertThat(result)
                .hasSize(2)
                .hasSameElementsAs(Set.of(TestEnum1.class, TestEnum3.class));
    }

    @LowercaseApiEnum
    enum TestEnum1 {
        VALUE1
    }

    enum TestEnum2 {
        VALUE2
    }

    @LowercaseApiEnum
    enum TestEnum3 {
        VALUE3
    }

    static class TestController {

        public void testMethod1(TestEnum1 enumParam) {
            // Implementation not necessary
        }

        public void testMethod2(TestEnum2 enumParam1, TestEnum3 enumParam2) {
            // Implementation not necessary
        }
    }
}