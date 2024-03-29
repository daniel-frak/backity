package dev.codesoapbox.backity.core.game.adapters.driven.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Game")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull
    @Column(unique = true)
    private String title;

    @NotNull
    @CreatedDate
    private LocalDateTime dateCreated;

    @NotNull
    @LastModifiedDate
    private LocalDateTime dateModified;
}
