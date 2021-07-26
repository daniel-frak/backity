package dev.codesoapbox.gogbackupservice.files.discovery.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = @Index(columnList = "uniqueId", name = "idx_unique_id", unique = true))
@Data
public class DiscoveredFile {

    @EmbeddedId
    private DiscoveredFileId id;

    private UUID uniqueId = UUID.randomUUID();

    private String name;
    private String gameTitle;
    private String size;

    @CreatedDate
    private LocalDateTime dateCreated;

    @LastModifiedDate
    private LocalDateTime dateModified;

    private boolean enqueued;
    private boolean ignored;
}
