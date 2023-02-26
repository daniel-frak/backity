package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class GameFileVersionJpaRepository implements GameFileVersionRepository {

    private final GameFileVersionSpringRepository springRepository;

    @Override
    public Optional<GameFileVersion> findOldestWaitingForDownload() {
        return springRepository.findAllWaitingForDownload(PageRequest.of(0, 1)).get()
                .findFirst();
    }

    @Override
    public Page<GameFileVersion> findAllWaitingForDownload(Pageable pageable) {
        return springRepository.findAllWaitingForDownload(pageable);
    }

    @Override
    public GameFileVersion save(GameFileVersion gameFileVersion) {
        return springRepository.save(gameFileVersion);
    }

    @Override
    public Optional<GameFileVersion> findCurrentlyDownloading() {
        return springRepository.findByStatus(FileStatus.DOWNLOAD_IN_PROGRESS);
    }

    @Override
    public Page<GameFileVersion> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByUrlAndVersion(url, version);
    }

    @Override
    public Optional<GameFileVersion> findById(Long id) {
        return springRepository.findById(id);
    }

    @Override
    public Page<GameFileVersion> findAllDiscovered(Pageable pageable) {
        return springRepository.findAllByStatus(pageable, FileStatus.DISCOVERED);
    }
}
