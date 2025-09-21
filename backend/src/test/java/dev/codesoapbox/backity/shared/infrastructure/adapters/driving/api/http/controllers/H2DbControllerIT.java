package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.storagesolution.infrastructure.config.LocalFileSystemStorageSolutionBeanConfig;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.game.infrastructure.config.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.infrastructure.config.GameFileJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.SpringDomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.jpa.SharedJpaRepositoryBeanConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = H2DbController.class, properties = "backity.h2dump.path=test_dump.sql")
@Import({
        LocalFileSystemStorageSolutionBeanConfig.class,
        GameJpaRepositoryBeanConfig.class,
        GameFileJpaRepositoryBeanConfig.class,
        SharedJpaRepositoryBeanConfig.class,
        SpringDomainEventPublisherBeanConfig.class
})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@EnableJpaAuditing
@MockitoBean(types = SimpMessagingTemplate.class)
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
        Game game = TestGame.any();
        GameFile gameFile = TestGameFile.gogBuilder()
                .gameId(game.getId())
                .build();
        gameRepository.save(game);
        gameFileRepository.save(gameFile);

        mockMvc.perform(get("/api/h2/dump"))
                .andDo(print())
                .andExpect(status().isOk());

        var dumpContents = readTestDump();

        assertThat(dumpContents)
                .contains("INSERT INTO \"PUBLIC\".\"GAME\" VALUES")
                .contains("INSERT INTO \"PUBLIC\".\"GAME_FILE\" VALUES");
    }

    private String readTestDump() throws IOException {
        return Files.readString(TEST_DUMP_PATH);
    }
}