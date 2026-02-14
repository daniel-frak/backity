package dev.codesoapbox.backity.core.backuptarget.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backuptarget.domain.exceptions.InvalidPathTemplatePlaceholdersException;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathTemplateTest {

    private static final String CHARACTERS_TO_REMOVE = "<>\"|?\n`';!@#$%^*[]~";
    private static final PathTemplate PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS =
            new PathTemplate("/test/{GAME_PROVIDER_ID}/{GAME_TITLE}/{FILENAME}");

    @Nested
    class Constructor {

        @SuppressWarnings("DataFlowIssue")
        @Test
        void shouldThrowGivenNullValue() {
            assertThatThrownBy(() -> new PathTemplate(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("value");
        }

        @Test
        void shouldReturnPathTemplateGivenValidValue() {
            var value = PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS.value();

            var result = new PathTemplate(value);

            assertThat(result.value()).isEqualTo(value);
        }

        /*
        We want to keep the unsanitized value so that we don't lose it in case of sanitization bugs.
         */
        @Test
        void shouldNotSanitizeValueInConstructor() {
            var unsanitizedValue = PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS.value() + CHARACTERS_TO_REMOVE;

            var result = new PathTemplate(unsanitizedValue);

            assertThat(result.value()).isEqualTo(unsanitizedValue);
        }

        @Test
        void shouldThrowGivenInvalidPlaceholdersInValue() {
            String valueWithInvalidPlaceholderSanitized = "/test/{INVALID_PLACEHOLDER}/{FILENAME}";
            var valueWithInvalidPlaceholder = valueWithInvalidPlaceholderSanitized + CHARACTERS_TO_REMOVE;

            assertThatThrownBy(() -> new PathTemplate(valueWithInvalidPlaceholder))
                    .asInstanceOf(InstanceOfAssertFactories.type(InvalidPathTemplatePlaceholdersException.class))
                    .satisfies(e -> {
                        assertThat(e.getInvalidPlaceholders()).containsOnly("INVALID_PLACEHOLDER");
                        assertThat(e.getPathTemplate()).isEqualTo(valueWithInvalidPlaceholderSanitized);
                    });
        }

        /*
        We want to make sure we validate the actual value that will be used when constructing the file path,
        not the initial one, in case the sanitization meaningfully changes it.
         */
        @Test
        void shouldValidateSanitizedValue() {
            String valueWithInvalidPlaceholderSanitized = "/test/{INVALID_PLACEHOLDER}/{FILENAME}";
            var valueWithInvalidPlaceholder = valueWithInvalidPlaceholderSanitized + CHARACTERS_TO_REMOVE;

            assertThatThrownBy(() -> new PathTemplate(valueWithInvalidPlaceholder))
                    .asInstanceOf(InstanceOfAssertFactories.type(InvalidPathTemplatePlaceholdersException.class))
                    .satisfies(e ->
                            assertThat(e.getPathTemplate()).isEqualTo(valueWithInvalidPlaceholderSanitized));
        }
    }

    @Nested
    class ConstructFilePath {

        @Nested
        class BasicFilePathConstruction {

            @Test
            void shouldResolveAllPlaceholders() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .gameProviderId(new GameProviderId("someGameProviderId"))
                        .originalGameTitle("someGameTitle")
                        .originalFileName("someFileName")
                        .build();

                String result =
                        PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldAlwaysAddFileExtensionAtTheEndOfThePath() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalFileName("someFileName.txt")
                        .build();
                var pathTemplate = new PathTemplate("{FILENAME}_suffix");

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "someFileName_suffix.txt";

                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldNotThrowGivenNoFileExtensionInFileName() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalFileName("someFileName")
                        .build();
                var pathTemplate = new PathTemplate("/test/{FILENAME}");

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/someFileName";

                assertThat(result).isEqualTo(expectedPath);
            }

            @ParameterizedTest
            @CsvSource({
                    "/test/",
                    "/test"
            })
            void shouldAddFileNameAtEndGivenFileNamePlaceholderIsMissing(String pathTemplateValue) {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalFileName("someFileName")
                        .build();
                var pathTemplate = new PathTemplate(pathTemplateValue);

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/someFileName";

                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldResolveFilePathWhenNoSeparatorInPathTemplate() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .gameProviderId(new GameProviderId("someGameProviderId"))
                        .originalGameTitle("someGameTitle")
                        .originalFileName("someFileName")
                        .build();
                var pathTemplate = new PathTemplate("{FILENAME}");

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "someFileName";
                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldResolveValidFilePathGivenWrongSeparatorInPathTemplate() {
                var wrongPathTemplate = new PathTemplate("{GAME_TITLE}\\{FILENAME}");
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalGameTitle("someGameTitle")
                        .originalFileName("someFileName")
                        .build();

                String result = wrongPathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "someGameTitle/someFileName";
                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldAddSuffixGivenSuffixIndexGreaterThanZero() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .gameProviderId(new GameProviderId("someGameProviderId"))
                        .originalGameTitle("someGameTitle")
                        .originalFileName("someFileName.exe")
                        .build();
                var pathTemplate = new PathTemplate("{FILENAME}");

                String result = pathTemplate.constructPath(sourceFile, "/", 1);

                String expectedPath = "someFileName_1.exe";
                assertThat(result).isEqualTo(expectedPath);
            }
        }

        @Nested
        class FilePathSanitization {

            @Test
            void shouldRemoveIllegalCharactersFromPathTemplate() {
                var pathTemplate = new PathTemplate(
                        "/test" + CHARACTERS_TO_REMOVE + "/{FILENAME}" + CHARACTERS_TO_REMOVE);
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalFileName("someFileName")
                        .build();

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/someFileName";
                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldReplaceIllegalCharactersFromPathTemplate() {
                var pathTemplate = new PathTemplate("/some:test\tfolder 1 & 2/{FILENAME}");
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .originalFileName("someFileName")
                        .build();

                String result = pathTemplate.constructPath(sourceFile, "/", 0);

                String expectedPath = "/some -test folder 1 and 2/someFileName";
                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldRemoveIllegalCharactersFromEachPlaceholderValue() {
                String charactersToRemove = CHARACTERS_TO_REMOVE + "{}\\/";
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .gameProviderId(new GameProviderId("someGameProviderId" + charactersToRemove))
                        .originalGameTitle("someGameTitle" + charactersToRemove)
                        .originalFileName("someFileName" + charactersToRemove)
                        .build();

                String result =
                        PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/someGameProviderId/someGameTitle/someFileName";

                assertThat(result).isEqualTo(expectedPath);
            }

            @Test
            void shouldReplaceIllegalCharactersFromEachPlaceholderValue() {
                SourceFile sourceFile = TestSourceFile.gogBuilder()
                        .gameProviderId(new GameProviderId("some:Game\tProviderId 1"))
                        .originalGameTitle("some:Game\tTitle 1")
                        .originalFileName("some:File\tName 1")
                        .build();

                String result =
                        PATH_TEMPLATE_WITH_ALL_PLACEHOLDERS.constructPath(sourceFile, "/", 0);

                String expectedPath = "/test/some -Game ProviderId 1/some -Game Title 1/some -File Name 1";

                assertThat(result).isEqualTo(expectedPath);
            }
        }
    }
}