package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.codesoapbox.backity.core.game.domain.TestGame.aGame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
abstract class GameJpaRepositoryAbstractIT {

    private static final Supplier<Game> GAME_1 = () -> aGame()
            .withId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .withTitle("Test Game 1")
            .build();

    private static final Supplier<Game> GAME_2 = () -> aGame()
            .withId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .withTitle("Test Game 2")
            .build();

    private static final Supplier<Game> GAME_3 = () -> aGame()
            .withId(new GameId(UUID.fromString("a811e5ad-f964-47de-a3fe-73f276918970")))
            .withTitle("Test Game 3")
            .build();

    @Autowired
    private GameJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameJpaEntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        persistTestData();
    }

    private void persistTestData() {
        Stream.of(GAME_1.get(), GAME_2.get(), GAME_3.get())
                .map(entityMapper::toEntity)
                .forEach(entityManager::persistAndFlush);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldSave() {
        Game game = aGame()
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
        String existingTitle = GAME_1.get().getTitle();
        Game game = aGame()
                .withId(GameId.newInstance())
                .withTitle(existingTitle)
                .build();

        jpaRepository.save(game);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldModify() {
        Game game = GAME_1.get();
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
        Game game = GAME_1.get();

        Optional<Game> foundGameOpt = jpaRepository.findById(game.getId());

        assertThat(foundGameOpt).get().usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldFindByTitle() {
        var game = GAME_1.get();

        Optional<Game> foundGameOpt = jpaRepository.findByTitle(game.getTitle());

        assertThat(foundGameOpt).get().usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldFindAllPaginated() {
        Pagination pageable = new Pagination(0, 2);
        Page<Game> result = jpaRepository.findAll(pageable);

        Page<Game> expectedResult = new Page<>(List.of(GAME_1.get(), GAME_2.get()), 2, 2, 3,
                2, 0);
        assertThat(result).usingRecursiveComparison()
                .isEqualTo(expectedResult);
    }
}