package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.domain.TestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProcessedFileCopiesUseCaseTest {

    private GetProcessedFileCopiesUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetProcessedFileCopiesUseCase(fileCopyRepository);
    }

    @Test
    void shouldGetProcessedFileCopies() {
        var pagination = new Pagination(0, 10);
        Page<FileCopy> fileCopyPage = mockFileCopyPageExists(pagination);

        Page<FileCopy> result = useCase.getProcessedFileCopies(pagination);

        assertThat(result).isEqualTo(fileCopyPage);
    }

    private Page<FileCopy> mockFileCopyPageExists(Pagination pagination) {
        Page<FileCopy> fileCopyPage = TestPage.of(List.of(TestFileCopy.storedIntegrityUnknown()), pagination);
        when(fileCopyRepository.findAllProcessed(pagination))
                .thenReturn(fileCopyPage);

        return fileCopyPage;
    }
}