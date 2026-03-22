package dev.codesoapbox.backity.core.sourcefile.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileId;
import dev.codesoapbox.backity.core.sourcefile.domain.TestSourceFile;
import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.SourceFileNotFoundException;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntity;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.game.domain.TestGame.anyBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class SourceFileJpaRepositoryIT {

    @Autowired
    protected SourceFileJpaRepository sourceFileJpaRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected SourceFileJpaEntityMapper entityMapper;

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

    private void populateDatabase(List<SourceFile> sourceFiles) {
        for (SourceFile sourceFile : sourceFiles) {
            entityManager.persist(entityMapper.toEntity(sourceFile));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldSave() {
        SourceFile newSourceFile = TestSourceFile.gog();

        SourceFile result = sourceFileJpaRepository.save(newSourceFile);
        entityManager.flush();

        assertEquals(result, newSourceFile);
        assertWasPersisted(newSourceFile);
    }

    private void assertEquals(SourceFile result, SourceFile expected) {
        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expected);
    }

    private void assertWasPersisted(SourceFile newSourceFile) {
        SourceFileJpaEntity persistedEntity = entityManager.find(SourceFileJpaEntity.class,
                newSourceFile.getId().value());
        assertThat(persistedEntity)
                .extracting(entity -> entityMapper.toDomain(entity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newSourceFile);
        assertThat(persistedEntity.getDateCreated()).isNotNull();
        assertThat(persistedEntity.getDateModified()).isNotNull();
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"1.0.0,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String version, boolean shouldFind) {
        populateDatabase(SOURCE_FILES.getAll());
        boolean exists = sourceFileJpaRepository.existsByUrlAndVersion("/downlink/some_game/some_file", version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void shouldFindById() {
        populateDatabase(SOURCE_FILES.getAll());
        SourceFile expectedSourceFile = SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        Optional<SourceFile> result = sourceFileJpaRepository.findById(expectedSourceFile.getId());

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedSourceFile);
    }

    @Test
    void shouldGetById() {
        populateDatabase(SOURCE_FILES.getAll());
        SourceFile expectedSourceFile = SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        SourceFile result = sourceFileJpaRepository.getById(expectedSourceFile.getId());

        assertEquals(result, expectedSourceFile);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonexistentId = new SourceFileId("59e37c43-dda7-4c5f-87a3-c380ebb5f8ea");

        assertThatThrownBy(() -> sourceFileJpaRepository.getById(nonexistentId))
                .isInstanceOf(SourceFileNotFoundException.class);
    }

    @Test
    void shouldFindByGameId() {
        populateDatabase(SOURCE_FILES.getAll());
        List<SourceFile> result = sourceFileJpaRepository.findAllByGameId(EXISTING_GAMES.GAME_1.getId());

        assertThat(result).containsExactlyInAnyOrder(
                SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get(),
                SOURCE_FILES.GOG_SOURCE_FILE_2_FOR_GAME_1.get()
        );
    }

    @Test
    void shouldFindAllById() {
        populateDatabase(SOURCE_FILES.getAll());
        SourceFile expectedSourceFile = SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        List<SourceFile> result = sourceFileJpaRepository.findAllByIdIn(List.of(expectedSourceFile.getId()));

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(List.of(expectedSourceFile));
    }

    @Test
    void shouldDeleteById() {
        SourceFile sourceFile = SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        populateDatabase(List.of(sourceFile));

        sourceFileJpaRepository.deleteById(sourceFile.getId());

        var foundEntity = entityManager.find(SourceFileJpaEntity.class, sourceFile.getId().value());
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

    private static class SOURCE_FILES {

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_1_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_2_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(EXISTING_GAMES.GAME_1.getId())
                .build();

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_1_FOR_GAME_2 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("3d65af79-a558-4f23-88bd-3c04e977e63f"))
                .gameId(EXISTING_GAMES.GAME_2.getId())
                .build();

        public static List<SourceFile> getAll() {
            return List.of(
                    SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_1.get(), SOURCE_FILES.GOG_SOURCE_FILE_2_FOR_GAME_1.get(),
                    SOURCE_FILES.GOG_SOURCE_FILE_1_FOR_GAME_2.get()
            );
        }
    }
}