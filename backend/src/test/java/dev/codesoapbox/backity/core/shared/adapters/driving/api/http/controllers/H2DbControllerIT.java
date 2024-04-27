package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.config.FileBackupBeanConfig;
import dev.codesoapbox.backity.core.filemanagement.config.LocalFileSystemBeanConfig;
import dev.codesoapbox.backity.core.filemanagement.config.SharedFileManagementBeanConfig;
import dev.codesoapbox.backity.core.game.config.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefiledetails.config.GameFileDetailsJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedJpaRepositoryBeanConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = H2DbController.class, properties = "h2dump.path=test_dump.sql")
@Import({SharedFileManagementBeanConfig.class, LocalFileSystemBeanConfig.class, FileBackupBeanConfig.class,
        GameJpaRepositoryBeanConfig.class, GameFileDetailsJpaRepositoryBeanConfig.class,
        SharedJpaRepositoryBeanConfig.class})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
class H2DbControllerIT {

    private static final Path TEST_DUMP_PATH = Path.of(
            System.getProperty("user.dir") + File.separator + "test_dump.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameFileDetailsRepository gameFileDetailsRepository;

    @MockBean
    private SimpMessagingTemplate messageService;

    @BeforeEach
    void setUp() throws IOException {
        deleteTestDump();
    }

    private void deleteTestDump() throws IOException {
        Files.deleteIfExists(TEST_DUMP_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteTestDump();
    }

    @Test
    void shouldDumpSql() throws Exception {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();
        Game game = new Game(gameFileDetails.getGameId(), "Test game");
        gameRepository.save(game);
        gameFileDetailsRepository.save(gameFileDetails);

        mockMvc.perform(get("/api/h2/dump"))
                .andDo(print())
                .andExpect(status().isOk());

        var dumpContents = readTestDump();

        assertTrue(dumpContents.contains("INSERT INTO \"PUBLIC\".\"GAME_FILE_DETAILS\" VALUES"));
    }

    private String readTestDump() throws IOException {
        return Files.readString(TEST_DUMP_PATH);
    }
}