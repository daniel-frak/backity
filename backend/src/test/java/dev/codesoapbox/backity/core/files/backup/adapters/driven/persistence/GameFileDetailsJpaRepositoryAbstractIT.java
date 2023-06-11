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
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

abstract class GameFileDetailsJpaRepositoryAbstractIT {

    private GameFileDetailsJpaRepository gameFileVersionJpaRepository;

    @Autowired
    private GameFileDetailsSpringRepository gameFileDetailsSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_1 = () -> TestGameFileDetails.enqueued()
            .id(new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_2 = () -> TestGameFileDetails.successful()
            .id(new GameFileDetailsId(UUID.fromString("a6adc122-df20-4e2c-a975-7d4af7104704")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_3 = () -> TestGameFileDetails.enqueued()
            .id(new GameFileDetailsId(UUID.fromString("0d4d181c-9a77-4146-bbd6-40f7d4453b5f")))
            .gameId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .build();

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_4 = () -> TestGameFileDetails.failed()
            .id(new GameFileDetailsId(UUID.fromString("568afe65-018b-40fc-a8b4-481ded571ff8")))
            .gameId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .build();

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_5 = () -> TestGameFileDetails.inProgress()
            .id(new GameFileDetailsId(UUID.fromString("0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<GameFileDetails> GAME_FILE_DETAILS_6 = () -> TestGameFileDetails.discovered()
            .id(new GameFileDetailsId(UUID.fromString("3d65af79-a558-4f23-88bd-3c04e977e63f")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

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
                        ('acde26d7-33c7-42ee-be16-bca91a604b48', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle',
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281' ,
                        'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'ENQUEUED', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason, file_path) VALUES
                        ('a6adc122-df20-4e2c-a975-7d4af7104704', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle', 
                        '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'SUCCESS', null,
                        'someFilePath');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('0d4d181c-9a77-4146-bbd6-40f7d4453b5f', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle', 
                        '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'ENQUEUED', null);
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('568afe65-018b-40fc-a8b4-481ded571ff8', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle',
                         '5bdd248a-c3aa-487a-8479-0bfdb32f7ae5',
                         'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'FAILED', 'someFailedReason');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id, 
                        version, size, date_created, date_modified, status, failed_reason, file_path) VALUES
                        ('0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'IN_PROGRESS', null,
                        'tempFilePath');
                        
                        INSERT INTO GAME_FILE_DETAILS
                        (id, source_id, url, file_title, original_file_name, original_game_title, game_id,
                         version, size, date_created, date_modified, status, failed_reason) VALUES
                        ('3d65af79-a558-4f23-88bd-3c04e977e63f', 'someSourceId', 'someUrl', 'someFileTitle', 
                        'someOriginalFileName', 'someOriginalGameTitle',
                         '1eec1c19-25bf-4094-b926-84b5bb8fa281',
                        'someVersion', '5 KB', '2022-04-29T14:15:53', '2023-04-29T14:15:53', 'DISCOVERED', null);
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

        assertThat(result)
                .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(GAME_FILE_DETAILS_1.get()));
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        var expectedResult = List.of(
                GAME_FILE_DETAILS_1.get(),
                GAME_FILE_DETAILS_3.get()
        );
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldSave() {
        GameFileDetails newGameFileDetails = TestGameFileDetails.full().build();

        GameFileDetails result = gameFileVersionJpaRepository.save(newGameFileDetails);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newGameFileDetails);
        Long recordCount = (Long) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM GAME_FILE_DETAILS f" +
                                " WHERE f.id = '" + result.getId().value() + "';")
                .getSingleResult();
        assertThat(recordCount).isOne();
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertThat(result)
                .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(GAME_FILE_DETAILS_5.get()));
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        var expectedResult = List.of(
                GAME_FILE_DETAILS_2.get(),
                GAME_FILE_DETAILS_4.get()
        );
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldFindIfExistsByUrlAndVersion() {
        boolean existsActual = gameFileVersionJpaRepository.existsByUrlAndVersion(
                "someUrl", "someVersion");
        boolean existsMissing = gameFileVersionJpaRepository.existsByUrlAndVersion(
                "someUrl", "fakeVersion");
        assertThat(existsActual).isTrue();
        assertThat(existsMissing).isFalse();
    }

    @Test
    void shouldFindById() {
        GameFileDetailsId id = new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findById(id);

        assertThat(result)
                .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison().isEqualTo(GAME_FILE_DETAILS_1.get()));
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        var expectedResult = singletonList(GAME_FILE_DETAILS_6.get());
        assertThat(result.getContent()).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldFindByGameId() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<GameFileDetails> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}