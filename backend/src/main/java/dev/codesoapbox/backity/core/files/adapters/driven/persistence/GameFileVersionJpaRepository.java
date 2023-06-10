package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GameFileVersionJpaRepository implements GameFileVersionRepository {

    private final GameFileVersionSpringRepository springRepository;
    private final JpaGameFileVersionMapper mapper;

    @Override
    public Optional<GameFileVersion> findOldestWaitingForDownload() {
        return springRepository.findAllWaitingForDownload(PageRequest.of(0, 1)).get()
                .findFirst()
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersion> findAllWaitingForDownload(Pageable pageable) {
        return springRepository.findAllWaitingForDownload(pageable)
                .map(mapper::toModel);
    }

    @Override
    public GameFileVersion save(GameFileVersion gameFileVersion) {
        JpaGameFileVersion entity = mapper.toEntity(gameFileVersion);
        JpaGameFileVersion savedEntity = springRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public Optional<GameFileVersion> findCurrentlyDownloading() {
        return springRepository.findByBackupStatus(FileBackupStatus.IN_PROGRESS)
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersion> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsByUrlAndVersion(url, version);
    }

    @Override
    public Optional<GameFileVersion> findById(Long id) {
        return springRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileVersion> findAllDiscovered(Pageable pageable) {
        return springRepository.findAllByBackupStatus(pageable, FileBackupStatus.DISCOVERED)
                .map(mapper::toModel);
    }

    @Override
    public List<GameFileVersion> findAllByGameId(GameId gameId) {
        String gameIdString = gameId.value().toString();
        return springRepository.findAllByGameId(gameIdString).stream()
                .map(mapper::toModel)
                .toList();
    }
}
