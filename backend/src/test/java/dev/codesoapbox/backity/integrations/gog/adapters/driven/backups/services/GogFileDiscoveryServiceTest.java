package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.files.domain.backup.model.SourceFileDetails;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameFileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogFileDiscoveryServiceTest {

    @InjectMocks
    private GogFileDiscoveryService gogFileDiscoveryService;

    @Mock
    private GogEmbedClient gogEmbedClient;

    @Test
    void shouldDiscoverNewFiles() {
        mockFileDiscovery();

        List<SourceFileDetails> gameFileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(gameFileVersionBackups::add);

        var expectedGameFileDetailss = List.of(
                new SourceFileDetails("GOG", "Game 2", "fileSimpleName1", "1.0.0",
                        "someUrl1", "fileName1", "100 KB"),
                new SourceFileDetails("GOG", "Game 2", "fileSimpleName2", "2.0.0",
                        "someUrl2", "fileName2", "200 KB"),
                new SourceFileDetails("GOG", "Game 4", "fileSimpleName3", "3.0.0",
                        "someUrl3", "fileName3", "300 KB")
        );

        assertEquals(expectedGameFileDetailss, gameFileVersionBackups);
    }

    private void mockFileDiscovery() {
        var gameId1 = "gameId1";
        var gameId2 = "gameId2";
        var gameId3 = "gameId3";
        var gameId4 = "gameId4";

        var game1Details = new GameDetailsResponse("Game 1", null, null,
                null, null, null);

        var game2Details = new GameDetailsResponse("Game 2", null, null,
                null, List.of(
                new GameFileDetailsResponse("1.0.0", "someUrl1", "fileSimpleName1", "100 KB",
                        "fileName1"),
                new GameFileDetailsResponse("2.0.0", "someUrl2", "fileSimpleName2", "200 KB",
                        "fileName2")
        ), null);

        var game4Details = new GameDetailsResponse("Game 4", null, null,
                null, List.of(
                new GameFileDetailsResponse("3.0.0", "someUrl3", "fileSimpleName3", "300 KB",
                        "fileName3")
        ), null);

        when(gogEmbedClient.getLibraryGameIds())
                .thenReturn(List.of(gameId1, gameId2, gameId3, gameId4));
        when(gogEmbedClient.getGameDetails(gameId1))
                .thenReturn(game1Details);
        when(gogEmbedClient.getGameDetails(gameId2))
                .thenReturn(game2Details);
        when(gogEmbedClient.getGameDetails(gameId3))
                .thenReturn(null);
        when(gogEmbedClient.getGameDetails(gameId4))
                .thenReturn(game4Details);
    }

    @Test
    void shouldStopFileDiscoveryBeforeProcessingFiles() {
        var gameId1 = "gameId1";

        when(gogEmbedClient.getLibraryGameIds())
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return List.of(gameId1, "gameId2", "gameId3", "gameId4");
                });

        List<SourceFileDetails> gameFileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(gameFileVersionBackups::add);

        assertEquals(0, gameFileVersionBackups.size());
        verify(gogEmbedClient, never()).getGameDetails(any());
    }

    @Test
    void shouldStopFileDiscoveryWhileProcessingFiles() {
        var gameId1 = "gameId1";
        var game1Details = new GameDetailsResponse("Game 1", null, null,
                null, List.of(
                new GameFileDetailsResponse("3.0.0", "someUrl3", "fileName3", "300 KB",
                        "setup.exe")
        ), null);

        when(gogEmbedClient.getLibraryGameIds())
                .thenReturn(List.of(gameId1, "gameId2", "gameId3", "gameId4"));
        when(gogEmbedClient.getGameDetails(gameId1))
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return game1Details;
                });

        List<SourceFileDetails> gameFileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(gameFileVersionBackups::add);

        assertEquals(1, gameFileVersionBackups.size());
        verify(gogEmbedClient, times(1)).getGameDetails(any());
    }

    @Test
    void shouldSubscribeToProgress() {
        mockFileDiscovery();

        List<ProgressInfo> progressInfos = new ArrayList<>();
        gogFileDiscoveryService.subscribeToProgress(progressInfos::add);
        gogFileDiscoveryService.startFileDiscovery(df -> {
        });

        assertEquals(25, progressInfos.get(0).percentage());
        assertEquals(50, progressInfos.get(1).percentage());
        assertEquals(75, progressInfos.get(2).percentage());
        assertEquals(100, progressInfos.get(3).percentage());
    }

    @Test
    void shouldGetProgressWhenNoProgress() {
        assertEquals(ProgressInfo.none(), gogFileDiscoveryService.getProgress());
    }

    @Test
    void shouldGetProgress() {
        mockFileDiscovery();
        gogFileDiscoveryService.startFileDiscovery(df -> {
        });

        ProgressInfo progressInfo = gogFileDiscoveryService.getProgress();

        assertEquals(100, progressInfo.percentage());
    }

    @Test
    void shouldGetSource() {
        assertEquals("GOG", gogFileDiscoveryService.getSource());
    }
}