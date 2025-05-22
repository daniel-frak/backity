package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.core.backup.application.FileCopyFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnqueueFileCopyUseCase {

    private final FileCopyRepository fileCopyRepository;
    private final FileCopyFactory fileCopyFactory;

    @DoNotMutate // False positive on replacing lambda with null for fileCopyFactory argument
    public void enqueue(FileCopyNaturalId fileCopyNaturalId) {
        FileCopy fileCopy = fileCopyRepository.findByNaturalIdOrCreate(fileCopyNaturalId,
                        () -> fileCopyFactory.create(fileCopyNaturalId));

        fileCopy.toEnqueued();
        fileCopyRepository.save(fileCopy);
    }
}
