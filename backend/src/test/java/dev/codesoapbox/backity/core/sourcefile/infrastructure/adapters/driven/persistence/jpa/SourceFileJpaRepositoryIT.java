package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.SourceFileNotFoundException;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static dev.codesoapbox.backity.core.game.domain.TestGame.anyBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class SourceFileJpaRepositoryIT {

    private final Assertions assertThat = new Assertions();

    @Autowired
    protected SourceFileJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected SourceFileJpaEntityMapper entityMapper;

    @Autowired
    protected Clock clock;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
    }

    private void persistSampleDependencies() {
        for (Game game : SampleGames.getAll()) {
            entityManager.persist(SampleGames.MAPPER.toEntity(game));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void saveShouldPersistNew() {
        persistSampleDependencies();
        SourceFile newSourceFile = TestSourceFile.gog();

        SourceFile result = repository.save(newSourceFile);
        entityManager.flush();

        assertEquals(result, newSourceFile);
        assertWasPersisted(newSourceFile);
    }

    private void assertEquals(SourceFile result, SourceFile expected) {
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private void assertWasPersisted(SourceFile newSourceFile) {
        SourceFileJpaEntity persistedEntity = entityManager.find(SourceFileJpaEntity.class,
                newSourceFile.getId().value());
        assertThat(persistedEntity)
                .extracting(entity -> entityMapper.toDomain(entity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(newSourceFile);
    }

    @Test
    void saveShouldModifyExisting() {
        persistSampleDependencies();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        persistSourceFiles(List.of(sourceFile));
        sourceFile.setFileTitle(new FileTitle("differentFileTitle"));

        repository.save(sourceFile);
        entityManager.flush();

        assertWasPersisted(sourceFile);
    }

    private void persistSourceFiles(List<SourceFile> sourceFiles) {
        for (SourceFile sourceFile : sourceFiles) {
            entityManager.persist(entityMapper.toEntity(sourceFile));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDates(EntityAuditControl entityAuditControl) {
        persistSampleDependencies();
        entityAuditControl.enable();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        repository.save(sourceFile);
        entityManager.flush();

        assertThat.datesWereUpdatedByAuditingHandler(sourceFile);
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"1.0.0,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String versionValue, boolean shouldFind) {
        persistSampleDependencies();
        persistSourceFiles(SampleSourceFiles.getAll());
        var version = new FileVersion(versionValue);
        var url = new SourceFileUrl("/downlink/some_game/some_file");
        boolean exists = repository.existsByUrlAndVersion(url, version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void shouldFindById() {
        persistSampleDependencies();
        persistSourceFiles(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        Optional<SourceFile> result = repository.findById(expectedSourceFile.getId());

        assertThat(result).get().usingRecursiveComparison()
                .isEqualTo(expectedSourceFile);
    }

    @Test
    void shouldGetById() {
        persistSampleDependencies();
        persistSourceFiles(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        SourceFile result = repository.getById(expectedSourceFile.getId());

        assertEquals(result, expectedSourceFile);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonexistentId = new SourceFileId("59e37c43-dda7-4c5f-87a3-c380ebb5f8ea");

        assertThatThrownBy(() -> repository.getById(nonexistentId))
                .isInstanceOf(SourceFileNotFoundException.class);
    }

    @Test
    void shouldFindByGameId() {
        persistSampleDependencies();
        persistSourceFiles(SampleSourceFiles.getAll());
        List<SourceFile> result = repository.findAllByGameId(SampleGames.GAME_1.getId());

        assertThat(result).containsExactlyInAnyOrder(
                SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get(),
                SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get()
        );
    }

    @Test
    void shouldFindAllById() {
        persistSampleDependencies();
        persistSourceFiles(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        List<SourceFile> result = repository.findAllByIdIn(List.of(expectedSourceFile.getId()));

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(List.of(expectedSourceFile));
    }

    @Test
    void shouldDeleteById() {
        persistSampleDependencies();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        persistSourceFiles(List.of(sourceFile));

        repository.deleteById(sourceFile.getId());

        var foundEntity = entityManager.find(SourceFileJpaEntity.class, sourceFile.getId().value());
        assertThat(foundEntity).isNull();
    }

    private static class SampleGames {

        public static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

        public static final Game GAME_1 = anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle(new GameTitle("Game 1"))
                .build();

        public static final Game GAME_2 = anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle(new GameTitle("Game 2"))
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1, GAME_2);
        }
    }

    private static class SampleSourceFiles {

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_1_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("acde26d7-33c7-42ee-be16-bca91a604b48"))
                .gameId(SampleGames.GAME_1.getId())
                .build();

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_2_FOR_GAME_1 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("a6adc122-df20-4e2c-a975-7d4af7104704"))
                .gameId(SampleGames.GAME_1.getId())
                .build();

        public static final Supplier<SourceFile> GOG_SOURCE_FILE_1_FOR_GAME_2 = () -> TestSourceFile.gogBuilder()
                .id(new SourceFileId("3d65af79-a558-4f23-88bd-3c04e977e63f"))
                .gameId(SampleGames.GAME_2.getId())
                .build();

        public static List<SourceFile> getAll() {
            return List.of(
                    SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get(), SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get(),
                    SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_2.get()
            );
        }
    }

    private class Assertions {

        void datesWereUpdatedByAuditingHandler(SourceFile sourceFile) {
            LocalDateTime now = LocalDateTime.now(clock);
            SourceFileJpaEntity persistedEntity = getPersistedEntity(sourceFile.getId());
            assertThat(persistedEntity.getDateCreated())
                    .isNotEqualTo(sourceFile.getDateCreated())
                    .isEqualTo(now);
            assertThat(persistedEntity.getDateModified())
                    .isNotEqualTo(sourceFile.getDateModified())
                    .isEqualTo(now);
        }

        private SourceFileJpaEntity getPersistedEntity(SourceFileId id) {
            return entityManager.find(SourceFileJpaEntity.class, id.value());
        }
    }
}