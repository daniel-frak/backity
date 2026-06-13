package dev.codesoapbox.backity.core.storagesolution.domain;

/// Represents a reserved unique file path for a file which might not exist yet.
///
/// Solves the problem of ensuring uniqueness of file paths among in-progress backups.
///
/// Must be closed after the file is created to free up memory.
public interface FilePathReservation extends AutoCloseable {

    FilePath get();

    @Override
    void close();
}
