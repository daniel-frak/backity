package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.game.domain.exceptions.GameNotFoundException;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.testing.jpa.TestJpaPersistenceAdapter;
import dev.codesoapbox.backity.testing.jpa.annotations.MultiDatabaseRepositoryTest;
import dev.codesoapbox.backity.testing.jpa.extensions.EntityAuditControl;
import dev.codesoapbox.backity.testing.time.FakeClock;
import dev.codesoapbox.backity.testing.time.config.FakeTimeBeanConfig;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@MultiDatabaseRepositoryTest
@Transactional // Required by @JpaRepositoryTest
abstract class GameJpaRepositoryIT {

    @Autowired
    private GameJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameJpaEntityMapper entityMapper;

    @Autowired
    private FakeClock clock;
    
    private TestJpaPersistenceAdapter<Game, GameJpaEntity> gameJpaAdapter;

    @SuppressWarnings("JUnitMalformedDeclaration")
    @BeforeEach
    void setUp(EntityAuditControl entityAuditControl) {
        entityAuditControl.disable();
        gameJpaAdapter = new TestJpaPersistenceAdapter<>(
                entityManager,
                entityMapper::toEntity,
                entityMapper::toDomain,
                (em, obj) -> em.find(GameJpaEntity.class, obj.getId().value())
        );
    }

    @Test
    void saveShouldPersistNew() {
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle(new GameTitle("New Title"))
                .build();

        repository.save(game);
        entityManager.flush();

        Game persistedAggregate = gameJpaAdapter.getPersistedDomainObject(game);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldNotSaveGivenGameWithTitleAlreadyExists() {
        persistSampleData();
        GameTitle existingTitle = SampleGames.GAME_CREATED_TODAY.get().getTitle();
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle(existingTitle)
                .build();

        repository.save(game);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }
    
    void persistSampleData() {
        gameJpaAdapter.persist(SampleGames.getAll());
    }

    @Test
    void saveShouldModifyExisting() {
        persistSampleData();
        Game game = SampleGames.GAME_CREATED_TODAY.get();
        game.setTitle(new GameTitle("New Title"));

        repository.save(game);
        entityManager.flush();

        Game persistedAggregate = gameJpaAdapter.getPersistedDomainObject(game);
        assertThat(persistedAggregate)
                .usingRecursiveComparison()
                .isEqualTo(game);
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenNew(EntityAuditControl entityAuditControl) {
        entityAuditControl.enable();
        Game game = SampleGames.GAME_CREATED_YESTERDAY.get();

        repository.save(game);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        Game persistedAggregate = gameJpaAdapter.getPersistedDomainObject(game);
        assertThat(persistedAggregate.getDateCreated())
                .isNotEqualTo(game.getDateCreated())
                .isEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(game.getDateModified())
                .isEqualTo(now);
    }

    @SuppressWarnings("JUnitMalformedDeclaration")
    @Test
    void saveShouldUpdateDatesGivenExisting(EntityAuditControl entityAuditControl) {
        persistSampleData();
        entityAuditControl.enable();
        Game game = SampleGames.GAME_CREATED_YESTERDAY.get();
        game.setTitle(new GameTitle("Changed Title"));

        repository.save(game);
        entityManager.flush();

        LocalDateTime now = LocalDateTime.now(clock);
        Game persistedAggregate = gameJpaAdapter.getPersistedDomainObject(game);
        assertThat(persistedAggregate.getDateCreated())
                .isEqualTo(game.getDateCreated())
                .isNotEqualTo(now);
        assertThat(persistedAggregate.getDateModified())
                .isNotEqualTo(game.getDateModified())
                .isEqualTo(now);
    }

    @Test
    void findByIdShouldReturnAggregateGivenItExists() {
        persistSampleData();
        Game expectedGame = SampleGames.GAME_CREATED_TODAY.get();

        Optional<Game> result = repository.findById(expectedGame.getId());

        assertThat(result)
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedGame);
    }

    @Test
    void shouldGetById() {
        persistSampleData();
        Game expectedResult = SampleGames.GAME_CREATED_TODAY.get();

        Game result = repository.getById(expectedResult.getId());

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        GameId nonexistentId = new GameId("9e9d9a87-919c-4475-a79d-1d897e1b0f53");

        assertThatThrownBy(() -> repository.getById(nonexistentId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldFindByTitle() {
        persistSampleData();
        var expectedResult = SampleGames.GAME_CREATED_TODAY.get();

        Optional<Game> result = repository.findByTitle(expectedResult.getTitle());

        assertThat(result)
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }

    @Test
    void findAllShouldReturnValidContent() {
        persistSampleData();
        Pagination pagination = everythingOnOnePage();
        List<Game> expectedContent = List.of(SampleGames.GAME_CREATED_TODAY.get(), SampleGames.GAME_CREATED_YESTERDAY.get());

        Page<Game> result = repository.findAll(pagination);

        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedContent);
    }

    private Pagination everythingOnOnePage() {
        return new Pagination(0, 999);
    }

    @Test
    void findAllShouldProperlyPaginate() {
        persistSampleData();
        Pagination pagination = new Pagination(0, 1);

        Page<Game> result = repository.findAll(pagination);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalPages()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.pagination()).isEqualTo(pagination);
    }

    @Test
    void findAllShouldSortByDateCreatedAsc() {
        persistSampleData();
        Pagination pageable = new Pagination(0, 5);
        List<Game> expectedContent = List.of(
                SampleGames.GAME_CREATED_YESTERDAY.get(),
                SampleGames.GAME_CREATED_TODAY.get()
        );

        Page<Game> result = repository.findAll(pageable);

        assertThat(result.content())
                .containsExactlyElementsOf(expectedContent);
    }

    @Test
    void findAllByIdInShouldReturnAllDataForAggregate() {
        persistSampleData();
        Game game = SampleGames.GAME_CREATED_TODAY.get();

        List<Game> result = repository.findAllByIdIn(List.of(game.getId()));

        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(game);
    }

    private static class Time {

        public static final LocalDateTime NOW = FakeTimeBeanConfig.DEFAULT_NOW;
        public static final LocalDate TODAY = NOW.toLocalDate();
        public static final LocalDate YESTERDAY = TODAY.minusDays(1);
    }

    private static class SampleGames {

        public static final Supplier<Game> GAME_CREATED_TODAY = () -> TestGame.anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle(new GameTitle("Test Game 1"))
                .withDateCreated(Time.TODAY.atStartOfDay())
                .build();

        public static final Supplier<Game> GAME_CREATED_YESTERDAY = () -> TestGame.anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle(new GameTitle("Test Game 2"))
                .withDateCreated(Time.YESTERDAY.atStartOfDay())
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_CREATED_TODAY.get(), GAME_CREATED_YESTERDAY.get());
        }
    }
}