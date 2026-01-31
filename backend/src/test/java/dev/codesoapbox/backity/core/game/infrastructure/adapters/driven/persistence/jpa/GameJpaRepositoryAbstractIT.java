package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.core.game.domain.exceptions.GameNotFoundException;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
abstract class GameJpaRepositoryAbstractIT {

    @Autowired
    private GameJpaRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameJpaEntityMapper entityMapper;

    @Test
    void saveShouldPersistNew() {
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle("New Title")
                .build();

        Game result = repository.save(game);
        entityManager.flush();

        assertSame(result, game);
        assertWasPersisted(game);
    }

    @Test
    void shouldNotSaveWhenGameWithTitleAlreadyExists() {
        populateDatabase(List.of(GAMES.GAME_1.get()));
        String existingTitle = GAMES.GAME_1.get().getTitle();
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle(existingTitle)
                .build();

        repository.save(game);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    private void populateDatabase(List<Game> games) {
        for (Game game : games) {
            entityManager.persist(entityMapper.toEntity(game));
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void saveShouldModifyExisting() {
        populateDatabase(List.of(GAMES.GAME_1.get()));
        Game game = GAMES.GAME_1.get();
        game.setTitle("New Title");

        Game result = repository.save(game);
        entityManager.flush();

        assertSame(result, game);
        assertWasPersisted(game);
    }

    private void assertWasPersisted(Game game) {
        GameJpaEntity persistedEntity = entityManager.find(GameJpaEntity.class, game.getId().value());
        Game persistedGame = entityMapper.toDomain(persistedEntity);
        assertSame(persistedGame, game);
    }

    @Test
    void shouldFindById() {
        populateDatabase(GAMES.getAll());
        Game game = GAMES.GAME_1.get();

        Optional<Game> maybeFoundGame = repository.findById(game.getId());

        assertThat(maybeFoundGame).isPresent();
        assertSame(maybeFoundGame.get(), game);
    }

    private void assertSame(Game actual, Game expected) {
        assertThat(actual)
                .satisfies(it -> {
                    assertThat(it.getDateCreated()).isNotNull();
                    assertThat(it.getDateModified()).isNotNull();
                })
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expected);
    }

    @Test
    void shouldGetById() {
        populateDatabase(GAMES.getAll());
        Game game = GAMES.GAME_1.get();

        Game maybeFoundGame = repository.getById(game.getId());

        assertSame(maybeFoundGame, game);
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        GameId nonexistentId = new GameId("9e9d9a87-919c-4475-a79d-1d897e1b0f53");

        assertThatThrownBy(() -> repository.getById(nonexistentId))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldFindByTitle() {
        populateDatabase(GAMES.getAll());
        var game = GAMES.GAME_1.get();

        Optional<Game> maybeFoundGame = repository.findByTitle(game.getTitle());

        assertThat(maybeFoundGame).isPresent();
        assertSame(maybeFoundGame.get(), game);
    }

    @Test
    void shouldFindAllPaginated() {
        populateDatabase(GAMES.getAll());
        Pagination pageable = new Pagination(0, 5);
        Page<Game> result = repository.findAll(pageable);

        Page<Game> expectedResult = new Page<>(List.of(GAMES.GAME_1.get(), GAMES.GAME_2.get()),
                1, 2,
                new Pagination(0, 5));
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .containsExactlyInAnyOrderElementsOf(expectedResult.content());
    }

    @Test
    void shouldFindAllById() {
        populateDatabase(GAMES.getAll());
        Game game = GAMES.GAME_1.get();

        List<Game> result = repository.findAllByIdIn(List.of(game.getId()));

        List<Game> expectedResult = List.of(game);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    private static class GAMES {

        public static final Supplier<Game> GAME_1 = () -> TestGame.anyBuilder()
                .withId(new GameId("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"))
                .withTitle("Test Game 1")
                .build();

        public static final Supplier<Game> GAME_2 = () -> TestGame.anyBuilder()
                .withId(new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281"))
                .withTitle("Test Game 2")
                .build();

        public static List<Game> getAll() {
            return List.of(GAME_1.get(), GAME_2.get());
        }
    }
}