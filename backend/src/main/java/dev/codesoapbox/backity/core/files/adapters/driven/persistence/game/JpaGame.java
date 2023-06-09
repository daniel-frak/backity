package dev.codesoapbox.backity.core.files.adapters.driven.persistence.game;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity(name = "Game")
@EntityListeners(AuditingEntityListener.class)
@Data
public class JpaGame {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_id_generator")
    @SequenceGenerator(name = "game_id_generator", sequenceName = "seq_game_id")
    private UUID id;

    @NotNull
    private String title;
}
