package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions.GameDetailsRequestFailedException;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GogEmbedClientTest {

    @RegisterExtension
    private static final WireMockExtension wireMockEmbed = WireMockExtension.newInstance()
            .build();
    @RegisterExtension
    private static final WireMockExtension wireMockGeneral = WireMockExtension.newInstance()
            .build();
    private GogEmbedClient gogEmbedClient;
    @Mock
    private GogAuthService authService;

    @BeforeEach
    void setUp() {
        WebClient webClientEmbed = WebClient.builder()
                .baseUrl(wireMockEmbed.baseUrl())
                .build();
        WebClient webClientGeneral = WebClient.builder()
                .baseUrl(wireMockGeneral.baseUrl())
                .build();

        gogEmbedClient = new GogEmbedClient(webClientEmbed, webClientGeneral, authService);
    }

    @Test
    void shouldGetGameDetails() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("example_gog_game_details_response.json")));

        GameDetailsResponse result = gogEmbedClient.getGameDetails("someGameId");

        var expectedResult = new GameDetailsResponse("Unreal Tournament 2004 Editor's Choice Edition",
                "//images-4.gog.com/ebed1d5546a4fa382d7d36db8aee7f298eac7db3a8dc2f4389120b5b7b3155a9",
                "someCdKey", "someTextInformation", singletonList(new GameFileDetailsResponse(
                "someVersion", "/downlink/unreal_tournament_2004_ece/en1installer3",
                "Unreal Tournament 2004 Editor's Choice Edition (Part 1 of 3)", "1 MB")),
                "someChangelog");

        assertEquals(expectedResult, result);
    }

    @Test
    void getGameDetailsShouldThrowWhenRequestFails() {
        var accessToken = "someAccessToken";

        when(authService.getAccessToken())
                .thenReturn(accessToken);

        wireMockEmbed.stubFor(get(urlPathEqualTo("/account/gameDetails/someGameId.json"))
                .withHeader(GogEmbedClient.HEADER_AUTHORIZATION, equalTo("Bearer " + accessToken))
                .willReturn(aResponse()
                        .withStatus(500)));

        assertThrows(GameDetailsRequestFailedException.class, () -> gogEmbedClient.getGameDetails("someGameId"));
    }

    // @TODO FINISH ME

    @Test
    void shouldGetLibrarySize() {

    }

    @Test
    void getLibraryGameIds() {
    }

    @Test
    void getFileBuffer() {
    }
}