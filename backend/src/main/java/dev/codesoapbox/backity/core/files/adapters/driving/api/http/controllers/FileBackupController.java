package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsJsonMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJson;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PageJsonMapper;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationJson;
import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.PaginationJsonMapper;
import dev.codesoapbox.backity.core.shared.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Backups", description = "Everything to do with downloading discovered files")
@RestController
@RequestMapping("backups")
@RequiredArgsConstructor
@Slf4j
public class FileBackupController {

    private final GameFileDetailsRepository gameFileDetailsRepository;
    private final GameFileDetailsJsonMapper gameFileDetailsMapper;
    private final PaginationJsonMapper paginationMapper;
    private final PageJsonMapper pageMapper;

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @GetMapping("current")
    public GameFileDetailsJson getCurrentlyProcessing() {
        return gameFileDetailsRepository.findCurrentlyDownloading()
                .map(GameFileDetailsJsonMapper.INSTANCE::toJson)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @GetMapping("queue")
    public PageJson<GameFileDetailsJson> getQueueItems(PaginationJson pagination) {
        Page<GameFileDetails> foundPage =
                gameFileDetailsRepository.findAllWaitingForDownload(paginationMapper.toModel(pagination));
        return pageMapper.toJson(foundPage, gameFileDetailsMapper::toJson);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @GetMapping("processed")
    public PageJson<GameFileDetailsJson> getProcessedFiles(PaginationJson pagination) {
        Page<GameFileDetails> foundPage =
                gameFileDetailsRepository.findAllProcessed(paginationMapper.toModel(pagination));
        return pageMapper.toJson(foundPage, gameFileDetailsMapper::toJson);
    }

    // @TODO Should be POST
    @Operation(summary = "Enqueue file", description = "Adds a discovered file to the download queue")
    @GetMapping("enqueue/{gameFileVersionId}")
    public ResponseEntity<Void> download(@PathVariable String gameFileVersionId) {
        // @TODO Return illegal argument if can't construct id
        Optional<GameFileDetails> gameFileVersionBackup = gameFileDetailsRepository
                .findById(new GameFileDetailsId(UUID.fromString(gameFileVersionId)));

        if (gameFileVersionBackup.isPresent()) {
            gameFileVersionBackup.get().enqueue();
            gameFileDetailsRepository.save(gameFileVersionBackup.get());
            return ResponseEntity.ok().build();
        }

        log.warn("Could not enqueue file. Game file version not found: " + gameFileVersionId);
        return ResponseEntity.badRequest().build();
    }
}
