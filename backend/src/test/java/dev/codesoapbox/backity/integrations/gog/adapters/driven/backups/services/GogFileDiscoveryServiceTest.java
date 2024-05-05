package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.discovery.domain.ProgressInfo;
import dev.codesoapbox.backity.core.filedetails.domain.SourceFileDetails;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.FileDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.model.embed.GameDetailsResponse;
import dev.codesoapbox.backity.integrations.gog.domain.services.GogEmbedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GogFileDiscoveryServiceTest {

    @InjectMocks
    private GogFileDiscoveryService gogFileDiscoveryService;

    @Mock
    private GogEmbedClient gogEmbedClient;

    @Test
    void startFileDiscoveryShouldDiscoverNewFiles() {
        mockFileDiscovery();

        List<SourceFileDetails> fileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(fileVersionBackups::add);

        var expectedFileDetails = List.of(
                new SourceFileDetails(new FileSourceId("GOG"), "Game 2", "fileSimpleName1",
                        "1.0.0", "someUrl1", "fileName1", "100 KB"),
                new SourceFileDetails(new FileSourceId("GOG"), "Game 2", "fileSimpleName2",
                        "2.0.0",
                        "someUrl2", "fileName2", "200 KB"),
                new SourceFileDetails(new FileSourceId("GOG"), "Game 4", "fileSimpleName3",
                        "3.0.0", "someUrl3", "fileName3", "300 KB")
        );

        assertThat(fileVersionBackups).isEqualTo(expectedFileDetails);
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
                new FileDetailsResponse("1.0.0", "someUrl1", "fileSimpleName1", "100 KB",
                        "fileName1"),
                new FileDetailsResponse("2.0.0", "someUrl2", "fileSimpleName2", "200 KB",
                        "fileName2")
        ), null);

        var game4Details = new GameDetailsResponse("Game 4", null, null,
                null, List.of(
                new FileDetailsResponse("3.0.0", "someUrl3", "fileSimpleName3", "300 KB",
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
    void startFileDiscoveryShouldRestartFileDiscoveryAfterStopping() {
        mockFileDiscovery();
        AtomicInteger discoveredFilesCount = new AtomicInteger();

        gogFileDiscoveryService.stopFileDiscovery();
        gogFileDiscoveryService.startFileDiscovery(gf -> discoveredFilesCount.getAndIncrement());

        assertThat(discoveredFilesCount.get()).isEqualTo(3);
    }

    @Test
    void shouldStopFileDiscoveryBeforeProcessingFiles() {
        var gameId1 = "gameId1";

        when(gogEmbedClient.getLibraryGameIds())
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return List.of(gameId1, "gameId2", "gameId3", "gameId4");
                });

        List<SourceFileDetails> fileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(fileVersionBackups::add);

        assertThat(fileVersionBackups).isEmpty();
        verify(gogEmbedClient, never()).getGameDetails(any());
    }

    @Test
    void shouldStopFileDiscoveryWhileProcessingFiles() {
        var gameId1 = "gameId1";
        var game1Details = new GameDetailsResponse("Game 1", null, null,
                null, List.of(
                new FileDetailsResponse("3.0.0", "someUrl3", "fileName3", "300 KB",
                        "setup.exe")
        ), null);

        when(gogEmbedClient.getLibraryGameIds())
                .thenReturn(List.of(gameId1, "gameId2", "gameId3", "gameId4"));
        when(gogEmbedClient.getGameDetails(gameId1))
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return game1Details;
                });

        List<SourceFileDetails> sourceFileDetails = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(sourceFileDetails::add);

        assertThat(sourceFileDetails.size()).isOne();
        verify(gogEmbedClient, times(1)).getGameDetails(any());
    }

    @Test
    void shouldSubscribeToProgress() {
        mockFileDiscovery();

        List<ProgressInfo> progressInfos = new ArrayList<>();
        gogFileDiscoveryService.subscribeToProgress(progressInfos::add);
        gogFileDiscoveryService.startFileDiscovery(df -> {
        });

        assertThat(progressInfos.get(0).percentage()).isEqualTo(25);
        assertThat(progressInfos.get(1).percentage()).isEqualTo(50);
        assertThat(progressInfos.get(2).percentage()).isEqualTo(75);
        assertThat(progressInfos.get(3).percentage()).isEqualTo(100);
    }

    @Test
    void shouldGetProgressWhenNoProgress() {
        assertThat(gogFileDiscoveryService.getProgress()).isEqualTo(ProgressInfo.none());
    }

    @Test
    void shouldGetProgress() {
        mockFileDiscovery();
        gogFileDiscoveryService.startFileDiscovery(df -> {
        });

        ProgressInfo progressInfo = gogFileDiscoveryService.getProgress();

        assertThat(progressInfo.percentage()).isEqualTo(100);
    }

    @Test
    void shouldGetSource() {
        assertThat(gogFileDiscoveryService.getSource()).isEqualTo("GOG");
    }
}