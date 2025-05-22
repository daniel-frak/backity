package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotFoundException;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.game.domain.TestGame.anyBuilder;
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

    private void populateDatabase(List<GameFile> gameFiles) {
        for (GameFile gameFile : gameFiles) {
            entityManager.persist(entityMapper.toEntity(gameFile));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldSave() {
        GameFile newGameFile = TestGameFile.gog();

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
                .extracting(entity -> entityMapper.toDomain(entity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newGameFile);
        assertThat(persistedEntity.getDateCreated()).isNotNull();
        assertThat(persistedEntity.getDateModified()).isNotNull();
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"1.0.0,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String version, boolean shouldFind) {
        populateDatabase(GAME_FILES.getAll());
        boolean exists = gameFileJpaRepository.existsByUrlAndVersion("/downlink/some_game/some_file", version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void shouldFindById() {
        populateDatabase(GAME_FILES.getAll());
        GameFile expectedGameFile = GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get();

        Optional<GameFile> result = gameFileJpaRepository.findById(expectedGameFile.getId());

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedGameFile);
    }

    @Test
    void shouldGetById() {
        populateDatabase(GAME_FILES.getAll());
        GameFile expectedGameFile = GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get();

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
    void shouldFindByGameId() {
        populateDatabase(GAME_FILES.getAll());
        List<GameFile> result = gameFileJpaRepository.findAllByGameId(EXISTING_GAMES.GAME_1.getId());

        assertThat(result).containsExactlyInAnyOrder(
                GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get(),
                GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get()
        );
    }

    @Test
    void shouldDeleteById() {
        GameFile gameFile = GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get();
        populateDatabase(List.of(gameFile));

        gameFileJpaRepository.deleteById(gameFile.getId());

        var foundEntity = entityManager.find(GameFileJpaEntity.class, gameFile.getId().value());
        assertThat(foundEntity).isNull();
    }

    private static class EXISTING_GAMES {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Game 1")
                .build();

        public static final Game GAME_2 = anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle("Game 2")
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1, GAME_2);
        }
    }

    private static class GAME_FILES {

        public static final Supplier<GameFile> GOG_GAME_FILE_1_FOR_GAME_1 = () -> TestGameFile.gogBuilder()
                .id(new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> GOG_GAME_FILE_2_FOR_GAME_1 = () -> TestGameFile.gogBuilder()
                .id(new GameFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<GameFile> GOG_GAME_FILE_1_FOR_GAME_2 = () -> TestGameFile.gogBuilder()
                .id(new GameFileId("3d65af79-a558-4f23-88bd-3c04e977e63f"))
                .gameId(EXISTING_GAMES.GAME_2.getId())
                .build();

        public static List<GameFile> getAll() {
            return List.of(
                    GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_1.get(), GAME_FILES.GOG_GAME_FILE_2_FOR_GAME_1.get(),
                    GAME_FILES.GOG_GAME_FILE_1_FOR_GAME_2.get()
            );
        }
    }
}