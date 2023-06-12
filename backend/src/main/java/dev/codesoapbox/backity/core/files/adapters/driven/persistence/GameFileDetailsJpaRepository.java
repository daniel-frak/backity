package dev.codesoapbox.backity.core.files.adapters.driven.persistence;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GameFileDetailsJpaRepository implements GameFileDetailsRepository {

    private final GameFileDetailsSpringRepository springRepository;
    private final GameFileDetailsJpaEntityMapper mapper;

    @Override
    public Optional<GameFileDetails> findOldestWaitingForDownload() {
        return springRepository.findAllWaitingForDownload(PageRequest.of(0, 1)).get()
                .findFirst()
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllWaitingForDownload(Pageable pageable) {
        return springRepository.findAllWaitingForDownload(pageable)
                .map(mapper::toModel);
    }

    @Override
    public GameFileDetails save(GameFileDetails gameFileDetails) {
        GameFileDetailsJpaEntity entity = mapper.toEntity(gameFileDetails);
        GameFileDetailsJpaEntity savedEntity = springRepository.save(entity);
        return mapper.toModel(savedEntity);
    }

    @Override
    public Optional<GameFileDetails> findCurrentlyDownloading() {
        return springRepository.findByBackupDetailsStatus(FileBackupStatus.IN_PROGRESS)
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllProcessed(Pageable pageable) {
        return springRepository.findAllProcessed(pageable)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByUrlAndVersion(String url, String version) {
        return springRepository.existsBySourceFileDetailsUrlAndSourceFileDetailsVersion(url, version);
    }

    @Override
    public Optional<GameFileDetails> findById(GameFileDetailsId id) {
        return springRepository.findById(id.value())
                .map(mapper::toModel);
    }

    @Override
    public Page<GameFileDetails> findAllDiscovered(Pageable pageable) {
        return springRepository.findAllByBackupDetailsStatus(pageable, FileBackupStatus.DISCOVERED)
                .map(mapper::toModel);
    }

    @Override
    public List<GameFileDetails> findAllByGameId(GameId gameId) {
        return springRepository.findAllByGameId(gameId.value()).stream()
                .map(mapper::toModel)
                .toList();
    }
}
