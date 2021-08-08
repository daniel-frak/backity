package dev.codesoapbox.gogbackupservice.files.discovery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Data
public class DiscoveredFileId implements Serializable {

    private String url;
    private String version;
}
