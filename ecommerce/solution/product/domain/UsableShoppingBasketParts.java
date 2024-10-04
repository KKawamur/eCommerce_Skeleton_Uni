package thkoeln.archilab.ecommerce.solution.product.domain;

import thkoeln.archilab.ecommerce.domainprimitives.Email;

import java.util.List;
import java.util.UUID;

public abstract class UsableShoppingBasketParts {
    public abstract List findListOfShoppingBasketPartsByProductId(UUID productId);

    public abstract void deleteAllShoppingBasketParts();

    public abstract int getReservedInventory(UUID productId);

    public abstract int useGetAvailableInventory(UUID productId);

    public abstract void deleteShoppingBasketFromClient(UUID clientId);

    public abstract void deleteStorageUnits();

    public abstract void updateShoppingBasketPart(UUID uuid, int newProductAmount);

    public abstract UUID getNewOrExistingShoppingBasketId(UUID clientId);

    public abstract void useRemoveProductFromShoppingBasket(Email clientEmail, UUID productId, int excessToBeRemovedProductQuantity);

}