package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.application.GameFacade;
import dev.codesoapbox.backity.core.files.application.GameWithFiles;
import dev.codesoapbox.backity.core.files.config.GameHttpBeanConfig;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@Import(GameHttpBeanConfig.class)
class GameControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameFacade gameFacade;

    @Test
    void shouldGetGames() throws Exception {
        var gameId = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        Pageable pageable = PageRequest.of(1, 2);
        var gameFileStringId = "acde26d7-33c7-42ee-be16-bca91a604b48";
        Page<GameWithFiles> gameWithFilesPage = new PageImpl<>(singletonList(
                new GameWithFiles(
                        new Game(gameId, "Test Game"),
                        singletonList(
                                new GameFileDetails(
                                        new GameFileDetailsId(UUID.fromString(gameFileStringId)),
                                        "someSource",
                                        "someUrl",
                                        "someTitle",
                                        "someOriginalFileName",
                                        "someFilePath",
                                        "someGameTitle",
                                        gameId.value().toString(),
                                        "someVersion",
                                        "100 KB",
                                        LocalDateTime.parse("2022-04-29T14:15:53"),
                                        LocalDateTime.parse("2022-04-29T14:15:53"),
                                        FileBackupStatus.DISCOVERED,
                                        "someFailReason"
                                )
                        )
                )
        ));
        when(gameFacade.getGamesWithFiles(pageable))
                .thenReturn(gameWithFilesPage);

        mockMvc.perform(get("/api/games?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "content": [{
                                "id": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                                "title": "Test Game",
                                "gameFiles": [{
                                    "id": "acde26d7-33c7-42ee-be16-bca91a604b48",
                                    "source": "someSource",
                                    "url": "someUrl",
                                    "title": "someTitle",
                                    "originalFileName": "someOriginalFileName",
                                    "filePath": "someFilePath",
                                    "gameTitle": "someGameTitle",
                                    "gameId": "5bdd248a-c3aa-487a-8479-0bfdb32f7ae5",
                                    "version": "someVersion",
                                    "size": "100 KB",
                                    "dateCreated": "2022-04-29T14:15:53",
                                    "dateModified": "2022-04-29T14:15:53",
                                    "backupStatus": "DISCOVERED",
                                    "backupFailedReason": "someFailReason"
                                }]
                            }]
                        }
                        """));
    }
}