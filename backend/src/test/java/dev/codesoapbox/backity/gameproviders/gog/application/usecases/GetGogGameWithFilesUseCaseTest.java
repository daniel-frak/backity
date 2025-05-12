package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGogGameWithFilesUseCaseTest {

    private GetGogGameDetailsUseCase useCase;

    @Mock
    private GogLibraryService gogLibraryService;

    @BeforeEach
    void setUp() {
        useCase = new GetGogGameDetailsUseCase(gogLibraryService);
    }

    @Test
    void shouldGetGameDetails() {
        String id = "someGameId";
        GogGameWithFiles gogGameWithFiles = mockGameDetailsExist(id);

        GogGameWithFiles result = useCase.getGameDetails(id);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(gogGameWithFiles);
    }

    private GogGameWithFiles mockGameDetailsExist(String id) {
        GogGameWithFiles gogGameWithFiles = aGameDetailsResponse();
        when(gogLibraryService.getGameDetails(id))
                .thenReturn(gogGameWithFiles);
        return gogGameWithFiles;
    }

    private GogGameWithFiles aGameDetailsResponse() {
        return new GogGameWithFiles(
                "Test game",
                null,
                null,
                null,
                null,
                null
        );
    }
}