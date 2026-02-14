package dev.codesoapbox.backity.core.storagesolution.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.exceptions.CouldNotResolveUniqueFilePathException;

public class UniqueFilePathResolver {

    private static final int MAX_ATTEMPTS = 1000;

    public String resolve(PathTemplate pathTemplate, SourceFile sourceFile, StorageSolution storageSolution) {
        return constructPathUntilUnique(pathTemplate, sourceFile, storageSolution);
    }

    private String constructPathUntilUnique(PathTemplate pathTemplate, SourceFile sourceFile,
                                            StorageSolution storageSolution) {
        String filePath;
        int attemptNumber = -1;
        do {
            attemptNumber++;
            if (attemptNumber >= MAX_ATTEMPTS) {
                throw new CouldNotResolveUniqueFilePathException(sourceFile.getOriginalGameTitle(),
                        sourceFile.getOriginalFileName(), attemptNumber);
            }

            filePath = pathTemplate.constructPath(sourceFile, storageSolution.getSeparator(), attemptNumber);
        } while (storageSolution.fileExists(filePath));

        return filePath;
    }
}
