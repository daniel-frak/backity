package dev.codesoapbox.backity.core.files.discovery.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A file that is available to download.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = @Index(columnList = "uniqueId", name = "idx_unique_id", unique = true))
@Data
public class DiscoveredFile {

    @EmbeddedId
    private DiscoveredFileId id;

    @NotNull
    private UUID uniqueId = UUID.randomUUID();

    @NotNull
    private String source;

    @NotNull
    private String name;

    @NotNull
    private String gameTitle;

    @NotNull
    private String size;

    @NotNull
    @CreatedDate
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;

    private boolean enqueued;
    private boolean ignored;
}
