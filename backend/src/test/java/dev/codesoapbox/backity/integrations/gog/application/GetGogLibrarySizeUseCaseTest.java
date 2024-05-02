package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
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
    private GogEmbedClient gogEmbedClient;

    @BeforeEach
    void setUp() {
        useCase = new GetGogLibrarySizeUseCase(gogEmbedClient);
    }

    @Test
    void shouldGetLibrarySize() {
        String size = "100 KB";
        when(gogEmbedClient.getLibrarySize())
                .thenReturn(size);

        String result = useCase.getLibrarySize();

        assertThat(result)
                .isEqualTo(size);
    }
}