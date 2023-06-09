package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GameFileVersionJpaBackupRepository implements GameFileVersionBackupRepository {

    private final GameFileVersionSpringRepository springRepository;
    private final JpaGameFileVersionBackupMapper mapper;

    @Override
    public Optional<GameFileVersionBackup> findOldestWaitingForDownload() {
        return springRepository.findAllWaitingForDownload(PageRequest.of(0, 1)).get()
                .findFirst()
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersionBackup> findAllWaitingForDownload(Pageable pageable) {
        return springRepository.findAllWaitingForDownload(pageable)
                .map(mapper::toModel);
    }

    @Override
    public GameFileVersionBackup save(GameFileVersionBackup gameFileVersionBackup) {
        JpaGameFileVersionBackup entity = mapper.toEntity(gameFileVersionBackup);
        JpaGameFileVersionBackup savedEntity = springRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public Optional<GameFileVersionBackup> findCurrentlyDownloading() {
        return springRepository.findByStatus(FileBackupStatus.IN_PROGRESS)
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersionBackup> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByUrlAndVersion(url, version);
    }

    @Override
    public Optional<GameFileVersionBackup> findById(Long id) {
        return springRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersionBackup> findAllDiscovered(Pageable pageable) {
        return springRepository.findAllByStatus(pageable, FileBackupStatus.DISCOVERED)
                .map(mapper::toModel);
    }

    @Override
    public List<GameFileVersionBackup> findAllByGameId(GameId gameId) {
        String gameIdString = gameId.value().toString();
        return springRepository.findAllByGameId(gameIdString).stream()
                .map(mapper::toModel)
                .toList();
    }
}
