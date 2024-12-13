package dev.codesoapbox.backity.core.gamefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.game.domain.TestGame.aGame;
import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Transactional
abstract class GameFileJpaRepositoryAbstractIT {

    @Autowired
    protected GameFileJpaRepository gameFileJpaRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected GameFileJpaEntityMapper entityMapper;

    @Autowired
    protected DomainEventPublisher domainEventPublisher;

    @BeforeEach
    void setUp() {
        persistExistingDependencies();
        entityManager.flush();
        entityManager.clear();
    }

    private void persistExistingDependencies() {
        for (Game game : EXISTING_GAMES.getAll()) {
            entityManager.persist(EXISTING_GAMES.MAPPER.toEntity(game));
        }
    }

    @Test
    void shouldFindOldestWaitingForDownload() {
        populateDatabase(FILE_DETAILS.getAll());
        Optional<GameFile> result = gameFileJpaRepository.findOldestWaitingForDownload();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(FILE_DETAILS.ENQUEUED_FOR_GAME_1.get());
    }

    private void populateDatabase(List<GameFile> gameFiles) {
        for (GameFile gameFile : gameFiles) {
            entityManager.persist(entityMapper.toEntity(gameFile));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        populateDatabase(FILE_DETAILS.getAll());
        var pagination = new Pagination(0, 2);
        Page<GameFile> result = gameFileJpaRepository.findAllWaitingForDownload(pagination);

        Page<GameFile> expectedResult = new Page<>(
                List.of(FILE_DETAILS.ENQUEUED_FOR_GAME_1.get(), FILE_DETAILS.ENQUEUED_FOR_GAME_2.get()),
                2, 1, 2, 2, 0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult.content());
    }

    @Test
    void shouldSave() {
        GameFile newGameFile = fullGameFile().build();

        GameFile result = gameFileJpaRepository.save(newGameFile);
        entityManager.flush();

        assertEquals(result, newGameFile);
        assertWasPersisted(newGameFile);
    }

    private void assertEquals(GameFile result, GameFile expected) {
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expected);
    }

