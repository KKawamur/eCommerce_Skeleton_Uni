package thkoeln.archilab.ecommerce.solution.product.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.product.domain.*;
import thkoeln.archilab.ecommerce.usecases.ProductCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.UUID;
@Service
public class ProductCatalogService implements ProductCatalogUseCases{
    private ProductRepository productRepository;
    private UsableOrderParts usableOrderParts;
    private UsableShoppingBasketParts usableShoppingBasketParts;
    private UsableDeliveryPackage usableDeliveryPackage;

    @Autowired
    public ProductCatalogService(ProductRepository productRepository,
                                 UsableOrderParts usableOrderParts,
                                 UsableDeliveryPackage usableDeliveryPackage,
                                 @Lazy UsableShoppingBasketParts usableShoppingBasketParts){
        this.productRepository = productRepository;
        this.usableOrderParts = usableOrderParts;
        this.usableDeliveryPackage = usableDeliveryPackage;
        this.usableShoppingBasketParts = usableShoppingBasketParts;
    }

    @Override
    public void addProductToCatalog(UUID productId, String name, String description, Float size, MoneyType buyingPrice, MoneyType sellPrice) {
        System.out.println("ADDING PRODUCT "+ productId + " TO CATALOGUE");
        if (productId == null)
            throw new ShopException("Id cannot be null!");
        doesProductNotExist(productId);
        if (name == null || description == null || buyingPrice == null || sellPrice == null)
            throw new ShopException("Parameters cannot be \"null\"!");
        if(name.isEmpty() || description.isEmpty())
            throw new ShopException("Name and description cannot be empty!");
        if((size != null && size <= 0) || buyingPrice.getAmount() <= 0 || sellPrice.getAmount() <= 0)
            throw new ShopException("Size, purchase price or sell price cannot be smaller than zero!");
        if(sellPrice.getAmount() < buyingPrice.getAmount())
            throw new ShopException("Sell price cannot be lower than purchase price!");

        Product p = new Product(productId, name, description, size, (Money) buyingPrice, (Money) sellPrice);
        productRepository.save(p);
        System.out.println("Product added: " + productId );

    }

    @Override
    public void removeProductFromCatalog(UUID productId) {
        System.out.println("REMOVING PRODUCT " + productId + "FROM CATALOGUE");
        doesProductExist(productId);
        if(productId != null && usableShoppingBasketParts.useGetAvailableInventory(productId) != 0)
            throw new ShopException("Product is still in inventory!");
        if (!usableShoppingBasketParts.findListOfShoppingBasketPartsByProductId(productId).isEmpty()|| // TODO: Product Catalog Service is referencing shoppingbasket and order repository.
                !usableOrderParts.findListOfOrderPartsByProductId(productId).isEmpty())                  //TODO: Makem not reference them anymore! The location of the files is important to get rid of the cycles!
            throw new ShopException("Product is reserved in a shopping basket or is part of a completed order!");


        productRepository.deleteById(productId);
        System.out.println("Removed Product: " + productId);
    }

    @Override
    public Money getSellPrice(UUID productId) {
        if (productId == null)
            throw new ShopException("Id cannot be null!");
        doesProductExist(productId);
        return productRepository.findById(productId).get().getSellPrice();
    }

    @Override
    public void deleteProductCatalog() {
        usableShoppingBasketParts.deleteAllShoppingBasketParts();
        usableOrderParts.deleteAllOrderParts();
        usableShoppingBasketParts.deleteStorageUnits();
        usableDeliveryPackage.deleteAllDeliveries();
        productRepository.deleteAll();
    }

    private void doesProductExist(UUID productId){
        if(!productRepository.existsById(productId))
            throw new ShopException("Product does not exist!");
    }
    private void doesProductNotExist(UUID productId){
        if(productRepository.existsById(productId))
            throw new ShopException("Product already exists!");
    }

    public Product findProductByID(UUID productId) {
        doesProductExist(productId);
        return productRepository.findById(productId).get();
    }

    public boolean existsByProductId(UUID productId) {
        return productRepository.existsById(productId);
    }
}
