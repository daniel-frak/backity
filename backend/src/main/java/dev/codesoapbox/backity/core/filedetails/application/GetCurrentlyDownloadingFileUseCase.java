package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class GetCurrentlyDownloadingFileUseCase {

    private final FileDetailsRepository fileDetailsRepository;

    public Optional<FileDetails> findCurrentlyDownloadingFile() {
        return fileDetailsRepository.findCurrentlyDownloading();
    }
}
