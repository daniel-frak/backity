package dev.codesoapbox.backity.core.files.downloading.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * A file that is either currently being downloaded or scheduled for download.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnqueuedFileDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "enqueued_file_generator")
    @SequenceGenerator(name = "enqueued_file_generator", sequenceName = "seq_enqueued_file")
    private Long id;

    @NotNull
    private String source;

    @NotNull
    private String url;

    @NotNull
    private String name;

    @NotNull
    private String gameTitle;

    @NotNull
    private String version;

    @NotNull
    private String size;

    @NotNull
    @CreatedDate
    private LocalDateTime dateCreated;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DownloadStatus status = DownloadStatus.WAITING;
    
    private String failedReason;

    public void fail(String failedReason) {
        this.status = DownloadStatus.FAILED;
        this.failedReason = failedReason;
    }
}
