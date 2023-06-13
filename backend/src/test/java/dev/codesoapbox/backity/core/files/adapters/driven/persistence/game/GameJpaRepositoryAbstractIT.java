package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.config.game.GameJpaRepositoryBeanConfig;
import dev.codesoapbox.backity.core.files.domain.game.Game;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import dev.codesoapbox.backity.core.files.domain.game.TestGame;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(GameJpaRepositoryBeanConfig.class)
@Transactional
abstract class GameJpaRepositoryAbstractIT {

    @Autowired
    private GameJpaRepository jpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameJpaEntityMapper entityMapper;

    private static final Supplier<Game> GAME_1 = () -> TestGame.aGame()
            .withId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .withTitle("Test Game 1")
            .build();

    private static final Supplier<Game> GAME_2 = () -> TestGame.aGame()
            .withId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .withTitle("Test Game 2")
            .build();

    private static final Supplier<Game> GAME_3 = () -> TestGame.aGame()
            .withId(new GameId(UUID.fromString("a811e5ad-f964-47de-a3fe-73f276918970")))
            .withTitle("Test Game 3")
            .build();

    @BeforeEach
    void setUp() {
        persistTestData();
    }

    private void persistTestData() {
        Stream.of(GAME_1.get(), GAME_2.get(), GAME_3.get())
                .map(entityMapper::toEntity)
                .forEach(entityManager::persistAndFlush);
    }

    @Test
    void shouldSave() {
        var game = Game.createNew("New Title");

        jpaRepository.save(game);

        GameJpaEntity persistedGame = entityManager.find(GameJpaEntity.class, game.getId().value());
        assertThat(entityMapper.toDomain(persistedGame)).usingRecursiveComparison()
                .isEqualTo(game);
    }

    @Test
    void shouldNotSaveWhenGameWithTitleAlreadyExists() {
        String existingTitle = GAME_1.get().getTitle();
        var game = Game.createNew(existingTitle);

        jpaRepository.save(game);

        assertThatThrownBy(() -> entityManager.flush())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldModify() {
        var game = GAME_1.get();
        game.setTitle("New Title");

        jpaRepository.save(game);

        GameJpaEntity persistedGame = entityManager.find(GameJpaEntity.class, game.getId().value());
        assertThat(persistedGame).usingRecursiveComparison()
                .isEqualTo(entityMapper.toEntity(game));
    }

    @Test
    void shouldFindById() {
        Game game = GAME_1.get();

        Optional<Game> foundGameOpt = jpaRepository.findById(game.getId());

        assertThat(foundGameOpt)
                .hasValueSatisfying(foundGame -> assertThat(foundGame).usingRecursiveComparison().isEqualTo(game));
    }

    @Test
    void shouldFindByTitle() {
        var game = GAME_1.get();

        Optional<Game> foundGameOpt = jpaRepository.findByTitle(game.getTitle());

        assertThat(foundGameOpt)
                .hasValueSatisfying(foundGame -> assertThat(foundGame).usingRecursiveComparison().isEqualTo(game));
    }

    @Test
    void shouldFindAllPaginated() {
        Page<Game> result = jpaRepository.findAll(PageRequest.of(0, 2));

        assertThat(result.getContent())
                .usingRecursiveComparison().isEqualTo(List.of(GAME_1.get(), GAME_2.get()));
        assertAll("Page information",
                () -> assertThat(result.getTotalElements()).isEqualTo(3),
                () -> assertThat(result.getTotalPages()).isEqualTo(2),
                () -> assertThat(result.getSize()).isEqualTo(2),
                () -> assertThat(result.getNumber()).isZero(),
                () -> assertThat(result.getNumberOfElements()).isEqualTo(2));
    }
}