package dev.codesoapbox.backity.integrations.gog.application;

import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetGogGameDetailsUseCaseTest {

    private GetGogGameDetailsUseCase useCase;

    @Mock
    private GogEmbedClient gogEmbedClient;

    @BeforeEach
    void setUp() {
        useCase = new GetGogGameDetailsUseCase(gogEmbedClient);
    }

    @Test
    void shouldGetGameDetails() {
        String id = "someGameId";
        GameDetailsResponse gameDetailsResponse = aGameDetailsResponse();
        when(gogEmbedClient.getGameDetails(id))
                .thenReturn(gameDetailsResponse);

        GameDetailsResponse result = useCase.getGameDetails(id);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(gameDetailsResponse);
    }

    private GameDetailsResponse aGameDetailsResponse() {
        GameDetailsResponse gameDetailsResponse = new GameDetailsResponse();
        gameDetailsResponse.setTitle("Test game");
        return gameDetailsResponse;
    }
}