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

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.successfulFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProcessedFileListUseCaseTest {

    private GetProcessedFileListUseCase useCase;

    @Mock
    private FileDetailsRepository fileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetProcessedFileListUseCase(fileDetailsRepository);
    }

    @Test
    void shouldGetProcessedFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<FileDetails> fileDetailsPage = new Page<>(singletonList(successfulFileDetails().build()),
                1, 1, 1, 10, 0);
        when(fileDetailsRepository.findAllProcessed(pagination))
                .thenReturn(fileDetailsPage);

        Page<FileDetails> result = useCase.getProcessedFileList(pagination);

        assertThat(result).isEqualTo(fileDetailsPage);
    }
}