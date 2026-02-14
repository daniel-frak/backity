package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.InvalidPathTemplatePlaceholdersException;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StringSanitizer;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record PathTemplate(@NonNull String value) {

    private static final String FILENAME_PLACEHOLDER_NAME = "FILENAME";

    private static final Map<String, Function<Context, String>> PATH_TEMPLATE_PARSERS = Map.of(
            "GAME_PROVIDER_ID",
            pathTemplateContext -> pathTemplateContext.gameProviderId().value(),
            "GAME_TITLE", Context::gameTitle,

            /*
            This is useful for adding prefixes or suffixes to the filename, e.g.,
            {GAME_PROVIDER_ID}_{FILENAME} or (maybe in the future) {FILENAME}_{VERSION}.
             */
            FILENAME_PLACEHOLDER_NAME, Context::targetFileNameWithoutExtension
    );
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    private static final Set<Character> CHARACTERS_TO_REMOVE_FROM_TEMPLATE = Set.of(
            '<', '>', '"', '|', '?', '\n', '`', ';', '!', '@', '#', '$', '%', '^', '*', '[', ']', '~', '\''
    );
    private static final List<StringSanitizer.StringReplacement> CHARACTERS_TO_REPLACE = List.of(
            new StringSanitizer.StringReplacement(":", " -"),
            new StringSanitizer.StringReplacement("\t", " "),
            new StringSanitizer.StringReplacement("&", "and")
    );
    private static final StringSanitizer PATH_TEMPLATE_SANITIZER = new StringSanitizer(
            CHARACTERS_TO_REMOVE_FROM_TEMPLATE, CHARACTERS_TO_REPLACE);
    private static final StringSanitizer PLACEHOLDER_SANITIZER = PATH_TEMPLATE_SANITIZER
            .withAdditionalCharactersToRemove(Set.of('{', '}', '\\', '/'));

    public PathTemplate {
        // We don't store the sanitized value so that we always have the original in case of sanitization bugs.
        String sanitizedValue = sanitize(value);
        validateAllPlaceholdersAreValid(sanitizedValue);
    }

    private void validateAllPlaceholdersAreValid(String sanitizedValue) {
        List<String> invalidPlaceholders = new ArrayList<>();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(sanitizedValue);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            if (!PATH_TEMPLATE_PARSERS.containsKey(placeholder)) {
                invalidPlaceholders.add(placeholder);
            }
        }
        if (!invalidPlaceholders.isEmpty()) {
            throw new InvalidPathTemplatePlaceholdersException(invalidPlaceholders, sanitizedValue);
        }
    }

    public String constructPath(SourceFile sourceFile, String targetSeparator, int suffixIndex) {
        String pathTemplateValue = replaceWithCorrectFileSeparator(value, targetSeparator);
        String sanitizedPathTemplateValue = sanitize(pathTemplateValue);
        sanitizedPathTemplateValue = addFileNamePlaceholderIfMissing(sanitizedPathTemplateValue, targetSeparator);

        return resolvePathVariables(sanitizedPathTemplateValue, sourceFile, suffixIndex);
    }

    private String addFileNamePlaceholderIfMissing(String sanitizedPathTemplateValue, String targetSeparator) {
        if (!sanitizedPathTemplateValue.contains("{" + FILENAME_PLACEHOLDER_NAME + "}")) {
            if (!sanitizedPathTemplateValue.endsWith(targetSeparator)) {
                sanitizedPathTemplateValue += targetSeparator;
            }
            sanitizedPathTemplateValue += "{" + FILENAME_PLACEHOLDER_NAME + "}";
        }
        return sanitizedPathTemplateValue;
    }

    private String resolvePathVariables(String sanitizedPathTemplateValue, SourceFile sourceFile, int suffixIndex) {
        Context context = Context.from(this, sourceFile, suffixIndex);
        String pathWithResolvedVariables = PLACEHOLDER_PATTERN.matcher(sanitizedPathTemplateValue)
                .replaceAll(match -> resolvePathVariable(match, context));

        return pathWithResolvedVariables + context.extensionWithDot();
    }

    private String resolvePathVariable(MatchResult match, Context context) {
        String key = match.group(1);
        String value = resolvePathVariable(key, context);
        String sanitizedValue = PLACEHOLDER_SANITIZER.sanitize(value);

        // While we don't technically need to use Matcher.quoteReplacement (as the sanitizer removes $ and \)
        // this gives us an extra bit of safety in case the sanitization rules change:
        return Matcher.quoteReplacement(sanitizedValue);
    }

    private String resolvePathVariable(String key, Context context) {
        Function<Context, String> parser = PATH_TEMPLATE_PARSERS.get(key);

        return parser.apply(context);
    }

    private String sanitize(String value) {
        return PATH_TEMPLATE_SANITIZER.sanitize(value);
    }

    private String replaceWithCorrectFileSeparator(String pathTemplate, String targetSeparator) {
        return pathTemplate
                .replace("/", targetSeparator)
                .replace("\\", targetSeparator);
    }

    private record Context(
            PathTemplate pathTemplate,
            GameProviderId gameProviderId,
            String gameTitle,
            String baseName,
            String extensionWithDot,
            int suffixIndex
    ) {

        static Context from(PathTemplate pathTemplate, SourceFile sourceFile, int suffixIndex) {
            String fileName = sourceFile.getOriginalFileName();
            String baseName = getBaseName(fileName);
            String extensionWithDot = fileName.substring(baseName.length());

            return new Context(
                    pathTemplate,
                    sourceFile.getGameProviderId(),
                    sourceFile.getOriginalGameTitle(),
                    baseName,
                    extensionWithDot,
                    suffixIndex
            );
        }

        private static String getBaseName(String fileName) {
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex != -1) {
                return fileName.substring(0, dotIndex);
            }
            return fileName;
        }

        public String targetFileNameWithoutExtension() {
            return (suffixIndex > 0)
                    ? (baseName() + "_" + suffixIndex)
                    : baseName();
        }
    }
}
