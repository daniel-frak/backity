package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem;

import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.FileResource;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionStatus;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.FileCouldNotBeDeletedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

class LocalFileSystemStorageSolutionIT {

    private LocalFileSystemStorageSolution localFileSystem;

    @BeforeEach
    void setUp() {
        localFileSystem = new LocalFileSystemStorageSolution();
    }

    private Path writeFileNextTo(Path tempDir) throws IOException {
        Path fileNextToRoot = tempDir.resolveSibling("file_next_to_%s.txt".formatted(tempDir.getFileName()));
        Files.writeString(fileNextToRoot, "This file is next to the root folder");

        return fileNextToRoot;
    }

    private FilePath filePathBypassingRootBoundary(Path root, Path fileNextToRoot) {
        return new FilePath(root.toString() + File.separator + ".." + File.separator
                + fileNextToRoot.getFileName().toString());
    }

    private void fileSizeRetrievalThrows(MockedStatic<Files> files, Path path, IOException cause) {
        files.when(() -> Files.size(path))
                .thenThrow(cause);
    }

    @Nested
    class GetId {

        @Test
        void shouldReturnId() {
            StorageSolutionId result = localFileSystem.getId();

            StorageSolutionId expectedResult = new StorageSolutionId("LOCAL_FILE_SYSTEM");
            assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class DeleteIfExists {

        @Test
        void shouldDeleteGivenExists(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");

            var existingFileCreated = new File(filePath.toString()).createNewFile();

            localFileSystem.deleteIfExists(filePath);

            var fileExists = Files.exists(Path.of(filePath.toString()));

            assertThat(existingFileCreated).isTrue();
            assertThat(fileExists).isFalse();
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldThrowGivenException(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");
            var existingFile = new File(filePath.toString());
            existingFile.createNewFile();
            tempDir.toFile().setWritable(false); // This will cause an IOException
            Files.setAttribute(existingFile.toPath(), "dos:readonly", true); // Workaround for Windows

            try {
                assertThatThrownBy(() -> localFileSystem.deleteIfExists(filePath))
                        .isInstanceOf(FileCouldNotBeDeletedException.class)
                        .hasMessageContaining(filePath.toString())
                        .hasCauseInstanceOf(IOException.class);
            } finally {
                tempDir.toFile().setWritable(true);
            }
        }

        @Test
        void shouldNotDeleteGivenDoesNotExist(@TempDir Path tempDir) {
            var filePath = new FilePath(tempDir + File.separator + "someFile");

            assertThatCode(() -> localFileSystem.deleteIfExists(filePath))
                    .doesNotThrowAnyException();
        }

        @Test
        void shouldRejectPathThatEscapesGivenRootDirectory(@TempDir Path tempDir) throws IOException {
            Path fileNextToRoot = writeFileNextTo(tempDir);
            FilePath filePathBypassingRootBoundary = filePathBypassingRootBoundary(tempDir, fileNextToRoot);

            try {
                assertThatThrownBy(() -> localFileSystem.deleteIfExists(filePathBypassingRootBoundary))
                        .isInstanceOf(FileCouldNotBeDeletedException.class);
                assertThat(Files.exists(fileNextToRoot)).isTrue();
            } finally {
                Files.deleteIfExists(fileNextToRoot);
            }
        }
    }

    @Nested
    class GetOutputStream {

        @Test
        void shouldReturnValidOutputStream(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");
            var fileContent = "Test Data";

            try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
                outputStream.write(fileContent.getBytes());
            }

            assertThatDataWasWrittenToDisk(filePath, fileContent);
        }

        private void assertThatDataWasWrittenToDisk(FilePath filePath, String fileContent) throws IOException {
            byte[] readBytes = Files.readAllBytes(Path.of(filePath.toString()));
            String readData = new String(readBytes);
            assertThat(readData).isEqualTo(fileContent);
        }

        @Test
        void shouldReturnValidOutputStreamWithSubfolders(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "subfolder" + File.separator + "someFile");
            var fileContent = "Test Data";

            try (OutputStream outputStream = localFileSystem.getOutputStream(filePath)) {
                outputStream.write(fileContent.getBytes());
            }

            assertThatDataWasWrittenToDisk(filePath, fileContent);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldFailGivenFileAlreadyExists(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");
            new File(filePath.toString()).createNewFile();

            assertThatThrownBy(() -> localFileSystem.getOutputStream(filePath))
                    .isInstanceOf(FileAlreadyExistsException.class);
        }

        @Test
        void shouldRejectPathThatEscapesGivenRootDirectory(@TempDir Path tempDir) throws IOException {
            Path outside = writeFileNextTo(tempDir);
            FilePath filePathBypassingRootBoundary = filePathBypassingRootBoundary(tempDir, outside);

            try {
                assertThatThrownBy(() -> localFileSystem.getOutputStream(filePathBypassingRootBoundary))
                        .isInstanceOf(IllegalArgumentException.class);
            } finally {
                Files.deleteIfExists(outside);
            }
        }
    }

    @Nested
    class GetSeparator {

        @Test
        void shouldReturnFileSeparator() {
            String result = localFileSystem.getSeparator();

            assertThat(result).isEqualTo(File.separator);
        }
    }

    @Nested
    class GetSizeInBytes {

        @Test
        void shouldReturnCorrectSize(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");
            String testData = "Test Data";
            try (var writer = new BufferedWriter(new FileWriter(filePath.toString()))) {
                writer.write(testData);
            }

            var sizeInBytes = localFileSystem.getSizeInBytes(filePath);

            assertThat(sizeInBytes).isEqualTo(9);
        }

        @Test
        void shouldReturnCorrectSizeGivenNestedPath(@TempDir Path tempDir) throws IOException {
            Path nestedFile = tempDir.resolve("subfolder").resolve("someFile");
            Files.createDirectories(nestedFile.getParent());
            Files.writeString(nestedFile, "Test Data");

            var sizeInBytes = localFileSystem.getSizeInBytes(new FilePath(nestedFile.toString()));

            assertThat(sizeInBytes).isEqualTo(9);
        }

        @Test
        void shouldThrowGivenFileSizeCannotBeRead() {
            Path path = Path.of("testfile.txt");
            var filePath = new FilePath(path.toString());
            var cause = new IOException("Simulated file size failure");

            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                fileSizeRetrievalThrows(files, path, cause);

                assertThatThrownBy(() -> localFileSystem.getSizeInBytes(filePath))
                        .isInstanceOf(UncheckedIOException.class)
                        .hasMessage("Could not read file size: " + filePath)
                        .hasCause(cause);
            }
        }

        @Test
        void shouldThrowGivenFileDoesNotExist(@TempDir Path tempDir) {
            Path missingFile = tempDir.resolve("missing.txt");
            var filePath = new FilePath(missingFile.toString());

            assertThatThrownBy(() -> localFileSystem.getSizeInBytes(filePath))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasMessage("Could not read file size: " + filePath)
                    .hasCauseInstanceOf(NoSuchFileException.class);
        }

        @Test
        void shouldRejectPathThatEscapesGivenRootDirectory(@TempDir Path tempDir) throws IOException {
            Path fileNextToRoot = writeFileNextTo(tempDir);
            FilePath filePathBypassingRootBoundary = filePathBypassingRootBoundary(tempDir, fileNextToRoot);

            try {
                assertThatThrownBy(() -> localFileSystem.getSizeInBytes(filePathBypassingRootBoundary))
                        .isInstanceOf(IllegalArgumentException.class);
            } finally {
                Files.deleteIfExists(fileNextToRoot);
            }
        }
    }

