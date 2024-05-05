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

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.inProgressFileDetails;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDiscoveredFileListUseCaseTest {

    private GetDiscoveredFileListUseCase useCase;

    @Mock
    private FileDetailsRepository fileDetailsRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetDiscoveredFileListUseCase(fileDetailsRepository);
    }

    @Test
    void shouldGetDiscoveredFileList() {
        Pagination pagination = new Pagination(0, 10);
        Page<FileDetails> fileDetails = new Page<>(singletonList(inProgressFileDetails().build()),
                1, 1, 1, 10, 0);
        when(fileDetailsRepository.findAllDiscovered(pagination))
                .thenReturn(fileDetails);

        Page<FileDetails> result = useCase.getDiscoveredFileList(pagination);

        assertThat(result).isEqualTo(fileDetails);
    }
}