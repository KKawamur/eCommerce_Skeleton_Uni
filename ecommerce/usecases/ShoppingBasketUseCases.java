package thkoeln.archilab.ecommerce.usecases;

import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.Map;
import java.util.UUID;

/**
 * This interface contains methods needed in the context of use cases handling the shopping basket of
 * a client.
 */
public interface ShoppingBasketUseCases {
    /**
     * Adds a product to the shopping basket of a client
     *
     * @param clientEmail
     * @param productId
     * @param quantity
     * @throws ShopException if ...
     *      - the client with the given email does not exist,
     *      - the product does not exist,
     *      - the quantity <= 0,
     *      - the product is not available in the requested quantity
     */
    public void addProductToShoppingBasket( EmailType clientEmail, UUID productId, int quantity );


    /**
     * Removes a product from the shopping basket of a client
     *
     * @param clientEmail
     * @param productId
     * @param quantity
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist,
     *      - the product does not exist
     *      - the quantity <= 0,
     *      - the product is not in the shopping basket in the requested quantity
     */
    public void removeProductFromShoppingBasket( EmailType clientEmail, UUID productId, int quantity );


    /**
     * Returns a map showing which products are in the shopping basket of a client and how many of each product
     *
     * @param clientEmail
     * @return the shopping basket of the client (map is empty if the shopping basket is empty)
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist
     */
    public Map<UUID, Integer> getShoppingBasketAsMap( EmailType clientEmail );


    /**
     * Returns the current value of all products in the shopping basket of a client
     *
     * @param clientEmail
     * @return the value of shopping basket of the client
     * @throws ShopException if ...
     *      - clientEmail is null,
     *      - the client with the given email does not exist
     */
    public MoneyType getShoppingBasketAsMoneyValue( EmailType clientEmail );


    /**
     * Get the number units of a specific product that are currently reserved in the shopping baskets of all clients
     * @param productId
     * @return the number of reserved products of that type in all shopping baskets
     * @throws ShopException
     *      - productId is null
     *      - if the product id does not exist
     */
    public int getReservedInventoryInShoppingBaskets( UUID productId );


    /**
     * Checks if the shopping basket of a client is empty
     *
     * @param clientEmail
     * @return true if the shopping basket is empty, false otherwise
     * @throws ShopException if ...
     *    - clientEmail is null
     *    - the client with the given email does not exist
     */
    public boolean isEmpty( EmailType clientEmail );


    /**
     * Checks out the shopping basket of a client. This means that the products in the shopping basket
     * are removed from the inventory. The shopping basket is emptied.
     *
     * @param clientEmail
     * @return the id of the order that was created
     * @throws ShopException if ...
     *      - clientEmail is null
     *      - the client with the given email does not exist
     *      - the shopping basket is empty
     *      - the products in the shopping basket are not available in the requested quantity
     */
    public UUID checkout( EmailType clientEmail );


    /**
     * Empties all shopping baskets for all clients
     */
    public void emptyAllShoppingBaskets();
}
