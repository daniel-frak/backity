package dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFileId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

abstract class DiscoveredFileJpaRepositoryAbstractTest {

    private DiscoveredFileJpaRepository discoveredFileJpaRepository;

    @Autowired
    private DiscoveredFileSpringRepository discoveredFileSpringRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        discoveredFileJpaRepository = new DiscoveredFileJpaRepository(discoveredFileSpringRepository);
        entityManager = entityManagerFactory.createEntityManager();
        cleanDatabase();
        persistTestDiscoveredFiles();
    }

    private void cleanDatabase() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM discovered_file;").executeUpdate();
        entityManager.getTransaction().commit();
    }

    private void persistTestDiscoveredFiles() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("""
                        INSERT INTO discovered_file
                        (date_created, date_modified, game_title, name, size, source, unique_id,
                         url, version, enqueued, ignored)
                        VALUES
                        ('2022-04-29T14:15:53', '2022-04-29T15:15:53', 'someGameTitle1',
                        'someName1', 'someSize1', 'someSource1', 'e3e4a77b-5ddf-479a-bd49-37ec2656ad4b', 
                        'someUrl1', 'someVersion1', true, false);
                        
                        INSERT INTO discovered_file
                        (date_created, date_modified, game_title, name, size, source, unique_id,
                         url, version, enqueued, ignored)
                        VALUES
                        ('2022-05-29T14:15:53', '2022-05-29T15:15:53', 'someGameTitle2',
                        'someName2', 'someSize2', 'someSource2', '1126057b-e0c5-4cd4-85e3-8e2719c737d3', 
                        'someUrl2', 'someVersion2', false, false);
                """).executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    @Test
    void shouldFindByUniqueId() {
        var expectedResult = new DiscoveredFile(
                new DiscoveredFileId("someUrl1", "someVersion1"),
                UUID.fromString("e3e4a77b-5ddf-479a-bd49-37ec2656ad4b"), "someSource1", "someName1",
                "someGameTitle1", "someSize1", LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2022-04-29T15:15:53"), true, false);

        var result = discoveredFileJpaRepository.findByUniqueId(
                UUID.fromString("e3e4a77b-5ddf-479a-bd49-37ec2656ad4b"));

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test
    void shouldFindAllUnqueued() {
        var expectedElement = new DiscoveredFile(
                new DiscoveredFileId("someUrl2", "someVersion2"),
                UUID.fromString("1126057b-e0c5-4cd4-85e3-8e2719c737d3"), "someSource2", "someName2",
                "someGameTitle2", "someSize2", LocalDateTime.parse("2022-05-29T14:15:53"),
                LocalDateTime.parse("2022-05-29T15:15:53"), false, false);

        var result = discoveredFileJpaRepository.findAllUnqueued(Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expectedElement, result.getContent().get(0));
    }

    @Test
    void shouldSave() {
        var discoveredFile = new DiscoveredFile(
                new DiscoveredFileId("someUrlNew", "someVersionNew"),
                UUID.fromString("e20cd77b-bdbf-41e0-8f9b-255b278b4ed5"), "someSourceNew", "someNameNew",
                "someGameTitleNew", "someSizeNew", null, null,
                true, false);

        DiscoveredFile savedFile = discoveredFileJpaRepository.save(discoveredFile);

        BigInteger recordCount = (BigInteger) entityManager.createNativeQuery(
                        "SELECT COUNT(*) FROM discovered_file f" +
                                " WHERE f.unique_id = '1126057b-e0c5-4cd4-85e3-8e2719c737d3';")
                .getSingleResult();
        assertEquals(BigInteger.ONE, recordCount);
        assertNotNull(savedFile.getDateCreated());
        assertNotNull(savedFile.getDateModified());
    }

    @Test
    void existsByIdShouldReturnCorrectValue() {
        assertTrue(discoveredFileJpaRepository.existsById(new DiscoveredFileId("someUrl1", "someVersion1")));
        assertFalse(discoveredFileJpaRepository.existsById(
                new DiscoveredFileId("nonexistentUrl", "nonexistentVersion")));
    }
}