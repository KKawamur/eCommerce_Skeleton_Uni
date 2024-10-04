package thkoeln.archilab.ecommerce.solution.product.domain;

import java.util.UUID;

public abstract class UsableInventoryManagementService {
    public abstract int useGetAvailableInventory(UUID productId);
    public abstract int getProductInventoryQuantity(UUID productId);
    public abstract void deleteStorageUnits();
    public abstract void useRemoveFromInventory(UUID storageId, UUID productId, int quantity);
}
