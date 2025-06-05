package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.application.FileCopyWithContextFactory;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.domain.TestPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFileCopyQueueUseCaseTest {

    private GetFileCopyQueueUseCase useCase;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileCopyWithContextFactory fileCopyWithContextFactory;

    @BeforeEach
    void setUp() {
        useCase = new GetFileCopyQueueUseCase(fileCopyRepository, fileCopyWithContextFactory);
    }

    @Test
    void shouldGetFileCopyQueue() {
        var pagination = new Pagination(0, 10);
        Page<FileCopyWithContext> fileCopyWithContextPage = mockFileCopyPageExists(pagination);

        Page<FileCopyWithContext> result = useCase.getFileCopyQueue(pagination);

        assertThat(result).isEqualTo(fileCopyWithContextPage);
    }

    private Page<FileCopyWithContext> mockFileCopyPageExists(Pagination pagination) {
        Page<FileCopy> fileCopyPage = TestPage.of(List.of(TestFileCopy.enqueued()), pagination);
        when(fileCopyRepository.findAllInProgressOrEnqueued(pagination))
                .thenReturn(fileCopyPage);

        return mockFileCopyWithContextCreation(pagination, fileCopyPage);
    }

    private Page<FileCopyWithContext> mockFileCopyWithContextCreation(
            Pagination pagination, Page<FileCopy> fileCopyPage) {
        Page<FileCopyWithContext> fileCopyWithContextPagePage = TestPage.of(List.of(
                new FileCopyWithContext(
                        TestFileCopy.enqueued(),
                        TestGameFile.gog(),
                        TestGame.any(),
                        TestBackupTarget.localFolder(),
                        null
                )
        ), pagination);
        when(fileCopyWithContextFactory.createPageFrom(fileCopyPage))
                .thenReturn(fileCopyWithContextPagePage);

        return fileCopyWithContextPagePage;
    }

    @Test
    void shouldNotUseFileCopyWithContextFactoryGivenNoFileCopiesInQueue() {
        var pagination = new Pagination(0, 10);
        when(fileCopyRepository.findAllInProgressOrEnqueued(pagination))
                .thenReturn(TestPage.of(emptyList(), pagination));

        Page<FileCopyWithContext> result = useCase.getFileCopyQueue(pagination);

        assertThat(result).isNotNull();
        verifyNoInteractions(fileCopyWithContextFactory);
    }
}