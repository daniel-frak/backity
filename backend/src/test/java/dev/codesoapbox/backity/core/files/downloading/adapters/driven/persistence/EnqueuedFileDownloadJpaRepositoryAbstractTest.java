package dev.codesoapbox.backity.core.files.downloading.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.downloading.domain.model.DownloadStatus;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

abstract class EnqueuedFileDownloadJpaRepositoryAbstractTest {

    @Autowired
    private EnqueuedFileDownloadJpaRepository enqueuedFileDownloadJpaRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        cleanDatabase();
        persistTestEnqueuedFiles();
    }

    private void cleanDatabase() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM enqueued_file_download;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestEnqueuedFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("""                         
                        INSERT INTO enqueued_file_download
                        (id, source, url, name, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_enqueued_file'), 'someSource1', 'someUrl1', 'someName1', 'someGameTitle1', 
                        'someVersion1', 'someSize1', '2022-04-29T14:15:53', 'WAITING', 'someFailedReason1');
                        
                        INSERT INTO enqueued_file_download
                        (id, source, url, name, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_enqueued_file'), 'someSource2', 'someUrl2', 'someName2', 'someGameTitle2', 
                        'someVersion2', 'someSize2', '2022-05-29T14:15:53', 'DOWNLOADED', 'someFailedReason2');
                        
                        INSERT INTO enqueued_file_download
                        (id, source, url, name, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_enqueued_file'), 'someSource3', 'someUrl3', 'someName3', 'someGameTitle3',
                         'someVersion3', 'someSize3', '2022-06-29T14:15:53', 'WAITING', 'someFailedReason3');
                        
                        INSERT INTO enqueued_file_download
                        (id, source, url, name, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_enqueued_file'), 'someSource4', 'someUrl4', 'someName4', 'someGameTitle4',
                         'someVersion4', 'someSize4', '2022-07-29T14:15:53', 'FAILED', 'someFailedReason4');
                        
                        INSERT INTO enqueued_file_download
                        (id, source, url, name, game_title, version, size, date_created, status, failed_reason) VALUES
                        (NEXTVAL('seq_enqueued_file'), 'someSource5', 'someUrl5', 'someName5', 'someGameTitle5', 
                        'someVersion5', 'someSize5', '2022-08-29T14:15:53', 'IN_PROGRESS', 'someFailedReason5');
                """).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void shouldFindOldestUnprocessed() {
        Optional<EnqueuedFileDownload> result = enqueuedFileDownloadJpaRepository.findOldestUnprocessed();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        var expectedResult = EnqueuedFileDownload.builder()
                .id(result.get().getId())
                .source("someSource1")
                .url("someUrl1")
                .name("someName1")
                .gameTitle("someGameTitle1")
                .version("someVersion1")
                .size("someSize1")
                .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                .status(DownloadStatus.WAITING)
                .failedReason("someFailedReason1")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllUnprocessed() {
        Page<EnqueuedFileDownload> result = enqueuedFileDownloadJpaRepository.findAllUnprocessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                EnqueuedFileDownload.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource1")
                        .url("someUrl1")
                        .name("someName1")
                        .gameTitle("someGameTitle1")
                        .version("someVersion1")
                        .size("someSize1")
                        .dateCreated(LocalDateTime.parse("2022-04-29T14:15:53"))
                        .status(DownloadStatus.WAITING)
                        .failedReason("someFailedReason1")
                        .build(),
                EnqueuedFileDownload.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource3")
                        .url("someUrl3")
                        .name("someName3")
                        .gameTitle("someGameTitle3")
                        .version("someVersion3")
                        .size("someSize3")
                        .dateCreated(LocalDateTime.parse("2022-06-29T14:15:53"))
                        .status(DownloadStatus.WAITING)
                        .failedReason("someFailedReason3")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }

    @Test
    void shouldSave() {
        var newEnqueuedFileDownload =
                EnqueuedFileDownload.builder()
                        .source("someSourceNew")
                        .url("someUrlNew")
                        .name("someNameNew")
                        .gameTitle("someGameTitleNew")
                        .version("someVersionNew")
                        .size("someSizeNew")
                        .status(DownloadStatus.WAITING)
                        .failedReason("someFailedReasonNew")
                        .build();

        var result = enqueuedFileDownloadJpaRepository.save(newEnqueuedFileDownload);

        assertNotNull(result.getId());
        assertNotNull(result.getDateCreated());
        BigInteger recordCount = (BigInteger) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM enqueued_file_download f" +
                                " WHERE f.id = '" + result.getId() + "';")
                .getSingleResult();
        assertEquals(BigInteger.ONE, recordCount);
    }

    @Test
    void shouldFindCurrentlyDownloading() {
        Optional<EnqueuedFileDownload> result = enqueuedFileDownloadJpaRepository.findCurrentlyDownloading();

        assertTrue(result.isPresent());
        assertNotNull(result.get().getId());
        var expectedResult = EnqueuedFileDownload.builder()
                .id(result.get().getId())
                .source("someSource5")
                .url("someUrl5")
                .name("someName5")
                .gameTitle("someGameTitle5")
                .version("someVersion5")
                .size("someSize5")
                .dateCreated(LocalDateTime.parse("2022-08-29T14:15:53"))
                .status(DownloadStatus.IN_PROGRESS)
                .failedReason("someFailedReason5")
                .build();
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllProcessed() {
        Page<EnqueuedFileDownload> result = enqueuedFileDownloadJpaRepository.findAllProcessed(Pageable.unpaged());

        assertNotNull(result.getContent().get(0).getId());
        var expectedResult = List.of(
                EnqueuedFileDownload.builder()
                        .id(result.getContent().get(0).getId())
                        .source("someSource2")
                        .url("someUrl2")
                        .name("someName2")
                        .gameTitle("someGameTitle2")
                        .version("someVersion2")
                        .size("someSize2")
                        .dateCreated(LocalDateTime.parse("2022-05-29T14:15:53"))
                        .status(DownloadStatus.DOWNLOADED)
                        .failedReason("someFailedReason2")
                        .build(),
                EnqueuedFileDownload.builder()
                        .id(result.getContent().get(1).getId())
                        .source("someSource4")
                        .url("someUrl4")
                        .name("someName4")
                        .gameTitle("someGameTitle4")
                        .version("someVersion4")
                        .size("someSize4")
                        .dateCreated(LocalDateTime.parse("2022-07-29T14:15:53"))
                        .status(DownloadStatus.FAILED)
                        .failedReason("someFailedReason4")
                        .build()
        );
        assertEquals(expectedResult, result.getContent());
    }
}