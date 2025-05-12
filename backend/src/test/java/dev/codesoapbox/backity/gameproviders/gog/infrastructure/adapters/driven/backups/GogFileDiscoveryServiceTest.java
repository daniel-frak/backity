package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.backups;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.ProgressInfo;
import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameProviderFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameProviderFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameWithFiles;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogGameFile;
import dev.codesoapbox.backity.gameproviders.gog.domain.GogLibraryService;
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
    private GogLibraryService gogLibraryService;

    @Test
    void startFileDiscoveryShouldDiscoverNewFiles() {
        mockFileDiscovery();

        List<GameProviderFile> fileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(fileVersionBackups::add);

        List<GameProviderFile> expectedGameFile = List.of(
                TestGameProviderFile.gogBuilder()
                        .originalGameTitle("Game 2")
                        .fileTitle("fileSimpleName1")
                        .version("1.0.0")
                        .url("http://some.url1")
                        .originalFileName("fileName1")
                        .size(new FileSize(102_400L))
                        .build(),
                TestGameProviderFile.gogBuilder()
                        .originalGameTitle("Game 2")
                        .fileTitle("fileSimpleName2")
                        .version("2.0.0")
                        .url("http://some.url2")
                        .originalFileName("fileName2")
                        .size(new FileSize(204_800L))
                        .build(),
                TestGameProviderFile.gogBuilder()
                        .originalGameTitle("Game 4")
                        .fileTitle("fileSimpleName3")
                        .version("3.0.0")
                        .url("http://some.url3")
                        .originalFileName("fileName3")
                        .size(new FileSize(307_200L))
                        .build()
        );

        assertThat(fileVersionBackups).isEqualTo(expectedGameFile);
    }

    private void mockFileDiscovery() {
        var gameId1 = "gameId1";
        var gameId2 = "gameId2";
        var gameId3 = "gameId3";
        var gameId4 = "gameId4";

        var game1Details = new GogGameWithFiles("Game 1", null, null,
                null, null, null);

        var game2Details = new GogGameWithFiles("Game 2", null, null,
                null, List.of(
                new GogGameFile("1.0.0", "http://some.url1", "fileSimpleName1", "100 KB",
                        "fileName1"),
                new GogGameFile("2.0.0", "http://some.url2", "fileSimpleName2", "200 KB",
                        "fileName2")
        ), null);

        var game4Details = new GogGameWithFiles("Game 4", null, null,
                null, List.of(
                new GogGameFile("3.0.0", "http://some.url3", "fileSimpleName3", "300 KB",
                        "fileName3")
        ), null);

        when(gogLibraryService.getLibraryGameIds())
                .thenReturn(List.of(gameId1, gameId2, gameId3, gameId4));
        when(gogLibraryService.getGameDetails(gameId1))
                .thenReturn(game1Details);
        when(gogLibraryService.getGameDetails(gameId2))
                .thenReturn(game2Details);
        when(gogLibraryService.getGameDetails(gameId3))
                .thenReturn(null);
        when(gogLibraryService.getGameDetails(gameId4))
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

        when(gogLibraryService.getLibraryGameIds())
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return List.of(gameId1, "gameId2", "gameId3", "gameId4");
                });

        List<GameProviderFile> fileVersionBackups = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(fileVersionBackups::add);

        assertThat(fileVersionBackups).isEmpty();
        verify(gogLibraryService, never()).getGameDetails(any());
    }

    @Test
    void shouldStopFileDiscoveryWhileProcessingFiles() {
        var gameId1 = "gameId1";
        var game1Details = new GogGameWithFiles("Game 1", null, null,
                null, List.of(
                new GogGameFile("3.0.0", "http://some.url3", "fileName3", "300 KB",
                        "setup.exe")
        ), null);

        when(gogLibraryService.getLibraryGameIds())
                .thenReturn(List.of(gameId1, "gameId2", "gameId3", "gameId4"));
        when(gogLibraryService.getGameDetails(gameId1))
                .thenAnswer(inv -> {
                    gogFileDiscoveryService.stopFileDiscovery();
                    return game1Details;
                });

        List<GameProviderFile> gameProviderFileDetails = new ArrayList<>();
        gogFileDiscoveryService.startFileDiscovery(gameProviderFileDetails::add);

        assertThat(gameProviderFileDetails.size()).isOne();
        verify(gogLibraryService, times(1)).getGameDetails(any());
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
        assertThat(gogFileDiscoveryService.getGameProviderId()).isEqualTo("GOG");
    }
}