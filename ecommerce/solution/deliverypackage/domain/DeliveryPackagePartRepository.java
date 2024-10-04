package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DeliveryPackagePartRepository extends CrudRepository<DeliveryPackagePart, UUID> {
}
