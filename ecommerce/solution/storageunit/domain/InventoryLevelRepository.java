package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface InventoryLevelRepository extends CrudRepository<InventoryLevel, UUID> {
}
