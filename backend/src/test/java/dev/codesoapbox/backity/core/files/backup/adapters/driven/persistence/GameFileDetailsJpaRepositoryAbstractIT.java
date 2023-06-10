package dev.codesoapbox.backity.core.files.backup.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsSpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.JpaGameFileDetailsMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
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

abstract class GameFileDetailsJpaRepositoryAbstractIT {

    private GameFileDetailsJpaRepository gameFileVersionJpaRepository;

    @Autowired
    private GameFileDetailsSpringRepository gameFileDetailsSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        JpaGameFileDetailsMapper mapper = Mappers.getMapper(JpaGameFileDetailsMapper.class);
        gameFileVersionJpaRepository = new GameFileDetailsJpaRepository(gameFileDetailsSpringRepository, mapper);
        entityManager = entityManagerFactory.createEntityManager();
        cleanDatabase();
        persistTestEnqueuedFiles();
    }

    private void cleanDatabase() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM GAME_FILE_DETAILS;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestEnqueuedFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("""                         
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('acde26d7-33c7-42ee-be16-bca91a604b48', 'someSource1', 'someUrl1', 'someTitle1', 
                        'someOriginalFileName1', 'someGameTitle1',
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281' ,
                        'someVersion1', 'someSize1', '2022-04-29T14:15:53', 'ENQUEUED', 'someFailedReason1');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('a6adc122-df20-4e2c-a975-7d4af7104704', 'someSource2', 'someUrl2', 'someTitle2', 
                        'someOriginalFileName2', 'someGameTitle2', 
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion2', 'someSize2', '2022-05-29T14:15:53', 'SUCCESS', 'someFailedReason2');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('0d4d181c-9a77-4146-bbd6-40f7d4453b5f', 'someSource3', 'someUrl3', 'someTitle3', 
                        'someOriginalFileName3', 'someGameTitle3', 
                        '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion3', 'someSize3', '2022-06-29T14:15:53', 'ENQUEUED', 'someFailedReason3');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('568afe65-018b-40fc-a8b4-481ded571ff8', 'someSource4', 'someUrl4', 'someTitle4', 
                        'someOriginalFileName4', 'someGameTitle4',
                         '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion4', 'someSize4', '2022-07-29T14:15:53', 'FAILED', 'someFailedReason4');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30', 'someSource5', 'someUrl5', 'someTitle5', 
                        'someOriginalFileName5', 'someGameTitle5',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion5', 'someSize5', '2022-08-29T14:15:53', 'IN_PROGRESS', 'someFailedReason5');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source, url, title, original_file_name, game_title, game_id, version, size, date_created, backup_status, backup_failed_reason) VALUES
                        ('3d65af79-a558-4f23-88bd-3c04e977e63f', 'someSource6', 'someUrl6', 'someTitle6', 
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
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findOldestWaitingForDownload();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());

        var expectedResult = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource1", "someUrl1", "someTitle1", "someOriginalFileName1",
                null, "someGameTitle1", result.get().getGameId(), "someVersion1", "someSize1",
                LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, "someFailedReason1");
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                new GameFileDetails(
                        result.getContent().get(0).getId(), "someSource1", "someUrl1", "someTitle1",
                        "someOriginalFileName1", null, "someGameTitle1",
                        result.getContent().get(0).getGameId(), "someVersion1", "someSize1",
                        LocalDateTime.parse("2022-04-29T14:15:53"),
                        null, FileBackupStatus.ENQUEUED, "someFailedReason1"),
                new GameFileDetails(
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
        var newEnqueuedFileDownload = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSourceNew", "someUrlNew", "someTitleNew", "someOriginalFileNameNew",
                null, "someGameTitleNew", GameId.newInstance().value().toString(),
                "someVersionNew", "someSizeNew", LocalDateTime.parse("2022-04-29T14:15:53"),
                null, FileBackupStatus.ENQUEUED, "someFailedReasonNew");

        var result = gameFileVersionJpaRepository.save(newEnqueuedFileDownload);

        assertNotNull(result.getId());
        assertNotNull(result.getDateCreated());
        Long recordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM GAME_FILE_DETAILS f" +
                                " WHERE f.id = '" + result.getId().value() + "';")
                .getSingleResult();
        assertEquals(1, recordCount);
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());

        var expectedResult = new GameFileDetails(
                result.get().getId(), "someSource5", "someUrl5", "someTitle5",
                "someOriginalFileName5", null, "someGameTitle5", result.get().getGameId(),
                "someVersion5", "someSize5", LocalDateTime.parse("2022-08-29T14:15:53"),
                null, FileBackupStatus.IN_PROGRESS, "someFailedReason5");
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                new GameFileDetails(
                        result.getContent().get(0).getId(), "someSource2", "someUrl2", "someTitle2",
                        "someOriginalFileName2", null, "someGameTitle2",
                        result.getContent().get(0).getGameId(), "someVersion2", "someSize2",
                        LocalDateTime.parse("2022-05-29T14:15:53"),
                        null, FileBackupStatus.SUCCESS, "someFailedReason2"),
                new GameFileDetails(
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
        GameFileDetailsId id = new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findById(id);

        assertTrue(result.isPresent());

        var expectedResult = new GameFileDetails(
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
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = new GameFileDetails(
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
        List<GameFileDetails> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}