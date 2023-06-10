package dev.codesoapbox.backity.core.files.adapters.driving.api.http.controllers;

import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsJson;
import dev.codesoapbox.backity.core.files.adapters.driving.api.http.model.GameFileDetailsJsonMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "List queue items", description = "Returns the file currently being downloaded")
    @PageableAsQueryParam
    @GetMapping("current")
    public GameFileDetailsJson getCurrentlyProcessing() {
        return gameFileDetailsRepository.findCurrentlyDownloading()
                .map(GameFileDetailsJsonMapper.INSTANCE::toJson)
                .orElse(null);
    }

    @Operation(summary = "List queue items", description = "Returns a paginated list of all downloads in the queue")
    @PageableAsQueryParam
    @GetMapping("queue")
    public Page<GameFileDetailsJson> getQueueItems(@Parameter(hidden = true) Pageable pageable) {
        return gameFileDetailsRepository.findAllWaitingForDownload(pageable)
                .map(GameFileDetailsJsonMapper.INSTANCE::toJson);
    }

    @Operation(summary = "List queue items",
            description = "Returns a paginated list of all processed files (downloaded or failed)")
    @PageableAsQueryParam
    @GetMapping("processed")
    public Page<GameFileDetailsJson> getProcessedFiles(@Parameter(hidden = true) Pageable pageable) {
        return gameFileDetailsRepository.findAllProcessed(pageable)
                .map(GameFileDetailsJsonMapper.INSTANCE::toJson);
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
