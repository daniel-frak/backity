package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetEnqueuedFileListUseCase {

    private final FileDetailsRepository fileDetailsRepository;

    public Page<FileDetails> getEnqueuedFileList(Pagination pagination) {
        return fileDetailsRepository.findAllWaitingForDownload(pagination);
    }
}
