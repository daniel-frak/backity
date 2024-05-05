package dev.codesoapbox.backity.core.filedetails.application;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.enqueuedFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetEnqueuedFileListUseCaseTest {

    private GetEnqueuedFileListUseCase useCase;

    @Mock
    private FileDetailsRepository fileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetEnqueuedFileListUseCase(fileDetailsRepository);
    }

    @Test
    void shouldGetEnqueuedFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<FileDetails> fileDetailsPage = new Page<>(singletonList(enqueuedFileDetails().build()),
                1, 1, 1, 10, 0);
        when(fileDetailsRepository.findAllWaitingForDownload(pagination))
                .thenReturn(fileDetailsPage);

        Page<FileDetails> result = useCase.getEnqueuedFileList(pagination);

        assertThat(result).isEqualTo(fileDetailsPage);
    }
}