package dev.codesoapbox.backity.core.files.downloading.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
public class EnqueuedFileDownload {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "seq_enqueued_file")
    private Long id;

    private String source;
    private String url;
    private String name;
    private String gameTitle;
    private String version;
    private String size;

    @CreatedDate
    private LocalDateTime dateCreated;

    private boolean downloaded;
    private boolean failed;
    private String failedReason;
}
