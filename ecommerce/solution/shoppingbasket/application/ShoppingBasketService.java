package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientRepository;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPartRepository;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.*;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasket;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPart;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketPartRepository;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.ShoppingBasketRepository;
import thkoeln.archilab.ecommerce.usecases.ShoppingBasketUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShoppingBasketService extends UsableShoppingBasketParts implements ShoppingBasketUseCases {
    private OrderRepository orderRepository;
    private ClientRepository clientRepository;
    private ProductRepository productRepository;
    private ShoppingBasketRepository shoppingBasketRepository;
    private UsableInventoryManagementService usableInventoryManagementService;
    private ShoppingBasketPartRepository shoppingBasketPartRepository;
    private final UsableDeliveryPackage usableDeliveryPackage;
    private final OrderPartRepository orderPartRepository;

    @Autowired
    public ShoppingBasketService (OrderRepository orderRepository,
                                  ClientRepository clientRepository,
                                  ProductRepository productRepository,
                                  ShoppingBasketRepository shoppingBasketRepository,
                                  UsableInventoryManagementService usableInventoryManagementService,
                                  ShoppingBasketPartRepository shoppingBasketPartRepository,
                                  UsableDeliveryPackage usableDeliveryPackage,
                                  OrderPartRepository orderPartRepository){
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.usableInventoryManagementService = usableInventoryManagementService;
        this.shoppingBasketPartRepository = shoppingBasketPartRepository;
        this.usableDeliveryPackage = usableDeliveryPackage;
        this.orderPartRepository = orderPartRepository;
    }

    @Override
    public void addProductToShoppingBasket(EmailType clientEmail, UUID productId, int quantity) {
        System.out.println("ADDING PRODUCT " + productId + " WITH QUANTITY " + quantity + " TO SHOPPING CART OF " + clientEmail.toString());
        doesClientExist(clientEmail);
        doesProductExist(productId);
        isQuantityNegative(quantity);
        if (usableInventoryManagementService.getProductInventoryQuantity(productId) < quantity) {
            System.out.println("Not as many Products in stock!");
            throw new ShopException("Not as many Products in stock!");
        }
        if(getReservedInventoryInShoppingBaskets(productId) + quantity > usableInventoryManagementService.getProductInventoryQuantity(productId)){
            throw new ShopException("Too many Products reserved by other Customers!");
        } else {
            ShoppingBasket shoppingBasket = getNewOrExistingShoppingBasket(clientEmail);
            Product product = productRepository.findById(productId).get();

            List<ShoppingBasketPart> shoppingBasketParts = shoppingBasket.getShoppingBasketParts();
            boolean listContainsProduct = false;
            for (int i = 0; i < shoppingBasketParts.size(); i++) {
                if (shoppingBasketParts.get(i).getProduct().getId().equals(productId)) {
                    ShoppingBasketPart shoppingBasketPart = shoppingBasketParts.get(i);
                    int oldProductAmount = shoppingBasketPart.getQuantity();
                    shoppingBasketPart.setQuantity(oldProductAmount + quantity);
                    shoppingBasketPartRepository.save(shoppingBasketPart);
                    listContainsProduct = true;
                    System.out.println("Product: " + productId +
                            "\nAdded to shopping basket: " + shoppingBasket +
                            "\nFrom client: " + clientEmail +
                            "\nNew shopping basket amount: " + shoppingBasketParts.get(i).getQuantity());
                    break;
                }
            }
            if(!listContainsProduct) {
                ShoppingBasketPart shoppingBasketPart = new ShoppingBasketPart();
                shoppingBasketPart.setProduct(product);
                shoppingBasketPart.setQuantity(quantity);
                shoppingBasketPartRepository.save(shoppingBasketPart);
                shoppingBasketParts.add(shoppingBasketPart);
                System.out.println("Product: " + productId +
                        "\nAdded to shopping basket: " + shoppingBasket +
                        "\nFrom client: " + clientEmail +
                        "\nNew shopping basket amount: " + quantity);
            }
            shoppingBasketRepository.save(shoppingBasket);
        }
    }

    @Override
    public void removeProductFromShoppingBasket(EmailType clientEmail, UUID productId, int quantity) {
        System.out.println("REMOVING PRODUCT " + productId + " WITH QUANTITY " + quantity + " FROM SHOPPING BASKET OF CLIENT " + clientEmail );

        doesClientExist(clientEmail);
        doesProductExist(productId);
        isQuantityNegative(quantity);
        if (shoppingBasketRepository.findByClientEmail((Email) clientEmail) == null) {
            throw new ShopException("Client does not have anything in shopping basket!");
        } else {
            ShoppingBasket shoppingBasket = getNewOrExistingShoppingBasket(clientEmail);
            List<ShoppingBasketPart> shoppingBasketParts = shoppingBasket.getShoppingBasketParts();
            boolean productIsInOrderParts = false;
            ShoppingBasketPart emptyShoppingBasketPart = null;
            for (int i = 0; i < shoppingBasketParts.size(); i++){
                ShoppingBasketPart shoppingBasketPart = shoppingBasketParts.get(i);
                int basketPartQuantity = shoppingBasketPart.getQuantity();
                if (shoppingBasketPart.getProduct().getId().equals(productId)) {
                    productIsInOrderParts = true;
                    if (basketPartQuantity > quantity) {
                        shoppingBasketPart.setQuantity(basketPartQuantity - quantity);
                        shoppingBasketPartRepository.save(shoppingBasketPart);
                        shoppingBasketRepository.save(shoppingBasket);
                        System.out.println("Product: " + productId +
                                "\nRemoved from: " + shoppingBasket +
                                "\nFrom client: " + clientEmail +
                                "\nRemoved Quantity: " + quantity +
                                "\nRemaining Quantity: " +shoppingBasketPart.getQuantity());
                    } else if (basketPartQuantity == quantity) {
                        emptyShoppingBasketPart = shoppingBasketPart;
                        break;
                    } else throw new ShopException("Quantity exceeds number of given product in shopping basket!");
                }

            }
            if (emptyShoppingBasketPart != null){
               shoppingBasketParts.remove(emptyShoppingBasketPart);
               shoppingBasketRepository.save(shoppingBasket);
               shoppingBasketPartRepository.delete(emptyShoppingBasketPart);
            }
            if (!productIsInOrderParts){
                throw new ShopException("Product not in shopping basket!");
            }
        }
    }

    @Override
    public Map<UUID, Integer> getShoppingBasketAsMap(EmailType clientEmail) {
        Map<UUID, Integer> map = new HashMap<>();
        doesClientExist(clientEmail);

        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail((Email) clientEmail);
        if (shoppingBasket != null){
            List<ShoppingBasketPart> shoppingBasketParts = shoppingBasket.getShoppingBasketParts();
            for (ShoppingBasketPart shoppingBasketPart: shoppingBasketParts){
                map.put(shoppingBasketPart.getProduct().getId(),shoppingBasketPart.getQuantity());
            }
        }
        return map;
    }

    @Override
    public MoneyType getShoppingBasketAsMoneyValue(EmailType clientEmail) {
        float shoppingBasketValue = 0;
        doesClientExist(clientEmail);
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail((Email) clientEmail);
        if (shoppingBasket != null){
            List<ShoppingBasketPart> shoppingBasketParts = shoppingBasket.getShoppingBasketParts();
            for (ShoppingBasketPart shoppingBasketPart: shoppingBasketParts){
                shoppingBasketValue += shoppingBasketPart.getProduct().getSellPrice().getAmount() * shoppingBasketPart.getQuantity();
            }
        }
        return Money.of(shoppingBasketValue, shoppingBasketRepository.findByClientEmail((Email) clientEmail).getShoppingBasketParts().get(0).getProduct().getPurchasePrice().getCurrency());
    }

    @Override
    public int getReservedInventoryInShoppingBaskets(UUID productId) {
        int numberOfReservedItems = 0;
        doesProductExist(productId);
        List<ShoppingBasket> shoppingBaskets = shoppingBasketRepository.findMultipleByShoppingBasketPartsProductId(productId);
        for (ShoppingBasket shoppingBasket : shoppingBaskets){
            for(ShoppingBasketPart shoppingBasketPart: shoppingBasket.getShoppingBasketParts()){
                if (shoppingBasketPart.getProduct().getId().equals(productId)){
                    numberOfReservedItems += shoppingBasketPart.getQuantity();
                }
            }
        }
        return numberOfReservedItems;
    }

    @Override
    public boolean isEmpty(EmailType clientEmail) {
        if(clientEmail == null)
            throw new ShopException("Client email cannot be null!");
        doesClientExist(clientEmail);
        Map<UUID, Integer> shoppingBasketMap = getShoppingBasketAsMap(clientEmail);
        return (shoppingBasketMap.isEmpty());
    }

    @Override
    public UUID checkout(EmailType clientEmail) {
        if (clientEmail == null)
            throw new ShopException("Client email cannot be null!");
        doesClientExist(clientEmail);
        if(isEmpty(clientEmail))
            throw new ShopException("Shopping basket is empty!");
        System.out.println("Checking out Shopping Basket from Client: " + clientEmail);
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail((Email) clientEmail);
        Client client = clientRepository.findByEmail((Email) clientEmail);


        Order order = new Order();
        order.setClient(client);
        orderRepository.save(order);

        System.out.println("Initialized new Order: " + order.getId());

        if (shoppingBasket == null){
            shoppingBasket = new ShoppingBasket();
            shoppingBasket.setClient(client);
            shoppingBasketRepository.save(shoppingBasket);
        }
        for (ShoppingBasketPart shoppingBasketPart : shoppingBasket.getShoppingBasketParts()){
            OrderPart orderPart = new OrderPart();
            orderPart.setProduct(shoppingBasketPart.getProduct());
            orderPart.setProductAmount(shoppingBasketPart.getQuantity());

            order.getOrderParts().add(orderPart);
            System.out.println("Adding Order Part "+ orderPart.getOrderPartId() + " to Order");
        }
        orderPartRepository.saveAll(order.getOrderParts());
        orderRepository.save(order);

        System.out.println("Deleting shoppingbasket: " + shoppingBasket.getId());
        shoppingBasketRepository.delete(shoppingBasket);

        List<DeliveryPackage> deliveryPackages = usableDeliveryPackage.useCreateDeliveryPackagesForOrder(order.getId());
        return order.getId();
    }

    @Override
    public void emptyAllShoppingBaskets() {
        for (Client client : clientRepository.findAll()) {
            Map<UUID, Integer> shoppingBasketMap = getShoppingBasketAsMap(client.getEmail());
            for(var entry: shoppingBasketMap.entrySet()){
                useRemoveProductFromShoppingBasket(client.getEmail(), entry.getKey(), entry.getValue());
            }
        }
    }

    private void doesClientExist(EmailType clientEmail){
        if (!clientRepository.existsByEmail((Email) clientEmail)){
            System.out.println("Client does not exist!");
            throw new ShopException("Client does not exist!");}
    }
    private void doesProductExist(UUID productId){
        if (!productRepository.existsById(productId)){
            System.out.println("Product does not exist!");
            throw new ShopException("Product does not exist!");}
    }
    private ShoppingBasket getNewOrExistingShoppingBasket(EmailType clientEmail){
        ShoppingBasket shoppingBasket = shoppingBasketRepository.findByClientEmail((Email) clientEmail);
        if (shoppingBasket == null) {
            shoppingBasket = new ShoppingBasket();
            shoppingBasket.setClient(clientRepository.findByEmail((Email) clientEmail));
            shoppingBasketRepository.save(shoppingBasket);
        }
        return shoppingBasket;
    }

    private void isQuantityNegative(int quantity){
        if (quantity < 0){
            System.out.println("Quantity cannot be negative!");
            throw new ShopException("Quantity cannot be negative!");}
    }

    @Override
    public List findListOfShoppingBasketPartsByProductId(UUID productId) {
        List<ShoppingBasket> shoppingBaskets = shoppingBasketRepository.findMultipleByShoppingBasketPartsProductId(productId);
        return shoppingBaskets;
    }

    @Override
    public void deleteAllShoppingBasketParts() {
        shoppingBasketRepository.deleteAll();
    }

    @Override
    public int getReservedInventory(UUID productId) {
        return getReservedInventoryInShoppingBaskets(productId);
    }

    @Override
    public int useGetAvailableInventory(UUID productId) {
        return usableInventoryManagementService.useGetAvailableInventory(productId);
    }

    @Override
    public void deleteShoppingBasketFromClient(UUID clientId) {
      Client client;
        if(clientRepository.existsById(clientId)){
            client = clientRepository.findById(clientId).get();
            shoppingBasketRepository.deleteByClient(client);
        }
    }

    @Override
    public void deleteStorageUnits() {
        usableInventoryManagementService.deleteStorageUnits();
    }

    @Override
    public void updateShoppingBasketPart(UUID uuid, int newProductAmount) {
       shoppingBasketPartRepository.updateProductQuantityById(newProductAmount, uuid);
    }

    @Override
    public UUID getNewOrExistingShoppingBasketId(UUID clientId) {
        return getNewOrExistingShoppingBasket(clientRepository.findById(clientId).get().getEmail()).getId();
    }

    @Override
    public void useRemoveProductFromShoppingBasket(Email clientEmail, UUID productId, int excessToBeRemovedProductQuantity) {
        removeProductFromShoppingBasket(clientEmail, productId, excessToBeRemovedProductQuantity);
    }

    public ShoppingBasket findById(UUID shoppingBasketId) {
        return shoppingBasketRepository.findById(shoppingBasketId).get();
    }

    public boolean existsByShoppingBasketId(UUID shoppingBasketId) {
        return shoppingBasketRepository.existsById(shoppingBasketId);
    }
}
