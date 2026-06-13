package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/// Provides file paths which are unique within a storage solution at the time of resolution.
///
/// Prevents the same path from being resolved multiple times concurrently by temporarily reserving resolved paths
/// until the returned [FilePathReservation] is closed.
///
/// # Concurrency warning
///
/// File path reservations are maintained in memory. Therefore, uniqueness cannot be guaranteed
/// across multiple instances of this class or multiple instances of the application.
public class UniqueFilePathResolver {

    private static final int MAX_ATTEMPTS = 1000;

    // Ensures that the same file path is not resolved multiple times concurrently
    private final Set<IdentifiableFilePathReservation> filePathReservations = new HashSet<>();

    /*
    The current implementation of resolve() only reserves the file path at the very end.
    To be truly thread-safe, resolve() would need to be changed so that each candidate file path
    is preemptively reserved. Otherwise, two different resolve() calls might resolve to the same path.

    Given the low frequency of calls to resolve(), it was easier to just add a ReentrantLock to ensure path resolution
    happens sequentially.
     */
    private final Lock reservationLock = new ReentrantLock();

    /*
    Warning! This alone is not enough to ensure uniqueness if files are backed up concurrently.
    In that case, two or more files might resolve to the same path, as neither exists in the storage solution yet.
     */
    @DoNotMutate // I was unable to figure out how to test concurrency on this class
    public FilePathReservation resolve(
            PathTemplate pathTemplate, SourceFile sourceFile, StorageSolution storageSolution) {
        reservationLock.lock();

        try {
            FilePath filePath = constructPathUntilUnique(pathTemplate, sourceFile, storageSolution);
            return reserve(filePath, storageSolution);
        } finally {
            reservationLock.unlock();
        }
    }

    private FilePath constructPathUntilUnique(PathTemplate pathTemplate, SourceFile sourceFile,
                                              StorageSolution storageSolution) {
        FilePath filePath;
        int attemptNumber = -1;
        do {
            attemptNumber++;
            if (attemptNumber >= MAX_ATTEMPTS) {
                throw new CouldNotResolveUniqueFilePathException(sourceFile.getOriginalGameTitle(),
                        sourceFile.getOriginalFileName(), attemptNumber);
            }

            filePath = pathTemplate.constructPath(sourceFile, storageSolution.getSeparator(), attemptNumber);
        } while (filePathIsReserved(storageSolution, filePath) || storageSolution.fileExists(filePath));

        return filePath;
    }

    private boolean filePathIsReserved(StorageSolution storageSolution, FilePath filePath) {
        return filePathReservations.contains(new IdentifiableFilePathReservation(filePath, storageSolution.getId()));
    }

    private FilePathReservation reserve(FilePath filePath, StorageSolution storageSolution) {
        var reservation = new IdentifiableFilePathReservation(filePath, storageSolution.getId());
        filePathReservations.add(reservation);

        return reservation;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private final class IdentifiableFilePathReservation implements FilePathReservation {

        private final FilePath filePath;
        private final StorageSolutionId storageSolutionId;

        @Override
        public FilePath get() {
            return filePath;
        }

        @Override
        public void close() {
            filePathReservations.remove(this);
        }
    }
}
