package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDiscoveredFileCopiesUseCaseTest {

    private GetDiscoveredFileCopiesUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @BeforeEach
    void setUp() {
        useCase = new GetDiscoveredFileCopiesUseCase(fileCopyRepository);
    }

    @Test
    void shouldGetDiscoveredFileCopies() {
        var pagination = new Pagination(0, 10);
        Page<FileCopy> fileCopyPage = mockFileCopyPageExists(pagination);

        Page<FileCopy> result = useCase.getDiscoveredFileCopies(pagination);

        assertThat(result).isEqualTo(fileCopyPage);
    }

    private Page<FileCopy> mockFileCopyPageExists(Pagination pagination) {
        Page<FileCopy> fileCopy = new Page<>(List.of(TestFileCopy.inProgressWithoutFilePath()),
                1, 1, 1, 10, 0);
        when(fileCopyRepository.findAllDiscovered(pagination))
                .thenReturn(fileCopy);

        return fileCopy;
    }
}