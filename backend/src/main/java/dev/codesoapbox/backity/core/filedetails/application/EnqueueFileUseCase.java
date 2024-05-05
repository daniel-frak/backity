package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class EnqueueFileUseCase {

    private final FileDetailsRepository fileDetailsRepository;

    public void enqueue(FileDetailsId fileDetailsId) {
        FileDetails fileDetails = fileDetailsRepository
                .getById(fileDetailsId);

        fileDetails.enqueue();
        fileDetailsRepository.save(fileDetails);
    }
}
