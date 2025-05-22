package dev.codesoapbox.backity.shared.infrastructure.config.exceptionhandling;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;
import dev.codesoapbox.backity.testing.matchers.UuidMatcher;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class RestExceptionHandlerIT {

    private MockMvc mockMvc;
    private TestController testController;

    @BeforeEach
    void setup() {
        testController = mock(TestController.class);
        this.mockMvc = standaloneSetup(testController)
                .setControllerAdvice(RestExceptionHandler.class)
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldHandleGeneralErrors() throws Exception {
        when(testController.testEndpoint()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorId").value(UuidMatcher.isUuid()));
    }

    @Test
    void shouldHandleDomainInvariantViolationExceptionsWithNoCause() throws Exception {
        String expectedMessage = "Domain error message";
        when(testController.testEndpoint()).thenThrow(new DomainInvariantViolationException(expectedMessage));

        mockMvc.perform(get("/"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldHandleDomainInvariantViolationExceptionsWithCause() throws Exception {
        String expectedMessage = "Domain error message";
        when(testController.testEndpoint())
                .thenThrow(new DomainInvariantViolationException(expectedMessage, new RuntimeException()));

        mockMvc.perform(get("/"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldHandleDomainInvariantViolationExceptionsWithNoCauseWithMessageKeys() throws Exception {
        var expectedException = new FileCopyNotBackedUpException(FileCopyId.newInstance());
        when(testController.testEndpoint())
                .thenThrow(expectedException);

        mockMvc.perform(get("/"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(expectedException.getMessage()))
                .andExpect(jsonPath("$.messageKey").value("FILE_COPY_NOT_BACKED_UP"));
    }

    @Test
    void shouldHandleDomainInvariantViolationExceptionsWithCauseWithMessageKeys() throws Exception {
        String expectedMessage = "Domain error message";
        when(testController.testEndpoint()).thenThrow(new DomainInvariantViolationException(expectedMessage,
                new FileCopyNotBackedUpException(FileCopyId.newInstance())));

        mockMvc.perform(get("/"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andExpect(jsonPath("$.messageKey").value("FILE_COPY_NOT_BACKED_UP"));
    }

    @Test
    void shouldHandleIllegalArgumentExceptions() throws Exception {
        String expectedMessage = "Test message";
        when(testController.testEndpoint()).thenThrow(new IllegalArgumentException(expectedMessage));

        mockMvc.perform(get("/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldHandleOptimisticLockExceptions() throws Exception {
        when(testController.testEndpoint()).thenThrow(new OptimisticLockException());

        String expectedMessage = "The resource has been modified by another user. Please refresh and try again.";
        mockMvc.perform(get("/"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldHandleObjectOptimisticLockingFailureExceptions() throws Exception {
        when(testController.testEndpoint()).thenThrow(new ObjectOptimisticLockingFailureException("",
                new RuntimeException()));

        String expectedMessage = "The resource has been modified by another user. Please refresh and try again.";
        mockMvc.perform(get("/"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void shouldHandleMethodArgumentNotValidExceptions() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new ObjectError("objectName1", "defaultMessage1"),
                new FieldError("objectName2", "field2", "defaultMessage2")
        ));
        MethodParameter methodParameter = mock(MethodParameter.class);

        // Can't mock Executable.class so just use anything as a stand-in:
        when(methodParameter.getExecutable()).thenReturn(this.getClass().getDeclaredConstructor());

        when(testController.testEndpoint()).thenThrow(new MethodArgumentNotValidException(
                methodParameter, bindingResult));

        mockMvc.perform(get("/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].fieldName").doesNotExist())
                .andExpect(jsonPath("$[0].message").value("defaultMessage1"))
                .andExpect(jsonPath("$[1].fieldName").value("field2"))
                .andExpect(jsonPath("$[1].message").value("defaultMessage2"));
    }

    @RestController
    @RequestMapping
    private interface TestController {

        @GetMapping
        String testEndpoint() throws MethodArgumentNotValidException;
    }
}