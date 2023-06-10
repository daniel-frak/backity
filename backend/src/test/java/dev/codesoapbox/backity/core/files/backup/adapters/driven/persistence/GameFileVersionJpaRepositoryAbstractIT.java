package dev.codesoapbox.backity.core.files.backup.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileVersionSpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.JpaGameFileVersionMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

abstract class GameFileVersionJpaRepositoryAbstractIT {

    private GameFileVersionJpaRepository gameFileVersionJpaRepository;

    @Autowired
    private GameFileVersionSpringRepository gameFileVersionSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        JpaGameFileVersionMapper mapper = Mappers.getMapper(JpaGameFileVersionMapper.class);
        gameFileVersionJpaRepository = new GameFileVersionJpaRepository(gameFileVersionSpringRepository, mapper);
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
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource1', 'someUrl1', 'someTitle1', 
                        'someOriginalFileName1', 'someGameTitle1',
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281' ,
                        'someVersion1', 'someSize1', '2022-04-29T14:15:53', 'ENQUEUED', 'someFailedReason1');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource2', 'someUrl2', 'someTitle2', 
                        'someOriginalFileName2', 'someGameTitle2', 
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion2', 'someSize2', '2022-05-29T14:15:53', 'SUCCESS', 'someFailedReason2');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource3', 'someUrl3', 'someTitle3', 
                        'someOriginalFileName3', 'someGameTitle3', 
                        '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion3', 'someSize3', '2022-06-29T14:15:53', 'ENQUEUED', 'someFailedReason3');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource4', 'someUrl4', 'someTitle4', 
                        'someOriginalFileName4', 'someGameTitle4',
                         '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion4', 'someSize4', '2022-07-29T14:15:53', 'FAILED', 'someFailedReason4');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource5', 'someUrl5', 'someTitle5', 
                        'someOriginalFileName5', 'someGameTitle5',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion5', 'someSize5', '2022-08-29T14:15:53', 'IN_PROGRESS', 'someFailedReason5');
                        
                        INSERT INTO GAME_FILE_VERSION
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        (NEXTVAL('seq_game_file_version'), 'someSource6', 'someUrl6', 'someTitle6', 
                        'someOriginalFileName6', 'someGameTitle6',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion6', 'someSize6', '2022-09-29T14:16:43', 'DISCOVERED', 'someFailedReason6');
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

        var expectedResult = new GameFileVersion(
                1L, "someSource1", "someUrl1", "someTitle1", "someOriginalFileName1",
                null, "someGameTitle1", result.get().getGameId(), "someVersion1", "someSize1",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, "someFailedReason1");
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                new GameFileVersion(
                        result.getContent().get(0).getId(), "someSource1", "someUrl1", "someTitle1",
                        "someOriginalFileName1", null, "someGameTitle1",
                        result.getContent().get(0).getGameId(), "someVersion1", "someSize1",
                        LocalDateTime.parse("2022-04-29T14:15:53"),
                        null, FileBackupStatus.ENQUEUED, "someFailedReason1"),
                new GameFileVersion(
                        result.getContent().get(1).getId(), "someSource3", "someUrl3", "someTitle3",
                        "someOriginalFileName3", null, "someGameTitle3",
                        result.getContent().get(1).getGameId(), "someVersion3", "someSize3",
                        LocalDateTime.parse("2022-06-29T14:15:53"), null, FileBackupStatus.ENQUEUED,
                        "someFailedReason3")
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldSave() {
        var newEnqueuedFileDownload = new GameFileVersion(
                1L, "someSourceNew", "someUrlNew", "someTitleNew", "someOriginalFileNameNew",
                null, "someGameTitleNew", GameId.newInstance().value().toString(),
                "someVersionNew", "someSizeNew", LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, "someFailedReasonNew");

        var result = gameFileVersionJpaRepository.save(newEnqueuedFileDownload);

        assertNotNull(result.getId());
        assertNotNull(result.getDateCreated());
        Long recordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM GAME_FILE_VERSION f" +
                                " WHERE f.id = '" + result.getId() + "';")
                .getSingleResult();
        assertEquals(1, recordCount);
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileVersion> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());

        var expectedResult = new GameFileVersion(
                result.get().getId(), "someSource5", "someUrl5", "someTitle5",
                "someOriginalFileName5", null, "someGameTitle5", result.get().getGameId(),
                "someVersion5", "someSize5", LocalDateTime.parse("2022-08-29T14:15:53"),
                null, FileBackupStatus.IN_PROGRESS, "someFailedReason5");
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                new GameFileVersion(
                        result.getContent().get(0).getId(), "someSource2", "someUrl2", "someTitle2",
                        "someOriginalFileName2", null, "someGameTitle2",
                        result.getContent().get(0).getGameId(), "someVersion2", "someSize2",
                        LocalDateTime.parse("2022-05-29T14:15:53"),
                        null, FileBackupStatus.SUCCESS, "someFailedReason2"),
                new GameFileVersion(
                        result.getContent().get(1).getId(), "someSource4", "someUrl4", "someTitle4",
                        "someOriginalFileName4", null, "someGameTitle4",
                        result.getContent().get(1).getGameId(), "someVersion4", "someSize4",
                        LocalDateTime.parse("2022-07-29T14:15:53"),
                        null, FileBackupStatus.FAILED, "someFailedReason4")
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

        var expectedResult = new GameFileVersion(
                result.get().getId(), "someSource1", "someUrl1", "someTitle1",
                "someOriginalFileName1", null, "someGameTitle1", result.get().getGameId(),
                "someVersion1", "someSize1", LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, "someFailedReason1");
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileVersion> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = new GameFileVersion(
                result.getContent().get(0).getId(), "someSource6", "someUrl6", "someTitle6",
                "someOriginalFileName6", null, "someGameTitle6",
                result.getContent().get(0).getGameId(), "someVersion6", "someSize6", 
                LocalDateTime.parse("2022-09-29T14:16:43"), null, FileBackupStatus.DISCOVERED,
                "someFailedReason6");
        assertEquals(singletonList(expectedResult), result.getContent());
    }

    @Test
    void shouldFindByGameId() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<GameFileVersion> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}