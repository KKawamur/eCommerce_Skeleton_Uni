package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.UUID;


/**
 * This interface contains methods needed in the context of use cases concerning the product catalog.
 */

public interface ProductCatalogUseCases {
    /**
     * Adds a new product to the shop catalog
     * @param productId
     * @param name
     * @param description
     * @param size
     * @param buyingPrice - the price the shop pays for the product
     * @param sellPrice
     * @throws ShopException if ...
     *      - productId is null,
     *      - the product with that id already exists,
     *      - name or description are null or empty,
     *      - the size is <= 0 (but can be null!),
     *      - the buyingPrice is null,
     *      - the sell price is null,
     *      - the sell price is lower than the buyingPrice
     */
    public void addProductToCatalog( UUID productId, String name, String description, Float size,
                                           MoneyType buyingPrice, MoneyType sellPrice );



    /**
     * Removes a product from the shop catalog
     * @param productId
     * @throws ShopException if
     *      - the product id does not exist
     *      - the product is still in inventory
     *      - the product is still reserved in a shopping basket, or part of a completed order
     */
    public void removeProductFromCatalog( UUID productId );


    /**
     * Get the sell price of a given product
     * @param productId
     * @return the sell price
     * @throws ShopException if ...
     *      - productId is null,
     *      - the product with that id does not exist
     */
    public MoneyType getSellPrice( UUID productId );


    /**
     * Clears the product catalog, i.e. removes all products from the catalog, including all references in
     * inventorys, all the reservations in shopping baskets and all the orders. Intended for testing purposes.
     */
    public void deleteProductCatalog();

}
