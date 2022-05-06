package dev.codesoapbox.backity.core.shared.adapters.driving.api.h2;

import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileJpaRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import dev.codesoapbox.backity.testing.annotations.H2RepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@H2RepositoryTest
@AutoConfigureMockMvc
class H2DbControllerTest {

    private static final Path TEST_DUMP_PATH = Path.of(System.getProperty("user.dir") + File.separator + "test_dump.sql");
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DiscoveredFileJpaRepository discoveredFileJpaRepository;

    @BeforeEach
    void setUp() throws IOException {
        deleteTestDump();
    }

    private void deleteTestDump() throws IOException {
        Files.delete(TEST_DUMP_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteTestDump();
    }

    @Test
    void shouldDumpSql() throws Exception {
        DiscoveredFile discoveredFile = DiscoveredFile.builder()
                .id(new DiscoveredFileId("someUrl", "someVersion"))
                .build();
        discoveredFileJpaRepository.save(discoveredFile);

        mockMvc.perform(get("/api/h2/dump"))
                .andDo(print())
                .andExpect(status().isOk());

        var dumpContents = readTestDump();

        assertTrue(dumpContents.contains("INSERT INTO \"PUBLIC\".\"DISCOVERED_FILE\" VALUES"));
    }

    private String readTestDump() throws IOException {
        return Files.readString(TEST_DUMP_PATH);
    }
}