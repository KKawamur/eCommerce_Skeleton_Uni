package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DeliveryPackageRepository extends CrudRepository <DeliveryPackage, UUID> {
    @Query("SELECT dp FROM DeliveryPackage dp LEFT JOIN FETCH dp.deliveryPackageParts " +
            "WHERE dp.order.id = :orderId AND dp.storageUnit.id = :storageUnitId")
    List<DeliveryPackage> findByOrderIdAndStorageUnitId(@Param("orderId") UUID orderId, @Param("storageUnitId") UUID storageUnitId);

    @Query("SELECT dp FROM DeliveryPackage dp JOIN FETCH dp.deliveryPackageParts WHERE dp.order.id = :orderId")
    Set<DeliveryPackage> findByOrderIdWithParts(@Param("orderId") UUID orderId);
}
