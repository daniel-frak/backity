package dev.codesoapbox.backity.core.backuptarget.domain.exceptions;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;
import lombok.Getter;

import java.util.List;

@Getter
public class InvalidPathTemplatePlaceholdersException extends DomainInvariantViolationException {

    private final List<String> invalidPlaceholders;
    private final String pathTemplate;

    public InvalidPathTemplatePlaceholdersException(List<String> invalidPlaceholders, String pathTemplate) {
        this.invalidPlaceholders = invalidPlaceholders;
        this.pathTemplate = pathTemplate;

        super("The PathTemplate ('%s') contains invalid placeholders (%s)"
                .formatted(pathTemplate, invalidPlaceholders));
    }
}
