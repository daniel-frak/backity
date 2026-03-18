package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.embed.webclient.operations;

import dev.codesoapbox.backity.core.sourcefile.domain.FileSize;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class GetLibrarySizeGogEmbedOperation {

    private final GetLibraryGameIdsGogEmbedOperation libraryGameIdsOperation;
    private final GetGameDetailsGogEmbedOperation gameDetailsOperation;

    public String execute() {
        log.info("Calculating library size...");
        List<String> libraryGameIds = libraryGameIdsOperation.execute();
        FileSize accumulatedFileSize = sumFileSizes(libraryGameIds);
        String librarySize = accumulatedFileSize.toString();

        log.info("Library size: {}", librarySize);

        return librarySize;
    }

    private FileSize sumFileSizes(List<String> libraryGameIds) {
        return libraryGameIds.stream()
                .map(gameDetailsOperation::execute)
                .filter(Optional::isPresent)
                .flatMap(Optional::stream)
                .flatMap(details -> details.files().stream())
                .map(GogFile::size)
                .map(FileSize::fromString)
                .reduce(new FileSize(0L), FileSize::add);
    }
}
