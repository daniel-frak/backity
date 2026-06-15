package dev.codesoapbox.backity.shared.infrastructure.config.jpa;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositorySliceConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@JpaRepositorySliceConfiguration
public class JpaAuditingConfig {
}
