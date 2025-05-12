package dev.codesoapbox.backity.gameproviders.gog.application.usecases;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGogLibrarySizeUseCaseTest {

    private GetGogLibrarySizeUseCase useCase;

    @Mock
    private GogLibraryService gogLibraryService;

    @BeforeEach
    void setUp() {
        useCase = new GetGogLibrarySizeUseCase(gogLibraryService);
    }

    @Test
    void shouldGetLibrarySize() {
        String size = mockLibrarySize();

        String result = useCase.getLibrarySize();

        assertThat(result)
                .isEqualTo(size);
    }

    private String mockLibrarySize() {
        String size = "100 KB";
        when(gogLibraryService.getLibrarySize())
                .thenReturn(size);

        return size;
    }
}