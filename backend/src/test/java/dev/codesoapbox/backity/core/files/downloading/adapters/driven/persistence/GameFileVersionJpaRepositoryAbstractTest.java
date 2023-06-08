package dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

abstract class GameFileVersionJpaRepositoryAbstractTest {

    private GameFileVersionJpaRepository gameFileVersionJpaRepository;

    @Autowired
    private GameFileVersionSpringRepository gameFileVersionSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        gameFileVersionJpaRepository = new GameFileVersionJpaRepository(gameFileVersionSpringRepository);
        entityManager = entityManagerFactory.createEntityManager();
        cleanDatabase();
        persistTestEnqueuedFiles();
    }

    private void cleanDatabase() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM GAME_FILE_VERSION;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestEnqueuedFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("ALTER SEQUENCE seq_game_file_version RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("""                         
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource1', 'someUrl1', 'someName1', 'someGameTitle1', 
                        'someVersion1', 'someSize1', '2022-04-29T14:15:53', 'ENQUEUED_FOR_DOWNLOAD', 'someFailedReason1');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource2', 'someUrl2', 'someName2', 'someGameTitle2', 
                        'someVersion2', 'someSize2', '2022-05-29T14:15:53', 'DOWNLOADED', 'someFailedReason2');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource3', 'someUrl3', 'someName3', 'someGameTitle3',
                         'someVersion3', 'someSize3', '2022-06-29T14:15:53', 'ENQUEUED_FOR_DOWNLOAD', 'someFailedReason3');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource4', 'someUrl4', 'someName4', 'someGameTitle4',
                         'someVersion4', 'someSize4', '2022-07-29T14:15:53', 'DOWNLOAD_FAILED', 'someFailedReason4');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource5', 'someUrl5', 'someName5', 'someGameTitle5', 
                        'someVersion5', 'someSize5', '2022-08-29T14:15:53', 'DOWNLOAD_IN_PROGRESS', 'someFailedReason5');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource6', 'someUrl6', 'someName6', 'someGameTitle6', 
                        'someVersion6', 'someSize6', '2022-08-29T14:16:43', 'DISCOVERED', 'someFailedReason6');
                """).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void shouldFindOldestWaitingForDownload() {
        Optional<GameFileVersion> result = gameFileVersionJpaRepository.findOldestWaitingForDownload();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        var expectedResult = GameFileVersion.builder()
                .id(result.get().getId())
                .source("someSource1")
                .url("someUrl1")
                .title("someName1")
                .gameTitle("someGameTitle1")
                .version("someVersion1")
                .size("someSize1")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                .failedReason("someFailedReason1")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersion.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource1")
                        .url("someUrl1")
                        .title("someName1")
                        .gameTitle("someGameTitle1")
                        .version("someVersion1")
                        .size("someSize1")
                        .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                        .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                        .failedReason("someFailedReason1")
                        .build(),
                GameFileVersion.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource3")
                        .url("someUrl3")
                        .title("someName3")
                        .gameTitle("someGameTitle3")
                        .version("someVersion3")
                        .size("someSize3")
                        .dateCreated(LocalDateTime.parse("2022-06-29T14:15:53"))
                        .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                        .failedReason("someFailedReason3")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldSave() {
        var newEnqueuedFileDownload =
                GameFileVersion.builder()
                        .source("someSourceNew")
                        .url("someUrlNew")
                        .title("someNameNew")
                        .gameTitle("someGameTitleNew")
                        .version("someVersionNew")
                        .size("someSizeNew")
                        .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                        .failedReason("someFailedReasonNew")
                        .build();

        var result = gameFileVersionJpaRepository.save(newEnqueuedFileDownload);

        assertNotNull(result.getId());
        assertNotNull(result.getDateCreated());
        BigInteger recordCount = (BigInteger) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM GAME_FILE_VERSION f" +
                                " WHERE f.id = '" + result.getId() + "';")
                .getSingleResult();
        assertEquals(BigInteger.ONE, recordCount);
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileVersion> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        var expectedResult = GameFileVersion.builder()
                .id(result.get().getId())
                .source("someSource5")
                .url("someUrl5")
                .title("someName5")
                .gameTitle("someGameTitle5")
                .version("someVersion5")
                .size("someSize5")
                .dateCreated(LocalDateTime.parse("2022-08-29T14:15:53"))
                .status(FileStatus.DOWNLOAD_IN_PROGRESS)
                .failedReason("someFailedReason5")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersion.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource2")
                        .url("someUrl2")
                        .title("someName2")
                        .gameTitle("someGameTitle2")
                        .version("someVersion2")
                        .size("someSize2")
                        .dateCreated(LocalDateTime.parse("2022-05-29T14:15:53"))
                        .status(FileStatus.DOWNLOADED)
                        .failedReason("someFailedReason2")
                        .build(),
                GameFileVersion.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource4")
                        .url("someUrl4")
                        .title("someName4")
                        .gameTitle("someGameTitle4")
                        .version("someVersion4")
                        .size("someSize4")
                        .dateCreated(LocalDateTime.parse("2022-07-29T14:15:53"))
                        .status(FileStatus.DOWNLOAD_FAILED)
                        .failedReason("someFailedReason4")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldFindIfExistsByUrlAndVersion() {
        boolean existsActual = gameFileVersionJpaRepository.existsByUrlAndVersion(
                "someUrl2", "someVersion2");
        boolean existsMissing = gameFileVersionJpaRepository.existsByUrlAndVersion(
                "someUrl2", "fakeVersion");
        assertTrue(existsActual);
        assertFalse(existsMissing);
    }

    @Test
    void shouldFindById() {
        Optional<GameFileVersion> result = gameFileVersionJpaRepository.findById(1L);

        assertTrue(result.isPresent());
        
        var expectedResult = GameFileVersion.builder()
                .id(result.get().getId())
                .source("someSource1")
                .url("someUrl1")
                .title("someName1")
                .gameTitle("someGameTitle1")
                .version("someVersion1")
                .size("someSize1")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(FileStatus.ENQUEUED_FOR_DOWNLOAD)
                .failedReason("someFailedReason1")
                .build();
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersion.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource6")
                        .url("someUrl6")
                        .title("someName6")
                        .gameTitle("someGameTitle6")
                        .version("someVersion6")
                        .size("someSize6")
                        .dateCreated(LocalDateTime.parse("2022-08-29T14:16:43"))
                        .status(FileStatus.DISCOVERED)
                        .failedReason("someFailedReason6")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }
}