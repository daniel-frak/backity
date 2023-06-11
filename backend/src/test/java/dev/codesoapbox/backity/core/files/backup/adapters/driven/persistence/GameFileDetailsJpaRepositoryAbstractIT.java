package dev.codesoapbox.backity.core.files.backup.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsSpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.JpaGameFileDetailsMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
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
        entityManager.createNativeQuery("DELETE FROM GAME;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestEnqueuedFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("""
                INSERT INTO GAME
                (id, title, date_created, date_modified) VALUES
                ('1eec1c19-25bf-4094-b926-84b5bb8fa281', 'Test Game 1', '2022-04-29T14:15:53', '2022-04-29T14:15:53');
                INSERT INTO GAME
                (id, title, date_created, date_modified) VALUES
                ('5bdd248a-c3aa-487a-8479-0bfdb32f7ae5', 'Test Game 1', '2022-04-29T14:15:53', '2022-04-29T14:15:53');
                """).executeUpdate();
        entityManager.createNativeQuery("""                         
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id, 
                        version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('acde26d7-33c7-42ee-be16-bca91a604b48', 'someSourceId1', 'someUrl1', 'someFileTitle1', 
                        'someOriginalFileName1', 'someOriginalGameTitle1',
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281' ,
                        'someVersion1', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'ENQUEUED', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('a6adc122-df20-4e2c-a975-7d4af7104704', 'someSourceId2', 'someUrl2', 'someFileTitle2', 
                        'someOriginalFileName2', 'someOriginalGameTitle2', 
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion2', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'SUCCESS', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('0d4d181c-9a77-4146-bbd6-40f7d4453b5f', 'someSourceId3', 'someUrl3', 'someFileTitle3', 
                        'someOriginalFileName3', 'someOriginalGameTitle3', 
                        '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion3', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'ENQUEUED', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('568afe65-018b-40fc-a8b4-481ded571ff8', 'someSourceId4', 'someUrl4', 'someFileTitle4', 
                        'someOriginalFileName4', 'someOriginalGameTitle4',
                         '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion4', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'FAILED', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id, 
                        version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30', 'someSourceId5', 'someUrl5', 'someFileTitle5', 
                        'someOriginalFileName5', 'someOriginalGameTitle5',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion5', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'IN_PROGRESS', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('3d65af79-a558-4f23-88bd-3c04e977e63f', 'someSourceId6', 'someUrl6', 'someFileTitle6', 
                        'someOriginalFileName6', 'someOriginalGameTitle6',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion6', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'DISCOVERED', null);
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

        var expectedResult = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                TestGameFileDetails.GAME_FILE_DETAILS_1.get(),
                TestGameFileDetails.GAME_FILE_DETAILS_3.get()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldSave() {
        var newGameFileDetails = TestGameFileDetails.FULL_GAME_FILE_DETAILS.get();

        var result = gameFileVersionJpaRepository.save(newGameFileDetails);

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

        var expectedResult = TestGameFileDetails.GAME_FILE_DETAILS_5.get();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(

                TestGameFileDetails.GAME_FILE_DETAILS_2.get(),
                TestGameFileDetails.GAME_FILE_DETAILS_4.get()
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

        var expectedResult = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        assertThat(result.get())
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = TestGameFileDetails.GAME_FILE_DETAILS_6.get();
        assertEquals(singletonList(expectedResult), result.getContent());
    }

    @Test
    void shouldFindByGameId() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<GameFileDetails> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}