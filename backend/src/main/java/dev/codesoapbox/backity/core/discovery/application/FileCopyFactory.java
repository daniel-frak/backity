package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.FileCopyId;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class FileCopyFactory {

    private final Supplier<FileCopyId> idSupplier;

    public FileCopy create() {
        return new FileCopy(idSupplier.get(), FileBackupStatus.DISCOVERED, null, null);
    }
}