    @Nested
    class GetFileResource {

        @Test
        void shouldReturnFileResourceGivenFileExists(@TempDir Path tempDir) throws IOException {
            String fileName = "testfile.txt";
            Path tempFile = tempDir.resolve(fileName);
            Files.write(tempFile, "Test content".getBytes());
            var filePath = new FilePath(tempFile.toString());

            try (FileResource fileResource = localFileSystem.getFileResource(filePath)) {
                assertThat(fileResource).isNotNull();
                assertThat(fileResource.sizeInBytes()).isPositive();
                assertThat(fileResource.fileName()).isEqualTo(fileName);
                assertThatCode(fileResource::close)
                        .doesNotThrowAnyException();
            }
        }

        @Test
        void shouldReturnFileResourceGivenNestedPath(@TempDir Path tempDir) throws IOException {
            Path nestedFile = tempDir.resolve("subfolder").resolve("someFile");
            Files.createDirectories(nestedFile.getParent());
            Files.writeString(nestedFile, "Test content");

            try (FileResource fileResource = localFileSystem.getFileResource(new FilePath(nestedFile.toString()))) {
                assertThat(fileResource).isNotNull();
                assertThat(fileResource.fileName()).isEqualTo("someFile");
            }
        }

        @Test
        void shouldThrowGivenFileNotFound() {
            var filePath = new FilePath("nonexistentfile.txt");

            assertThatThrownBy(() -> localFileSystem.getFileResource(filePath).close())
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + filePath);
        }

        @Test
        void shouldThrowGivenFileIsDirectory(@TempDir Path tempDir) throws IOException {
            Path tempDirectory = tempDir.resolve("tempDirectory");
            Files.createDirectory(tempDirectory);
            var filePath = new FilePath(tempDirectory.toString());

            assertThatThrownBy(() -> localFileSystem.getFileResource(filePath).close())
                    .isInstanceOf(FileNotFoundException.class)
                    .hasMessage("File not found: " + tempDirectory);
        }

