package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class FileCopyJpaEntityMapperTest {

    private static final FileCopyJpaEntityMapper MAPPER = Mappers.getMapper(FileCopyJpaEntityMapper.class);

    @Test
    void shouldMapTrackedDomainToJpa() {
        FileCopy domain = domainTracked();

        FileCopyJpaEntity result = MAPPER.toEntity(domain);

        FileCopyJpaEntity expectedResult = jpaTracked();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private FileCopy domainTracked() {
        return TestFileCopy.tracked();
    }

    private FileCopyJpaEntity jpaTracked() {
        return jpa(FileCopyStatus.TRACKED, null, null);
    }

    private FileCopyJpaEntity jpa(FileCopyStatus tracked, String failedReason, String filePath) {
        return new FileCopyJpaEntity(
                UUID.fromString("6df888e8-90b9-4df5-a237-0cba422c0310"),
                new FileCopyNaturalIdentifierJpaEmbeddable(
                        UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"),
                        UUID.fromString("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
                ),
                tracked,
                failedReason,
                filePath,
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void shouldMapTrackedJpaToDomain() {
        FileCopyJpaEntity model = jpaTracked();

        FileCopy result = MAPPER.toDomain(model);

        FileCopy expectedResult = domainTracked();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMapStoredUnverifiedDomainToJpa() {
        FileCopy domain = domainStoredUnverified();

        FileCopyJpaEntity result = MAPPER.toEntity(domain);

        FileCopyJpaEntity expectedResult = jpaStoredUnverified();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private FileCopy domainStoredUnverified() {
        return TestFileCopy.storedIntegrityUnknown();
    }

    private FileCopyJpaEntity jpaStoredUnverified() {
        return jpa(FileCopyStatus.STORED_INTEGRITY_UNKNOWN, null, "someFilePath");
    }

    @Test
    void shouldMapStoredUnverifiedJpaToDomain() {
        FileCopyJpaEntity model = jpaStoredUnverified();

        FileCopy result = MAPPER.toDomain(model);

        FileCopy expectedResult = domainStoredUnverified();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldMapFailedDomainToJpa() {
        FileCopy domain = domainFailed();

        FileCopyJpaEntity result = MAPPER.toEntity(domain);

        FileCopyJpaEntity expectedResult = jpaFailed();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }

    private FileCopy domainFailed() {
        return TestFileCopy.failed();
    }

    private FileCopyJpaEntity jpaFailed() {
        return jpa(FileCopyStatus.FAILED, "someFailedReason", null);
    }

    @Test
    void shouldMapFailedJpaToDomain() {
        FileCopyJpaEntity model = jpaFailed();

        FileCopy result = MAPPER.toDomain(model);

        FileCopy expectedResult = domainFailed();
        assertThat(result)
                .usingRecursiveComparison().isEqualTo(expectedResult);
    }
}