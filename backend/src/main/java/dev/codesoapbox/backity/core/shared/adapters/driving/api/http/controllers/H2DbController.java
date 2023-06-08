package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "H2 Database", description = "Operations specific to a H2 Database")
@RestController
@ConditionalOnProperty(value = "spring.datasource.driverClassName", havingValue = "org.h2.Driver")
public class H2DbController {

    private final EntityManager entityManager;

    private final String h2DumpPath;

    public H2DbController(EntityManager entityManager, @Value("${h2dump.path}") String h2DumpPath) {
        this.entityManager = entityManager;
        this.h2DumpPath = h2DumpPath;
    }

    @Operation(summary = "Dump to SQL file", description = "Dumps the H2 database into a file")
    @GetMapping("h2/dump")
    void dumpSql() {
        entityManager.createNativeQuery("SCRIPT TO '" + h2DumpPath + "';").getResultList();
    }
}
