package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPackageDTO {
    private UUID id;
    private UUID storageUnitId;
    private UUID orderId;
    private DeliveryPackagePartDTO[] deliveryPackageParts;
}
