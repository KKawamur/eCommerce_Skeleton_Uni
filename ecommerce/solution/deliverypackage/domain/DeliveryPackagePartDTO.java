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
public class DeliveryPackagePartDTO {
    private UUID productId;
    private int quantity;
}
