package dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntity;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.domain.*;
import dev.codesoapbox.backity.core.sourcefile.domain.exceptions.SourceFileNotFoundException;
import dev.codesoapbox.backity.testing.jpa.TestJpaPersistenceAdapter;
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

    @Autowired
    protected SourceFileJpaRepository repository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected SourceFileJpaEntityMapper entityMapper;

    @Autowired
    protected Clock clock;

    private TestJpaPersistenceAdapter<SourceFile, SourceFileJpaEntity> sourceFileJpaAdapter;
    private TestJpaPersistenceAdapter<Game, GameJpaEntity> gameJpaAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
        sourceFileJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (entityManager, domainObject) ->
                        entityManager.find(SourceFileJpaEntity.class, domainObject.getId().value())
        );
        gameJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                SampleGames.MAPPER::toEntity,
                SampleGames.MAPPER::toDomain,
                (entityManager, domainObject) ->
                        entityManager.find(GameJpaEntity.class, domainObject.getId().value())
        );
    }

    @Test
    void saveShouldPersistNew() {
        persistSampleDependencies();
        SourceFile sourceFile = TestSourceFile.gog();

        repository.save(sourceFile);
        entityManager.flush();

        SourceFile persistedAggregate = sourceFileJpaAdapter.getPersistedDomainObject(sourceFile);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(sourceFile);
    }

    private void persistSampleDependencies() {
        gameJpaAdapter.persist(SampleGames.getAll());
    }

    @Test
    void saveShouldModifyExisting() {
        persistSampleData();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        sourceFile.setFileTitle(new FileTitle("differentFileTitle"));

        repository.save(sourceFile);
        entityManager.flush();

        SourceFile persistedAggregate = sourceFileJpaAdapter.getPersistedDomainObject(sourceFile);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(sourceFile);
    }

    void persistSampleData() {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenNew(EntityAuditControl entityAuditControl) {
        persistSampleDependencies();
        entityAuditControl.enable();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        repository.save(sourceFile);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        SourceFile persistedAggregate = sourceFileJpaAdapter.getPersistedDomainObject(sourceFile);
        assertThat(persistedAggregate.getDateCreated())
                .isNotEqualTo(sourceFile.getDateCreated())
                .isEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(sourceFile.getDateModified())
                .isEqualTo(now);
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenExisting(EntityAuditControl entityAuditControl) {
        persistSampleData();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        entityAuditControl.enable();
        sourceFile.setFileTitle(new FileTitle("differentFileTitle"));

        repository.save(sourceFile);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        SourceFile persistedAggregate = sourceFileJpaAdapter.getPersistedDomainObject(sourceFile);
        assertThat(persistedAggregate.getDateCreated())
                .isEqualTo(sourceFile.getDateCreated())
                .isNotEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(sourceFile.getDateModified())
                .isEqualTo(now);
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"1.0.0,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String versionValue, boolean shouldFind) {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        var version = new FileVersion(versionValue);
        var url = new SourceFileUrl("/downlink/some_game/some_file");

        boolean exists = repository.existsByUrlAndVersion(url, version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void findByIdShouldReturnAggregateGivenItExists() {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        Optional<SourceFile> result = repository.findById(expectedSourceFile.getId());

        assertThat(result)
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedSourceFile);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalGivenNotFound() {
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        Optional<SourceFile> result = repository.findById(sourceFile.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void getByIdShouldReturnAggregateGivenItExists() {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        SourceFile result = repository.getById(expectedSourceFile.getId());

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedSourceFile);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonexistentId = new SourceFileId("59e37c43-dda7-4c5f-87a3-c380ebb5f8ea");

        assertThatThrownBy(() -> repository.getById(nonexistentId))
                .isInstanceOf(SourceFileNotFoundException.class);
    }

    @Test
    void findAllByGameIdShouldReturnAllDataForAggregate() {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        List<SourceFile> result = repository.findAllByGameId(SampleGames.GAME_1.getId());

        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get(),
                        SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get()
                );
    }

    @Test
    void findAllByIdInShouldReturnAllDataForAggregate() {
        persistSampleDependencies();
        sourceFileJpaAdapter.persist(SampleSourceFiles.getAll());
        SourceFile expectedSourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();

        List<SourceFile> result = repository.findAllByIdIn(List.of(expectedSourceFile.getId()));

        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedSourceFile);
    }

    @Test
    void deleteByIdShouldDeleteAggregate() {
        persistSampleDependencies();
        SourceFile sourceFile = SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get();
        sourceFileJpaAdapter.persist(sourceFile);

        repository.deleteById(sourceFile.getId());

        assertThat(sourceFileJpaAdapter.exists(sourceFile)).isFalse();
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
                    SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_1.get(),
                    SampleSourceFiles.GOG_SOURCE_FILE_2_FOR_GAME_1.get(),
                    SampleSourceFiles.GOG_SOURCE_FILE_1_FOR_GAME_2.get()
            );
        }
    }
}