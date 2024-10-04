package thkoeln.archilab.ecommerce.solution.product.domain;

import java.util.List;
import java.util.UUID;

public abstract class UsableDeliveryPackage {
    public abstract void deleteAllDeliveries();
    public abstract List useCreateDeliveryPackagesForOrder(UUID orderId);
}
