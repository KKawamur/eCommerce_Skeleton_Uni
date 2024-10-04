package thkoeln.archilab.ecommerce.solution.storageunit.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.solution.product.domain.ProductRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.UsableInventoryManagementService;
import thkoeln.archilab.ecommerce.solution.product.domain.UsableShoppingBasketParts;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.InventoryLevel;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.InventoryLevelRepository;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnitRepository;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.List;
import java.util.UUID;

@Service
public class StorageUnitService extends UsableInventoryManagementService implements StorageUnitUseCases {
    private ProductRepository productRepository;
    private StorageUnitRepository storageUnitRepository;
    private UsableShoppingBasketParts usableShoppingBasketParts;
    private InventoryLevelRepository inventoryLevelRepository;

    @Autowired
    public StorageUnitService(StorageUnitRepository storageUnitRepository,
                              ProductRepository productRepository,
                              InventoryLevelRepository inventoryLevelRepository
                              ) {
        this.productRepository = productRepository;
        this.storageUnitRepository = storageUnitRepository;
        this.inventoryLevelRepository = inventoryLevelRepository;
    }

    @Autowired
    public void setUsableShoppingBasketParts(@Lazy UsableShoppingBasketParts usableShoppingBasketParts){
        this.usableShoppingBasketParts = usableShoppingBasketParts;
    }

