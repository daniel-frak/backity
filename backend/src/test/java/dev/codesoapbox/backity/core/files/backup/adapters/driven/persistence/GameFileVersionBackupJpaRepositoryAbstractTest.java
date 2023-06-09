package dev.codesoapbox.backity.core.files.backup.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaBackupRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

abstract class GameFileVersionBackupJpaRepositoryAbstractTest {

    private GameFileVersionJpaBackupRepository gameFileVersionJpaRepository;

    @Autowired
    private GameFileVersionSpringRepository gameFileVersionSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        gameFileVersionJpaRepository = new GameFileVersionJpaBackupRepository(gameFileVersionSpringRepository);
        entityManager = entityManagerFactory.createEntityManager();
        cleanDatabase();
        persistTestEnqueuedFiles();
    }

    private void cleanDatabase() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM GAME_FILE_VERSION_BACKUP;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestEnqueuedFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("ALTER SEQUENCE seq_game_file_version_backup RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("""                         
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource1', 'someUrl1', 'someName1', 'someGameTitle1',
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281' ,
                        'someVersion1', 'someSize1', '2022-04-29T14:15:53', 'ENQUEUED', 'someFailedReason1');
                        
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource2', 'someUrl2', 'someName2', 'someGameTitle2', 
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion2', 'someSize2', '2022-05-29T14:15:53', 'SUCCESS', 'someFailedReason2');
                        
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource3', 'someUrl3', 'someName3', 'someGameTitle3', 
                        '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion3', 'someSize3', '2022-06-29T14:15:53', 'ENQUEUED', 'someFailedReason3');
                        
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource4', 'someUrl4', 'someName4', 'someGameTitle4',
                         '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion4', 'someSize4', '2022-07-29T14:15:53', 'FAILED', 'someFailedReason4');
                        
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource5', 'someUrl5', 'someName5', 'someGameTitle5',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion5', 'someSize5', '2022-08-29T14:15:53', 'IN_PROGRESS', 'someFailedReason5');
                        
                        INSERT INTO GAME_FILE_VERSION_BACKUP
                        (id, source, url, title, game_title, game_id, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version_backup'), 'someSource6', 'someUrl6', 'someName6', 'someGameTitle6',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
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
        Optional<GameFileVersionBackup> result = gameFileVersionJpaRepository.findOldestWaitingForDownload();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        var expectedResult = GameFileVersionBackup.builder()
                .id(result.get().getId())
                .source("someSource1")
                .url("someUrl1")
                .title("someName1")
                .gameId(result.get().getGameId())
                .gameTitle("someGameTitle1")
                .version("someVersion1")
                .size("someSize1")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(FileBackupStatus.ENQUEUED)
                .failedReason("someFailedReason1")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileVersionBackup> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersionBackup.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource1")
                        .url("someUrl1")
                        .title("someName1")
                        .gameId(result.getContent().get(0).getGameId())
                        .gameTitle("someGameTitle1")
                        .version("someVersion1")
                        .size("someSize1")
                        .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                        .status(FileBackupStatus.ENQUEUED)
                        .failedReason("someFailedReason1")
                        .build(),
                GameFileVersionBackup.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource3")
                        .url("someUrl3")
                        .title("someName3")
                        .gameId(result.getContent().get(1).getGameId())
                        .gameTitle("someGameTitle3")
                        .version("someVersion3")
                        .size("someSize3")
                        .dateCreated(LocalDateTime.parse("2022-06-29T14:15:53"))
                        .status(FileBackupStatus.ENQUEUED)
                        .failedReason("someFailedReason3")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldSave() {
        var newEnqueuedFileDownload =
                GameFileVersionBackup.builder()
                        .source("someSourceNew")
                        .url("someUrlNew")
                        .title("someNameNew")
                        .gameTitle("someGameTitleNew")
                        .version("someVersionNew")
                        .size("someSizeNew")
                        .status(FileBackupStatus.ENQUEUED)
                        .failedReason("someFailedReasonNew")
                        .build();

        var result = gameFileVersionJpaRepository.save(newEnqueuedFileDownload);

        assertNotNull(result.getId());
        assertNotNull(result.getDateCreated());
        Long recordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM GAME_FILE_VERSION_BACKUP f" +
                                " WHERE f.id = '" + result.getId() + "';")
                .getSingleResult();
        assertEquals(1, recordCount);
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileVersionBackup> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        GameFileVersionBackup expectedResult = GameFileVersionBackup.builder()
                .id(result.get().getId())
                .gameId(result.get().getGameId())
                .source("someSource5")
                .url("someUrl5")
                .title("someName5")
                .gameTitle("someGameTitle5")
                .version("someVersion5")
                .size("someSize5")
                .dateCreated(LocalDateTime.parse("2022-08-29T14:15:53"))
                .status(FileBackupStatus.IN_PROGRESS)
                .failedReason("someFailedReason5")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileVersionBackup> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersionBackup.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource2")
                        .url("someUrl2")
                        .title("someName2")
                        .gameId(result.getContent().get(0).getGameId())
                        .gameTitle("someGameTitle2")
                        .version("someVersion2")
                        .size("someSize2")
                        .dateCreated(LocalDateTime.parse("2022-05-29T14:15:53"))
                        .status(FileBackupStatus.SUCCESS)
                        .failedReason("someFailedReason2")
                        .build(),
                GameFileVersionBackup.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource4")
                        .url("someUrl4")
                        .title("someName4")
                        .gameId(result.getContent().get(1).getGameId())
                        .gameTitle("someGameTitle4")
                        .version("someVersion4")
                        .size("someSize4")
                        .dateCreated(LocalDateTime.parse("2022-07-29T14:15:53"))
                        .status(FileBackupStatus.FAILED)
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
        Optional<GameFileVersionBackup> result = gameFileVersionJpaRepository.findById(1L);

        assertTrue(result.isPresent());

        var expectedResult = GameFileVersionBackup.builder()
                .id(result.get().getId())
                .source("someSource1")
                .url("someUrl1")
                .title("someName1")
                .gameId(result.get().getGameId())
                .gameTitle("someGameTitle1")
                .version("someVersion1")
                .size("someSize1")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(FileBackupStatus.ENQUEUED)
                .failedReason("someFailedReason1")
                .build();
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileVersionBackup> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                GameFileVersionBackup.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource6")
                        .url("someUrl6")
                        .title("someName6")
                        .gameId(result.getContent().get(0).getGameId())
                        .gameTitle("someGameTitle6")
                        .version("someVersion6")
                        .size("someSize6")
                        .dateCreated(LocalDateTime.parse("2022-08-29T14:16:43"))
                        .status(FileBackupStatus.DISCOVERED)
                        .failedReason("someFailedReason6")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldFindByGameId() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<GameFileVersionBackup> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}