        @Test
        void shouldThrowGivenFileCannotBeOpened() {
            Path path = Path.of("testfile.txt");
            var filePath = new FilePath(path.toString());
            var cause = new IOException("Simulated read failure");

            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                regularFileCheckWorks(files, path);
                fileSizeRetrievalWorks(files, path);
                inputStreamCreationThrows(files, path, cause);

                assertThatThrownBy(() -> localFileSystem.getFileResource(filePath))
                        .isInstanceOf(FileNotFoundException.class)
                        .hasMessage("Could not read file: " + filePath)
                        .hasCause(cause);
            }
        }

        @SuppressWarnings("resource")
        private void inputStreamCreationThrows(MockedStatic<Files> files, Path path, IOException cause) {
            files.when(() -> Files.newInputStream(path))
                    .thenThrow(cause);
        }

        private void fileSizeRetrievalWorks(MockedStatic<Files> files, Path path) {
            files.when(() -> Files.size(path))
                    .thenReturn(12L);
        }

        private void regularFileCheckWorks(MockedStatic<Files> files, Path path) {
            files.when(() -> Files.isRegularFile(path))
                    .thenReturn(true);
        }

        @Test
        void shouldThrowGivenFileSizeCannotBeRead() {
            Path path = Path.of("testfile.txt");
            var filePath = new FilePath(path.toString());
            var cause = new IOException("Simulated file size failure");

            try (MockedStatic<Files> files = mockStatic(Files.class)) {
                regularFileCheckWorks(files, path);
                fileSizeRetrievalThrows(files, path, cause);

                assertThatThrownBy(() -> localFileSystem.getFileResource(filePath))
                        .isInstanceOf(FileNotFoundException.class)
                        .hasMessage("Could not read file: " + filePath)
                        .hasCause(cause);
            }
        }

        @SuppressWarnings("EmptyTryBlock")
        @Test
        void shouldRejectPathThatEscapesGivenRootDirectory(@TempDir Path tempDir) throws IOException {
            Path fileNextToRoot = writeFileNextTo(tempDir);
            FilePath filePathBypassingRootBoundary = filePathBypassingRootBoundary(tempDir, fileNextToRoot);

            try {
                assertThatThrownBy(() -> {
                    try (var _ = localFileSystem.getFileResource(filePathBypassingRootBoundary)) {
                        // Try-catch to close if the adapter incorrectly opens the escaped path.
                    }
                }).isInstanceOf(IllegalArgumentException.class);
            } finally {
                Files.deleteIfExists(fileNextToRoot);
            }
        }
    }

    @Nested
    class FileExists {

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Test
        void shouldReturnTrueGivenFileExists(@TempDir Path tempDir) throws IOException {
            var filePath = new FilePath(tempDir + File.separator + "someFile");
            new File(filePath.toString()).createNewFile();

            boolean result = localFileSystem.fileExists(filePath);

            assertThat(result).isTrue();
        }

        @Test
        void shouldReturnFalseGivenFileDoesNotExist(@TempDir Path tempDir) {
            var nonExistentFilePath = new FilePath(tempDir + File.separator + "someFile");

            boolean result = localFileSystem.fileExists(nonExistentFilePath);

            assertThat(result).isFalse();
        }

        @Test
        void shouldReturnTrueGivenNestedFileExists(@TempDir Path tempDir) throws IOException {
            Path nestedFile = tempDir.resolve("subfolder").resolve("someFile");
            Files.createDirectories(nestedFile.getParent());
            Files.createFile(nestedFile);

            boolean result = localFileSystem.fileExists(new FilePath(nestedFile.toString()));

            assertThat(result).isTrue();
        }

        @Test
        void shouldRejectPathThatEscapesGivenRootDirectory(@TempDir Path tempDir) throws IOException {
            Path fileNextToRoot = writeFileNextTo(tempDir);
            FilePath filePathBypassingRootBoundary = filePathBypassingRootBoundary(tempDir, fileNextToRoot);

            try {
                assertThatThrownBy(() -> localFileSystem.fileExists(filePathBypassingRootBoundary))
                        .isInstanceOf(IllegalArgumentException.class);
            } finally {
                Files.deleteIfExists(fileNextToRoot);
            }
        }
    }

    @Nested
    class GetStatus {

        @Test
        void shouldReturnConnected() {
            StorageSolutionStatus result = localFileSystem.getStatus();

            assertThat(result).isEqualTo(StorageSolutionStatus.CONNECTED);
        }
    }
}
