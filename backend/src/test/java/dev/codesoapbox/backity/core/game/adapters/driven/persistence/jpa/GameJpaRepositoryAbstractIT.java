package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.TestGame;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
abstract class GameJpaRepositoryAbstractIT {

    @Autowired
    private GameJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameJpaEntityMapper entityMapper;

    @Test
    void shouldSaveNew() {
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle("New Title")
                .build();

        jpaRepository.save(game);
        entityManager.flush();

        GameJpaEntity persistedEntity = entityManager.find(GameJpaEntity.class, game.getId().value());
        assertThat(entityMapper.toDomain(persistedEntity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldNotSaveWhenGameWithTitleAlreadyExists() {
        populateDatabase(List.of(GAMES.GAME_1.get()));
        String existingTitle = GAMES.GAME_1.get().getTitle();
        Game game = TestGame.anyBuilder()
                .withId(GameId.newInstance())
                .withTitle(existingTitle)
                .build();

        jpaRepository.save(game);

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
    void shouldModify() {
        populateDatabase(List.of(GAMES.GAME_1.get()));
        Game game = GAMES.GAME_1.get();
        game.setTitle("New Title");

        jpaRepository.save(game);
        entityManager.flush();

        GameJpaEntity persistedEntity = entityManager.find(GameJpaEntity.class, game.getId().value());
        assertThat(persistedEntity).usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(entityMapper.toEntity(game));
    }

    @Test
    void shouldFindById() {
        populateDatabase(GAMES.getAll());
        Game game = GAMES.GAME_1.get();

        Optional<Game> maybeFoundGame = jpaRepository.findById(game.getId());

        assertThat(maybeFoundGame).get().usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldFindByTitle() {
        populateDatabase(GAMES.getAll());
        var game = GAMES.GAME_1.get();

        Optional<Game> maybeFoundGame = jpaRepository.findByTitle(game.getTitle());

        assertThat(maybeFoundGame).get().usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldFindAllPaginated() {
        populateDatabase(GAMES.getAll());
        Pagination pageable = new Pagination(0, 5);
        Page<Game> result = jpaRepository.findAll(pageable);

        Page<Game> expectedResult = new Page<>(List.of(GAMES.GAME_1.get(), GAMES.GAME_2.get()),
                5, 1, 2,
                5, 0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .containsExactlyInAnyOrderElementsOf(expectedResult.content());
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