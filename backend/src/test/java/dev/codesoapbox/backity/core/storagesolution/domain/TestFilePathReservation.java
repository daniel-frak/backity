package dev.codesoapbox.backity.core.storagesolution.domain;

public class TestFilePathReservation {

    public static FilePathReservation of(FilePath filePath) {
        return new FilePathReservation() {
            @Override
            public FilePath get() {
                return filePath;
            }

            @Override
            public void close() {
                // Do nothing
            }
        };
    }

    public static FilePathReservation withCloseCallback(FilePath filePath, Runnable onClose) {
        return new FilePathReservation() {
            @Override
            public FilePath get() {
                return filePath;
            }

            @Override
            public void close() {
                onClose.run();
            }
        };
    }
}