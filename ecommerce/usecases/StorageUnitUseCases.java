package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.UUID;


/**
 * This interface contains methods needed in the context of managing storage units, and handling
 * the shop inventory, i.e. adding and removing products in the storage unit.
 */
public interface StorageUnitUseCases {
    /**
     * Adds a new storage unit to the shop
     * @param address
     * @param name
     * @return the id of the new storage unit
     * @throws ShopException if ...
     *      - address is null
     *      - name is null or empty
     */
    public UUID addNewStorageUnit( HomeAddressType address, String name );


    /**
     * Deletes all storage units from the shop. Intended for testing purposes.
     * @throws ShopException if the products catalog is not empty
     */
    public void deleteAllStorageUnits();


    /**
     * Adds a certain quantity of a given product to the inventory
     * @param storageUnitId
     * @param productId
     * @param addedQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - productId is null
     *      - the product with that id does not exist
     *      - addedQuantity < 0
     */
    public void addToInventory( UUID storageUnitId, UUID productId, int addedQuantity );


    /**
     * Removes a certain quantity of a given product from the inventory.
     * If the new total quantity is lower than the currently reserved products, some of currently reserved products
     * (in the clients' shopping baskets) are removed. This means that some of the reserved products are lost for
     * the client. (This is necessary because there probably was a mistake in the inventory management, a mis-counting,
     * or some of the products were stolen from the storage unit, are broken, etc.)
     * @param storageUnitId
     * @param productId
     * @param removedQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - productId is null
     *      - the product with that id does not exist
     *      - removedQuantity < 0
     *      - the removed quantity is greater than the current inventory and the currently reserved products together
     */
    public void removeFromInventory( UUID storageUnitId, UUID productId, int removedQuantity );


    /**
     * Changes the total quantity of a given product in the inventory.
     * If the new total quantity is lower than the currently reserved products, some of currently reserved products
     * (in the clients' shopping baskets) are removed. This means that some of the reserved products are lost for
     * the client. (This is necessary because there probably was a mistake in the inventory management, a mis-counting,
     * or some of the products were stolen from the storage unit, are broken, etc.)
     * @param storageUnitId
     * @param productId
     * @param newTotalQuantity
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - productId is null
     *      - the product with that id does not exist
     *      - newTotalQuantity < 0
     */
    public void changeInventoryTo( UUID storageUnitId, UUID productId, int newTotalQuantity );


    /**
     * Get the current inventory of a given product in one specific storage unit.
     * @param storageUnitId
     * @param productId
     * @return the current total inventory of the product
     * @throws ShopException if ...
     *      - storageUnitId is null
     *      - the storage unit with that id does not exist
     *      - productId is null
     *      - the product with that id does not exist
     */
    public int getAvailableInventory( UUID storageUnitId, UUID productId );


    /**
     * Get the current total inventory of a given product, across all storage units, and including the currently
     * reserved products in shopping baskets.
     * @param productId
     * @return the current total inventory of the product
     * @throws ShopException if ...
     *      - productId is null
     *      - the product with that id does not exist
     */
    public int getAvailableInventory( UUID productId );
}
