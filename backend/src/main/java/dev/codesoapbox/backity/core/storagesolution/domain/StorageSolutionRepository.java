package dev.codesoapbox.backity.core.storagesolution.domain;

import java.util.List;

public interface StorageSolutionRepository {

    StorageSolution getById(StorageSolutionId id);

    List<StorageSolution> findAll();
}
