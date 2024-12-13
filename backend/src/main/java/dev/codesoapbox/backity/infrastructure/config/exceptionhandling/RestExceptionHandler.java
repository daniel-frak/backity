package dev.codesoapbox.backity.infrastructure.config.exceptionhandling;

import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.exceptionhandling.ErrorMessageHttpDto;
import dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.exceptionhandling.ValidationErrorHttpDto;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private final Map<Class<? extends Throwable>, String> messageKeysByExceptionClass = Map.of(
            GameFileNotBackedUpException.class, "GAME_FILE_NOT_BACKED_UP"
    );

    @ExceptionHandler(RuntimeException.class)
    @ApiResponse(responseCode = "500", description = "An internal error occurred")
    public ResponseEntity<ErrorMessageHttpDto> handleGeneralErrors(RuntimeException ex) {
        String errorId = UUID.randomUUID().toString();
        log.error("An internal error occurred (id=" + errorId + "): ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageHttpDto(errorId, null, null));
    }

    @ExceptionHandler(DomainInvariantViolationException.class)
    @ApiResponse(responseCode = "422",
            description = "The request was well-formed, but the parameters were contradictory, invalid, " +
                          "or otherwise unable to be processed")
    public ResponseEntity<ErrorMessageHttpDto> handleDomainErrors(DomainInvariantViolationException ex) {
        log.warn("A domain error occurred: ", ex);
        String messageKey = null;
        if (messageKeysByExceptionClass.containsKey(ex.getClass())) {
            messageKey = messageKeysByExceptionClass.get(ex.getClass());
        } else if (ex.getCause() != null && messageKeysByExceptionClass.containsKey(ex.getCause().getClass())) {
            messageKey = messageKeysByExceptionClass.get(ex.getCause().getClass());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorMessageHttpDto(null, ex.getMessage(), messageKey));
    }

    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    @ApiResponse(responseCode = "409", description = "A request conflicts with the current state of the server")
    public ResponseEntity<ErrorMessageHttpDto> handleOptimisticLockException(RuntimeException exception) {
        log.warn("An optimistic lock exception has occurred", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessageHttpDto(
                        null,
                        "The resource has been modified by another user. Please refresh and try again.",
                        null));
    }

    @ApiResponse(responseCode = "400", description = "The server cannot or will not process the request due to"
                                                     + " something that is perceived to be a client error",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorHttpDto.class))))
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<ValidationErrorHttpDto> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = null;
                    if (error instanceof FieldError fieldError) {
                        field = fieldError.getField();
                    }
                    return new ValidationErrorHttpDto(
                            field,
                            error.getDefaultMessage());
                })
                .toList();
        return this.handleExceptionInternal(ex, errors, headers, status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessageHttpDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ErrorMessageHttpDto(null, exception.getMessage(), null));
    }
}