    @Override
    public void addToInventory(UUID storageUnitId,UUID productId, int addedQuantity) {
        System.out.println("ADDING " + productId + " WITH QUANTITY " + addedQuantity + " TO INVENTORY");

        if (productId == null)
            throw new ShopException("Product id cannot be null!");
        doesProductExist(productId);
        if(storageUnitId == null)
            throw new ShopException("Storage unit id cannot be null!");
        doesStorageUnitExist(storageUnitId);
        if (addedQuantity < 0)
            throw new ShopException("Added quantity cannot be smaller than 0!");

        if(storageUnitRepository.existsByIdAndInventoryLevelListProductId(storageUnitId, productId)) {
            StorageUnit storageUnit = storageUnitRepository.findByIdAndInventoryLevelListProductId(storageUnitId, productId);
            List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
            for (InventoryLevel inventoryLevel : inventoryLevels){
                if (inventoryLevel.getProduct().getId().equals(productId)) {
                    int oldProductQuantity = inventoryLevel.getProductQuantity();
                    inventoryLevel.setProductQuantity(oldProductQuantity += addedQuantity);
                    inventoryLevelRepository.save(inventoryLevel);
                    break;
                }
            }
            storageUnitRepository.save(storageUnit);
            System.out.println("Added" + productId + " with quantity " + addedQuantity +" to storage unit " + storageUnitId);
            System.out.println("New total quantity is " + getAvailableInventory(productId));
        } else {
            StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).get();
            InventoryLevel inventoryLevel = new InventoryLevel();
            inventoryLevel.setProduct(productRepository.findById(productId).get());
            inventoryLevel.setProductQuantity(addedQuantity);
            inventoryLevelRepository.save(inventoryLevel);

            storageUnit.getInventoryLevelList().add(inventoryLevel);
            storageUnitRepository.save(storageUnit);
            System.out.println("Added" + productId + " with quantity " + addedQuantity +" to storage unit " + storageUnitId);
            System.out.println("New total quantity is " + getAvailableInventory(storageUnitId, productId));
        }

    }

    @Override
    public void removeFromInventory(UUID storageUnitId, UUID productId, int removedQuantity) {
        isUuidNull(storageUnitId);
        isUuidNull(productId);
        doesStorageUnitExist(storageUnitId);
        doesProductExist(productId);
        if (removedQuantity < 0)
            throw new ShopException("Quantity to be removed cannot be smaller than or equal to zero!");

        System.out.println("Removing Product: " + productId + "from Storage Unit: " +  storageUnitId + " with Quantity: " + removedQuantity);
        int productInInventoryQuantity = 0;
        InventoryLevel inventoryLevelOfProduct = null;
        if(storageUnitRepository.existsByIdAndInventoryLevelListProductId(storageUnitId,productId)){
            StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).get();
            List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
            for (InventoryLevel inventoryLevel : inventoryLevels){
                if (inventoryLevel.getProduct().getId().equals(productId)){
                    productInInventoryQuantity = inventoryLevel.getProductQuantity();
                    inventoryLevelOfProduct = inventoryLevel;
                    System.out.println("Product: " + productId + " is stored in Inventory Level: "+ inventoryLevelOfProduct + " with Quantity: " +productInInventoryQuantity);
                    break;
                }
            }
        }

        System.out.println("Number of Product: " + productId + " in inventory: " + productInInventoryQuantity + " and reserved in Shopping Baskets: " + usableShoppingBasketParts.getReservedInventory(productId));
        if (removedQuantity > productInInventoryQuantity + usableShoppingBasketParts.getReservedInventory(productId))
            throw new ShopException("Quantity exceed number of Products in this Storage Unit and all shopping baskets combined!");

        if (productInInventoryQuantity - removedQuantity < usableShoppingBasketParts.getReservedInventory(productId)) {
            takeProductsFromShoppingPartDuringCheckout(productId, productInInventoryQuantity, removedQuantity, inventoryLevelOfProduct);

        } else {
            if (inventoryLevelOfProduct != null){
                inventoryLevelOfProduct.setProductQuantity(productInInventoryQuantity - removedQuantity);
                System.out.println("New Stored Quantity: " + inventoryLevelOfProduct.getProductQuantity());
            }
        }
        if (inventoryLevelOfProduct != null)
            inventoryLevelRepository.save(inventoryLevelOfProduct);
        System.out.println("Finished removing Products.");
    }

    private void takeProductsFromShoppingPartDuringCheckout(UUID productId, int productInInventoryQuantity, int removedQuantity, InventoryLevel inventoryLevelOfProduct) {
        System.out.println("Too few Products in Storage unit. Taking Products out of Shopping Baskets");
        int excessToBeRemovedProductQuantity = usableShoppingBasketParts.getReservedInventory(productId) - productInInventoryQuantity + removedQuantity;
        List<ShoppingBasket> shoppingBaskets = usableShoppingBasketParts.findListOfShoppingBasketPartsByProductId(productId);
        for (ShoppingBasket shoppingBasket : shoppingBaskets) {
            List<ShoppingBasketPart> shoppingBasketParts = shoppingBasket.getShoppingBasketParts();
            ShoppingBasketPart emptyShoppingBasketPart = null;
            for (ShoppingBasketPart shoppingBasketPart : shoppingBasketParts) {
                System.out.println("Number of Products that need to be taken from Shopping baskets:" + excessToBeRemovedProductQuantity);
                if(shoppingBasketPart.getProduct().getId().equals(productId)) {
                    if (excessToBeRemovedProductQuantity <= 0)
                        break;
                    if (shoppingBasketPart.getQuantity() >= excessToBeRemovedProductQuantity) {
                        System.out.println("Taking " + excessToBeRemovedProductQuantity + " " + shoppingBasketPart.getProduct() + "out of Shopping Basket Part: " +shoppingBasket.getId());
                        Email clientEmail = shoppingBasket.getClient().getEmail();
                        usableShoppingBasketParts.useRemoveProductFromShoppingBasket(clientEmail, productId, excessToBeRemovedProductQuantity);
                        System.out.println("Number of Products remaining in Shopping Basket: " + shoppingBasketPart.getQuantity());
                        excessToBeRemovedProductQuantity = 0;
                        System.out.println("Remaining number of Products to be removed: " + excessToBeRemovedProductQuantity);
                        break;
                    } else {
                        System.out.println("Taking " + excessToBeRemovedProductQuantity + " " + shoppingBasketPart.getProduct() + "out of Shopping Basket Part: " +shoppingBasket.getId());
                        excessToBeRemovedProductQuantity -= shoppingBasketPart.getQuantity();
                        Email clientEmail = shoppingBasket.getClient().getEmail();
                        usableShoppingBasketParts.useRemoveProductFromShoppingBasket(clientEmail, productId, shoppingBasketPart.getQuantity());
                        System.out.println("Number of Products remaining in Shopping Basket: " + shoppingBasketPart.getQuantity());
                        usableShoppingBasketParts.updateShoppingBasketPart(shoppingBasketPart.getId(), 0);
                        System.out.println("Remaining number of Products to be removed: " + excessToBeRemovedProductQuantity);
                        break;
                    }
                }
            }
            if (inventoryLevelOfProduct != null){
                inventoryLevelOfProduct.setProductQuantity(0);
            }
        }
    }

    @Override
    public void changeInventoryTo(UUID storageUnitId, UUID productId, int newTotalQuantity) {
        System.out.println("CHANGING INVENTORY QUANTITY FROM PRODUCT " + productId + " TO NEW TOTAL QUANTITY" + newTotalQuantity);
        isUuidNull(storageUnitId);
        isUuidNull(productId);
        doesStorageUnitExist(storageUnitId);
        doesProductExist(productId);
        if (newTotalQuantity < 0)
            throw new ShopException("New product quantity cannot be negative!");
        if(newTotalQuantity < getAvailableInventory(productId)){
            System.out.println("TAKING FROM SHOPPING CARTS INSTEAD");
            int productsToBeRemoved = getAvailableInventory(productId) - newTotalQuantity;
            removeFromInventory(storageUnitId, productId, productsToBeRemoved);
        } else {
            StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).get();
            List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
            for (InventoryLevel inventoryLevel : inventoryLevels){
                if(inventoryLevel.getProduct().getId().equals(productId)){
                    inventoryLevel.setProductQuantity(newTotalQuantity);
                    inventoryLevelRepository.save(inventoryLevel);
                    break;
                }
            }
            System.out.println("Product: " + productId+
                    "\nNew ProductQuantity: " + newTotalQuantity);
        }

    }

    @Override
    public int getAvailableInventory(UUID storageUnitId, UUID productId) {
        isUuidNull(storageUnitId);
        isUuidNull(productId);
        doesStorageUnitExist(storageUnitId);
        doesProductExist(productId);
        StorageUnit storageUnit = storageUnitRepository.findById(storageUnitId).get();
        List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
        for (InventoryLevel inventoryLevel : inventoryLevels){
            if (inventoryLevel.getProduct().getId().equals(productId)){
                return inventoryLevel.getProductQuantity();
            }
        }
        return 0;
    }

    @Override
    public UUID addNewStorageUnit(HomeAddressType address, String name) {
        if(address == null || name == null)
            throw new ShopException("Parameters cannot be null!");
        if(name.isEmpty())
            throw new ShopException("Name cannot be empty!");

        StorageUnit storageUnit = new StorageUnit();
        storageUnit.setHomeAddress((HomeAddress) address);
        storageUnit.setName(name);
        storageUnitRepository.save(storageUnit);
        return storageUnit.getId();
    }

    @Override
    public void deleteAllStorageUnits() {
        storageUnitRepository.deleteAll();
        inventoryLevelRepository.deleteAll();
    }

    @Override
    public int getAvailableInventory(UUID productId) {
        isUuidNull(productId);
        doesProductExist(productId);
        System.out.println("Getting Total Inventory amount for: " + productId);
        int productQuantity = 0;
        List<StorageUnit> storageUnits = storageUnitRepository.findByInventoryLevelListProductId(productId);
        System.out.println(storageUnits.size() + " Storage Units have Product: " + productId + " in inventory");
        for (StorageUnit storageUnit : storageUnits){
            List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
            for (InventoryLevel inventoryLevel : inventoryLevels){
                if (inventoryLevel.getProduct().getId().equals(productId)){
                    productQuantity += inventoryLevel.getProductQuantity();
                    System.out.println("Storage Unit: " +storageUnit.getId() + "has Product in Quantity of" + inventoryLevel.getProductQuantity());
                    System.out.println("New Total Quantity is: " + productQuantity);
                    break;
                }
            }
        }
        System.out.println("Last Total Quantity is: " + productQuantity);
        return  productQuantity;

    }

    private void doesProductExist(UUID productId) {
        if (!productRepository.existsById(productId)){
            System.out.println("Product does not exist!");
            throw new ShopException("Product does not exist!");}
    }

    @Override
    public int useGetAvailableInventory(UUID productId) {
        return getAvailableInventory(productId);
    }

    @Override
    public int getProductInventoryQuantity(UUID productId) {
        System.out.println("RETURNING INVENTORY QUANTITY OF PRODUCT " + productId);
        doesProductExist(productId);
        System.out.println("PRODUCT EXISTS");
        List<StorageUnit> storageUnits = storageUnitRepository.findByInventoryLevelListProductId(productId);
        int productInventoryQuantity = 0;
        for (StorageUnit storageUnit : storageUnits){
            List<InventoryLevel> inventoryLevels = storageUnit.getInventoryLevelList();
            for (InventoryLevel inventoryLevel : inventoryLevels){
                if(inventoryLevel.getProduct().getId().equals(productId)){
                    productInventoryQuantity += inventoryLevel.getProductQuantity();
                    break;
                }
            }
        }
        return productInventoryQuantity;
    }

    @Override
    public void deleteStorageUnits() {
        deleteAllStorageUnits();
    }

    @Override
    public void useRemoveFromInventory(UUID storageId, UUID productId, int quantity) {
        removeFromInventory(storageId, productId, quantity);
    }

    private void isUuidNull(UUID uuid){
        if (uuid == null)
            throw new ShopException("UUID cannot be Null!");
    }
    private void doesStorageUnitExist(UUID storageUnitId){
        if(!storageUnitRepository.existsById(storageUnitId))
            throw new ShopException("Storage unit does not exist!");
    }
}
