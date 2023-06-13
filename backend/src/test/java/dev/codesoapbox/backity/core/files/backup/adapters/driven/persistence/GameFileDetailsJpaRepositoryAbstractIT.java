package dev.codesoapbox.backity.core.files.backup.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaEntity;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaEntityMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.config.gamefiledetails.GameFileDetailsJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@Import(GameFileDetailsJpaRepositoryBeanConfig.class)
@Transactional
abstract class GameFileDetailsJpaRepositoryAbstractIT {

    @Autowired
    protected GameFileDetailsJpaRepository gameFileVersionJpaRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected GameFileDetailsJpaEntityMapper entityMapper;

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
        persistTestData();
    }

    private void persistTestData() {
        List<GameFileDetailsJpaEntity> gameFileDetailsEntities = Stream.of(
                        GAME_FILE_DETAILS_1.get(), GAME_FILE_DETAILS_2.get(), GAME_FILE_DETAILS_3.get(),
                        GAME_FILE_DETAILS_4.get(), GAME_FILE_DETAILS_5.get(), GAME_FILE_DETAILS_6.get())
                .map(entityMapper::toEntity)
                .toList();
        persistAssociatedGames(gameFileDetailsEntities);
        gameFileDetailsEntities.forEach(entityManager::persistAndFlush);
    }

    private void persistAssociatedGames(List<GameFileDetailsJpaEntity> gameFileDetailsEntities) {
        gameFileDetailsEntities.stream()
                .map(GameFileDetailsJpaEntity::getGame)
                .distinct()
                .forEach(entityManager::persistAndFlush);
    }

    @Test
    void shouldFindOldestWaitingForDownload() {
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findOldestWaitingForDownload();

        assertThat(result)
                .hasValueSatisfying(r -> assertThat(r)
                        .usingRecursiveComparison()
                        .ignoringFields("dateCreated", "dateModified")
                        .isEqualTo(GAME_FILE_DETAILS_1.get()));
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllWaitingForDownload(Pageable.unpaged());

        var expectedResult = List.of(
                GAME_FILE_DETAILS_1.get(),
                GAME_FILE_DETAILS_3.get()
        );
        assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldSave() {
        GameFileDetails newGameFileDetails = TestGameFileDetails.full().build();

        GameFileDetails result = gameFileVersionJpaRepository.save(newGameFileDetails);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newGameFileDetails);

        GameFileDetailsJpaEntity persistedEntity = entityManager.find(GameFileDetailsJpaEntity.class,
                newGameFileDetails.getId().value());
        assertThat(persistedEntity)
                .extracting(entity -> entityMapper.toModel(entity))
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newGameFileDetails);
        assertThat(persistedEntity.getDateCreated()).isNotNull();
        assertThat(persistedEntity.getDateModified()).isNotNull();
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<GameFileDetails> result = gameFileVersionJpaRepository.findCurrentlyDownloading();

        assertThat(result)
                .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison()
                        .ignoringFields("dateCreated", "dateModified")
                        .isEqualTo(GAME_FILE_DETAILS_5.get()));
    }

    @Test
    void shouldFindAllProcessed() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllProcessed(Pageable.unpaged());

        var expectedResult = List.of(
                GAME_FILE_DETAILS_2.get(),
                GAME_FILE_DETAILS_4.get()
        );
        assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
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
                .hasValueSatisfying(r -> assertThat(r).usingRecursiveComparison()
                        .ignoringFields("dateCreated", "dateModified")
                        .isEqualTo(GAME_FILE_DETAILS_1.get()));
    }

    @Test
    void shouldFindAllDiscovered() {
        Page<GameFileDetails> result = gameFileVersionJpaRepository.findAllDiscovered(Pageable.unpaged());

        var expectedResult = singletonList(GAME_FILE_DETAILS_6.get());
        assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindByGameId() {
        GameId id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<GameFileDetails> result = gameFileVersionJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}