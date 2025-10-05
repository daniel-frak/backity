package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings(
        // @Data and @EqualsAndHashCode are safe to use here because:
        // - We explicitly add a @NoArgsConstructor (required by Jpa spec)
        // - @EqualsAndHashCode only uses id (so won't break HashSets)
        // - We don't do lazy loading (so toString() won't break it).
        {"com.intellij.jpb.LombokDataInspection", "com.intellij.jpb.LombokEqualsAndHashCodeInspection"})
@Entity(name = "OutboxEvent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OutboxEventEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    private String type;

    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter.class)
    @ColumnTransformer(write = "?::json")
    private Map<String, Object> payload;

    private boolean processed = false;

    @CreatedDate
    private LocalDateTime createdAt; // @TODO Should it be LocalDateTime? Or Instant?
}

