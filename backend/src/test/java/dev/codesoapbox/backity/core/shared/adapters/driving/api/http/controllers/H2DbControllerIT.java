package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.backup.config.FileBackupBeanConfig;
import dev.codesoapbox.backity.core.filemanagement.config.LocalFileSystemBeanConfig;
import dev.codesoapbox.backity.core.filemanagement.config.SharedFileManagementBeanConfig;
import dev.codesoapbox.backity.core.game.config.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.config.GameFileJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.config.DomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.core.shared.config.jpa.SharedJpaRepositoryBeanConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = H2DbController.class, properties = "h2dump.path=test_dump.sql")
@Import({SharedFileManagementBeanConfig.class, LocalFileSystemBeanConfig.class, FileBackupBeanConfig.class,
        GameJpaRepositoryBeanConfig.class, GameFileJpaRepositoryBeanConfig.class,
        SharedJpaRepositoryBeanConfig.class, DomainEventPublisherBeanConfig.class})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@EnableJpaAuditing
@MockBeans({
        @MockBean(SimpMessagingTemplate.class)
})
class H2DbControllerIT {

    private static final Path TEST_DUMP_PATH = Path.of(
            System.getProperty("user.dir") + File.separator + "test_dump.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameFileRepository gameFileRepository;

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
        GameFile gameFile = discoveredGameFile().build();
        Game game = new Game(gameFile.getGameId(), "Test game");
        gameRepository.save(game);
        gameFileRepository.save(gameFile);

        mockMvc.perform(get("/api/h2/dump"))
                .andDo(print())
                .andExpect(status().isOk());

        var dumpContents = readTestDump();

        assertThat(dumpContents).contains("INSERT INTO \"PUBLIC\".\"GAME_FILE\" VALUES");
    }

    private String readTestDump() throws IOException {
        return Files.readString(TEST_DUMP_PATH);
    }
}