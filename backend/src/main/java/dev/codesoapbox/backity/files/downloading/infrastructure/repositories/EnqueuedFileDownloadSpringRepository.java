package dev.codesoapbox.backity.files.downloading.infrastructure.repositories;

import dev.codesoapbox.backity.files.downloading.domain.model.EnqueuedFileDownload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnqueuedFileDownloadSpringRepository extends JpaRepository<EnqueuedFileDownload, Long> {

    Optional<EnqueuedFileDownload> findFirstByDownloadedFalseAndFailedFalseOrderByDateCreatedAsc();

    Page<EnqueuedFileDownload> findAllByOrderByDateCreatedAsc(Pageable pageable);
}
