package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import dev.codesoapbox.backity.core.files.domain.game.Game;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

abstract class GameJpaRepositoryAbstractIT {

    private static final GameJpaEntityMapper MAPPER = Mappers.getMapper(GameJpaEntityMapper.class);

    @Autowired
    private GameJpaEntitySpringRepository springRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private GameJpaRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = new GameJpaRepository(springRepository, MAPPER);
        entityManager = entityManagerFactory.createEntityManager();
    }

    @AfterEach
    void tearDown() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM GAME;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    void shouldSaveAndFindById() {
        var game = Game.createNew("someTitle");

        jpaRepository.save(game);

        Optional<Game> foundGameOpt = jpaRepository.findById(game.getId());

        assertThat(foundGameOpt)
                .hasValueSatisfying(foundGame -> assertThat(foundGame).usingRecursiveComparison().isEqualTo(game));
    }

    @Test
    void shouldSaveAndModifyAndFindById() {
        var game = Game.createNew("someTitle");

        jpaRepository.save(game);
        game.setTitle("someOtherTitle");
        jpaRepository.save(game);

        Optional<Game> foundGameOpt = jpaRepository.findById(game.getId());
        Long recordCount = countGamesInDatabase();

        assertThat(foundGameOpt)
                .hasValueSatisfying(foundGame -> assertThat(foundGame).usingRecursiveComparison().isEqualTo(game));
        assertThat(recordCount).isOne();
    }

    private Long countGamesInDatabase() {
        return (Long) entityManager.createNativeQuery("SELECT COUNT(*) FROM GAME g;")
                .getSingleResult();
    }

    @Test
    void shouldSaveAndFindByTitle() {
        var game = Game.createNew("someTitle");

        jpaRepository.save(game);

        Optional<Game> foundGameOpt = jpaRepository.findByTitle(game.getTitle());

        assertThat(foundGameOpt)
                .hasValueSatisfying(foundGame -> assertThat(foundGame).usingRecursiveComparison().isEqualTo(game));
    }

    @Test
    void shouldSaveAndFindAllPaginated() {
        Game game1 = Game.createNew("someTitle1");
        Game game2 = Game.createNew("someTitle2");
        Game game3 = Game.createNew("someTitle3");
        jpaRepository.save(game1);
        jpaRepository.save(game2);
        jpaRepository.save(game3);

        Page<Game> result = jpaRepository.findAll(PageRequest.of(0, 2));

        assertThat(result.getContent())
                .usingRecursiveComparison().isEqualTo(List.of(game1, game2));
        assertAll("Page information",
                () -> assertThat(result.getTotalElements()).isEqualTo(3),
                () -> assertThat(result.getTotalPages()).isEqualTo(2),
                () -> assertThat(result.getSize()).isEqualTo(2),
                () -> assertThat(result.getNumber()).isZero(),
                () -> assertThat(result.getNumberOfElements()).isEqualTo(2));
    }
}