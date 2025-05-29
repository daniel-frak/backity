package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class FileCopyFactory {

    private final Supplier<FileCopyId> idSupplier;

    public FileCopy create(FileCopyNaturalId fileCopyNaturalId) {
        return new FileCopy(idSupplier.get(), fileCopyNaturalId, FileCopyStatus.TRACKED,
                null, null, null, null);
    }
}
