package dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsId;
import dev.codesoapbox.backity.core.filedetails.domain.exceptions.FileDetailsNotFoundException;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.shared.domain.Page;
import dev.codesoapbox.backity.core.shared.domain.Pagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.codesoapbox.backity.core.filedetails.domain.TestFileDetails.*;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
abstract class FileDetailsJpaRepositoryAbstractIT {

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_1 = () -> enqueuedFileDetails()
            .id(new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_2 = () -> successfulFileDetails()
            .id(new FileDetailsId(UUID.fromString("a6adc122-df20-4e2c-a975-7d4af7104704")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_3 = () -> enqueuedFileDetails()
            .id(new FileDetailsId(UUID.fromString("0d4d181c-9a77-4146-bbd6-40f7d4453b5f")))
            .gameId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .build();

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_4 = () -> failedFileDetails()
            .id(new FileDetailsId(UUID.fromString("568afe65-018b-40fc-a8b4-481ded571ff8")))
            .gameId(new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5")))
            .build();

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_5 = () -> inProgressFileDetails()
            .id(new FileDetailsId(UUID.fromString("0a2a4b8d-f02e-4e3e-a3da-f47e1ea6aa30")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    private static final Supplier<FileDetails> GAME_FILE_DETAILS_6 = () -> discoveredFileDetails()
            .id(new FileDetailsId(UUID.fromString("3d65af79-a558-4f23-88bd-3c04e977e63f")))
            .gameId(new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")))
            .build();

    @Autowired
    protected FileDetailsJpaRepository fileDetailsJpaRepository;

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected FileDetailsJpaEntityMapper entityMapper;

    @BeforeEach
    void setUp() {
        persistTestData();
    }

    private void persistTestData() {
        List<FileDetailsJpaEntity> fileDetailsEntities = Stream.of(
                        GAME_FILE_DETAILS_1.get(), GAME_FILE_DETAILS_2.get(), GAME_FILE_DETAILS_3.get(),
                        GAME_FILE_DETAILS_4.get(), GAME_FILE_DETAILS_5.get(), GAME_FILE_DETAILS_6.get())
                .map(entityMapper::toEntity)
                .toList();
        persistAssociatedGames(fileDetailsEntities);
        fileDetailsEntities.forEach(entityManager::persist);
        entityManager.flush();
        entityManager.clear();
    }

    private void persistAssociatedGames(List<FileDetailsJpaEntity> fileDetailsEntities) {
        fileDetailsEntities.stream()
                .map(FileDetailsJpaEntity::getGame)
                .distinct()
                .forEach(entityManager::persist);
        entityManager.flush();
    }

    @Test
    void shouldFindOldestWaitingForDownload() {
        Optional<FileDetails> result = fileDetailsJpaRepository.findOldestWaitingForDownload();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(GAME_FILE_DETAILS_1.get());
    }

    @Test
    void shouldFindAllWaitingForDownload() {
        var pagination = new Pagination(0, 2);
        Page<FileDetails> result = fileDetailsJpaRepository.findAllWaitingForDownload(pagination);

        Page<FileDetails> expectedResult = new Page<>(
                List.of(GAME_FILE_DETAILS_1.get(), GAME_FILE_DETAILS_3.get()),
                2, 1, 2, 2, 0);
        assertThat(result).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expectedResult);
        assertThat(result.content())
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult.content());
    }

    @Test
    void shouldSave() {
        FileDetails newFileDetails = fullFileDetails().build();

        FileDetails result = fileDetailsJpaRepository.save(newFileDetails);
        entityManager.flush();

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newFileDetails);

        FileDetailsJpaEntity persistedEntity = entityManager.find(FileDetailsJpaEntity.class,
                newFileDetails.getId().value());
        assertThat(persistedEntity)
                .extracting(entity -> entityMapper.toModel(entity))
                .hasNoNullFieldsOrProperties()
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(newFileDetails);
        assertThat(persistedEntity.getDateCreated()).isNotNull();
        assertThat(persistedEntity.getDateModified()).isNotNull();
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<FileDetails> result = fileDetailsJpaRepository.findCurrentlyDownloading();

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(GAME_FILE_DETAILS_5.get());
    }

    @Test
    void shouldFindAllProcessed() {
        var pagination = new Pagination(0, 2);
        Page<FileDetails> result = fileDetailsJpaRepository.findAllProcessed(pagination);

        List<FileDetails> expectedResult = List.of(
                GAME_FILE_DETAILS_2.get(),
                GAME_FILE_DETAILS_4.get()
        );
        assertThat(result.content())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @ParameterizedTest(name = "should return {1} for version={0}")
    @CsvSource(value = {"someVersion,true", "fakeVersion,false"})
    void existsByUrlAndVersion(String version, boolean shouldFind) {
        boolean exists = fileDetailsJpaRepository.existsByUrlAndVersion("someUrl", version);

        assertThat(exists).isEqualTo(shouldFind);
    }

    @Test
    void shouldFindById() {
        FileDetailsId id = new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));

        Optional<FileDetails> result = fileDetailsJpaRepository.findById(id);

        assertThat(result).get().usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(GAME_FILE_DETAILS_1.get());
    }

    @Test
    void shouldGetById() {
        var id = new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));

        FileDetails result = fileDetailsJpaRepository.getById(id);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(GAME_FILE_DETAILS_1.get());
    }

    @Test
    void getByIdShouldThrowGivenNotFound() {
        var nonexistentId = new FileDetailsId(UUID.fromString("59e37c43-dda7-4c5f-87a3-c380ebb5f8ea"));

        assertThatThrownBy(() -> fileDetailsJpaRepository.getById(nonexistentId))
                .isInstanceOf(FileDetailsNotFoundException.class);
    }

    @Test
    void shouldFindAllDiscovered() {
        var pagination = new Pagination(0, 2);
        Page<FileDetails> result = fileDetailsJpaRepository.findAllDiscovered(pagination);

        List<FileDetails> expectedResult = singletonList(GAME_FILE_DETAILS_6.get());
        assertThat(result.content())
                .usingRecursiveComparison()
                .ignoringFields("dateCreated", "dateModified")
                .isEqualTo(expectedResult);
    }

    @Test
    void shouldFindByGameId() {
        var id = new GameId(UUID.fromString("5bdd248a-c3aa-487a-8479-0bfdb32f7ae5"));
        List<FileDetails> result = fileDetailsJpaRepository.findAllByGameId(id);

        assertThat(result).hasSize(2);
    }
}