    private void assertWasPersisted(GameFile newGameFile) {
        GameFileJpaEntity persistedEntity = entityManager.find(GameFileJpaEntity.class,
                newGameFile.getId().value());
        assertThat(persistedEntity)
                .extracting(entity -> entityMapper.toModel(entity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newGameFile);
        assertThat(persistedEntity.getDateCreated()).isNotNull();
        assertThat(persistedEntity.getDateModified()).isNotNull();
    }

    @Test
    void saveShouldPublishEventsAfterCommitting() {
        GameFile gameFile = fullGameFile().build();
        gameFile.markAsInProgress();
        gameFileJpaRepository.save(gameFile);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        verify(domainEventPublisher).publish(any(FileBackupStartedEvent.class));
    }

    @Test
    void saveShouldClearEvents() {
        GameFile gameFile = fullGameFile().build();
        gameFile.markAsInProgress();
        gameFileJpaRepository.save(gameFile);

        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);

        assertThat(gameFile.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        populateDatabase(FILE_DETAILS.getAll());
        Optional<GameFile> result = gameFileJpaRepository.findCurrentlyDownloading();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(FILE_DETAILS.IN_PROGRESS_FOR_GAME_2.get());
    }

    @Test
    void shouldFindAllProcessed() {
        populateDatabase(FILE_DETAILS.getAll());
        var pagination = new Pagination(0, 2);
        Page<GameFile> result = gameFileJpaRepository.findAllProcessed(pagination);

        List<GameFile> expectedResult = List.of(
                FILE_DETAILS.SUCCESSFUL_FOR_GAME_1.get(),
                FILE_DETAILS.FAILED_FOR_GAME_2.get()
        );
        assertThat(result.content())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"someVersion,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String version, boolean shouldFind) {
        populateDatabase(FILE_DETAILS.getAll());
        boolean exists = gameFileJpaRepository.existsByUrlAndVersion("someUrl", version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void shouldFindById() {
        populateDatabase(FILE_DETAILS.getAll());
        GameFile expectedGameFile = FILE_DETAILS.ENQUEUED_FOR_GAME_1.get();

        Optional<GameFile> result = gameFileJpaRepository.findById(expectedGameFile.getId());

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedGameFile);
    }

    @Test
    void shouldGetById() {
        populateDatabase(FILE_DETAILS.getAll());
        GameFile expectedGameFile = FILE_DETAILS.ENQUEUED_FOR_GAME_1.get();

        GameFile result = gameFileJpaRepository.getById(expectedGameFile.getId());

        assertEquals(result, expectedGameFile);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonexistentId = new GameFileId("59e37c43-dda7-4c5f-87a3-c380ebb5f8ea");

        assertThatThrownBy(() -> gameFileJpaRepository.getById(nonexistentId))
                .isInstanceOf(GameFileNotFoundException.class);
    }

    @Test
    void shouldFindAllDiscovered() {
        populateDatabase(FILE_DETAILS.getAll());
        var pagination = new Pagination(0, 2);
        Page<GameFile> result = gameFileJpaRepository.findAllDiscovered(pagination);

        List<GameFile> expectedResult = singletonList(FILE_DETAILS.DISCOVERED_FOR_GAME_1.get());
        assertThat(result.content())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindByGameId() {
        populateDatabase(FILE_DETAILS.getAll());
        List<GameFile> result = gameFileJpaRepository.findAllByGameId(EXISTING_GAMES.GAME_1.getId());

        assertThat(result).containsExactlyInAnyOrder(
                FILE_DETAILS.ENQUEUED_FOR_GAME_1.get(),
                FILE_DETAILS.SUCCESSFUL_FOR_GAME_1.get(),
                FILE_DETAILS.DISCOVERED_FOR_GAME_1.get()
        );
    }

    @Test
    void shouldDeleteById() {
        GameFile gameFile = FILE_DETAILS.ENQUEUED_FOR_GAME_1.get();
        populateDatabase(List.of(gameFile));

        gameFileJpaRepository.deleteById(gameFile.getId());

        var foundEntity = entityManager.find(GameFileJpaEntity.class, gameFile.getId().value());
        assertThat(foundEntity).isNull();
    }

    private static class EXISTING_GAMES {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = aGame()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Game 1")
                .build();

        public static final Game GAME_2 = aGame()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle("Game 2")
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1, GAME_2);
        }
    }

    private static class FILE_DETAILS {

        public static final Supplier<GameFile> ENQUEUED_FOR_GAME_1 = () -> enqueuedGameFile()
                .id(new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> SUCCESSFUL_FOR_GAME_1 = () -> successfulGameFile()
                .id(new GameFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> DISCOVERED_FOR_GAME_1 = () -> discoveredGameFile()
                .id(new GameFileId("3d65af79-a558-4f23-88bd-3c04e977e63f"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> ENQUEUED_FOR_GAME_2 = () -> enqueuedGameFile()
                .id(new GameFileId("0d4d181c-9a77-4146-bbd6-40f7d4453b5f"))
                .gameId(EXISTING_GAMES.GAME_2.getId())
                .build();

        public static final Supplier<GameFile> FAILED_FOR_GAME_2 = () -> failedGameFile()
                .id(new GameFileId("568afe65-018b-40fc-a8b4-481ded571ff8"))
                .gameId(EXISTING_GAMES.GAME_2.getId())
                .build();

        public static final Supplier<GameFile> IN_PROGRESS_FOR_GAME_2 = () -> inProgressGameFile()
                .id(new GameFileId("0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30"))
                .gameId(EXISTING_GAMES.GAME_2.getId())
                .build();

        public static List<GameFile> getAll() {
            return List.of(
                    FILE_DETAILS.ENQUEUED_FOR_GAME_1.get(), FILE_DETAILS.SUCCESSFUL_FOR_GAME_1.get(),
                    FILE_DETAILS.ENQUEUED_FOR_GAME_2.get(), FILE_DETAILS.FAILED_FOR_GAME_2.get(),
                    FILE_DETAILS.IN_PROGRESS_FOR_GAME_2.get(), FILE_DETAILS.DISCOVERED_FOR_GAME_1.get()
            );
        }
    }
}