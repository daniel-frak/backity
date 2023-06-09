package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RequiredArgsConstructor
public class GameFileVersionJpaBackupRepository implements GameFileVersionBackupRepository {

    private final GameFileVersionSpringRepository springRepository;

    @Override
    public Optional<GameFileVersionBackup> findOldestWaitingForDownload() {
        return springRepository.findAllWaitingForDownload(PageRequest.of(0, 1)).get()
                .findFirst();
    }

    @Override
    public Page<GameFileVersionBackup> findAllWaitingForDownload(Pageable pageable) {
        return springRepository.findAllWaitingForDownload(pageable);
    }

    @Override
    public GameFileVersionBackup save(GameFileVersionBackup gameFileVersionBackup) {
        return springRepository.save(gameFileVersionBackup);
    }

    @Override
    public Optional<GameFileVersionBackup> findCurrentlyDownloading() {
        return springRepository.findByStatus(FileBackupStatus.IN_PROGRESS);
    }

    @Override
    public Page<GameFileVersionBackup> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByUrlAndVersion(url, version);
    }

    @Override
    public Optional<GameFileVersionBackup> findById(Long id) {
        return springRepository.findById(id);
    }

    @Override
    public Page<GameFileVersionBackup> findAllDiscovered(Pageable pageable) {
        return springRepository.findAllByStatus(pageable, FileBackupStatus.DISCOVERED);
    }
}
