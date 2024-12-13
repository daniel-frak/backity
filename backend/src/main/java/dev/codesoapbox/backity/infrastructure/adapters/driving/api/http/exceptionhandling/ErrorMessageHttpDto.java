package dev.codesoapbox.backity.infrastructure.adapters.driving.api.http.exceptionhandling;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorMessage")
public record ErrorMessageHttpDto(

        @Schema(description = "An optional error id that will uniquely identify the error" +
                " can be presented to the user")
        String errorId,
        @Schema(description = "An optional message describing the error (should not be shown to the user)")
        String message,
        @Schema(description = "An optional message key describing the error." +
                " Can be used to show the user relevant information about the error.")
        String messageKey
) {
}
