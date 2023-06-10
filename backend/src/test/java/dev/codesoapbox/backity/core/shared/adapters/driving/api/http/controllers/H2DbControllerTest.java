package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.config.FileBackupBeanConfig;
import dev.codesoapbox.backity.core.files.config.FileManagementBeanConfig;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = H2DbController.class, properties = "h2dump.path=test_dump.sql")
@Import({FileManagementBeanConfig.class, FileBackupBeanConfig.class})
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
class H2DbControllerTest {

    private static final Path TEST_DUMP_PATH = Path.of(
            System.getProperty("user.dir") + File.separator + "test_dump.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameFileDetailsJpaRepository gameFileVersionRepository;

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
        var gameFileVersionBackup = new GameFileDetails(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.DISCOVERED, null);
        gameFileVersionRepository.save(gameFileVersionBackup);

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