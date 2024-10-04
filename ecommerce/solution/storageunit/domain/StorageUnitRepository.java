package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface StorageUnitRepository extends CrudRepository<StorageUnit, UUID> {

    boolean existsByIdAndInventoryLevelListProductId(@NonNull UUID id, @NonNull UUID id1);

    StorageUnit findByIdAndInventoryLevelListProductId(@NonNull UUID id, @NonNull UUID id1);

    List<StorageUnit> findByInventoryLevelListProductId(@NonNull UUID id);
}
