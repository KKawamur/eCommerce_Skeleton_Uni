package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePartRepository;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageRepository;
import thkoeln.archilab.ecommerce.solution.order.application.OrderService;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderPart;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.UsableDeliveryPackage;
import thkoeln.archilab.ecommerce.solution.storageunit.application.StorageUnitService;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.InventoryLevel;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnitRepository;
import thkoeln.archilab.ecommerce.usecases.DeliveryPackageUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import java.util.*;

@Service
public class DeliveryPackageService extends UsableDeliveryPackage implements DeliveryPackageUseCases {
    DeliveryPackageRepository deliveryPackageRepository;
    StorageUnitRepository storageUnitRepository;
    StorageUnitService storageUnitService;
    OrderRepository orderRepository;
    private final DeliveryPackagePartRepository deliveryPackagePartRepository;
    private OrderService orderService;

    @Autowired
    public DeliveryPackageService(DeliveryPackageRepository deliveryPackageRepository, StorageUnitRepository storageUnitRepository,
                                  OrderRepository orderRepository,
                                  DeliveryPackagePartRepository deliveryPackagePartRepository,
                                  OrderService orderService,
                                  StorageUnitService storageUnitService
                                  ){
        this.deliveryPackageRepository = deliveryPackageRepository;
        this.storageUnitRepository = storageUnitRepository;
        this.orderRepository = orderRepository;
        this.deliveryPackagePartRepository = deliveryPackagePartRepository;
        this.orderService = orderService;
        this.storageUnitService = storageUnitService;
    }

    @Override
    public List<UUID> getContributingStorageUnitsForOrder(UUID orderId) {
        isUuidNull(orderId);
        doesOrderExist(orderId);
        List<UUID> contributingStorageUnits = new ArrayList<>();

        Set<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderIdWithParts(orderId);
        for (DeliveryPackage deliveryPackage : deliveryPackages){
            if(!contributingStorageUnits.contains(deliveryPackage.getStorageUnit().getId())) {
                contributingStorageUnits.add(deliveryPackage.getStorageUnit().getId());
            }
        }
        return contributingStorageUnits;
    }

    @Override
    public Map<UUID, Integer> getDeliveryPackageForOrderAndStorageUnit(UUID orderId, UUID storageUnitId) {
        isUuidNull(orderId);
        doesOrderExist(orderId);
        isUuidNull(storageUnitId);
        doesStorageUnitExist(storageUnitId);

        System.out.println("MAKING MAP CONTAINING PRODUCTS ON PRODUCT AMOUNT IN DELIVERY FOR ORDER: " + orderId + " FROM STORAGE UNIT: " +storageUnitId);
        Map<UUID, Integer> products = new HashMap<>();
        List <DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderIdAndStorageUnitId(orderId, storageUnitId);
        for (DeliveryPackage deliveryPackage: deliveryPackages){
            System.out.println("From delivery Package: " +deliveryPackage.getId());
            List<DeliveryPackagePart> deliveryPackageParts = deliveryPackage.getDeliveryPackageParts();
            for (DeliveryPackagePart deliveryPackagePart : deliveryPackageParts){
                products.put(deliveryPackagePart.getProduct().getId(), deliveryPackagePart.getAmount());
                System.out.println("Adding Product "+ deliveryPackagePart.getProduct().getId() + " with amount: " + deliveryPackagePart.getAmount() + " to map");
            }
        }
        System.out.println("RETURNING MAP");
        return products;
    }

    @Override
    public void deleteAllDeliveryPackages() {
        deliveryPackageRepository.deleteAll();
    }

    public List<DeliveryPackage> createDeliveryPackagesForOrder(UUID orderId){
        Map<UUID, Integer> wantedProducts = new HashMap<>();
        Order order = orderRepository.findByIdWithOrderParts(orderId);
        List<DeliveryPackagePart> deliveryPackageParts = new ArrayList<>();
        List<DeliveryPackage> deliveryPackages = new ArrayList<>();
        for (OrderPart orderPart : order.getOrderParts()){
            wantedProducts.put(orderPart.getProduct().getId(), orderPart.getProductAmount());
        }

        while (!wantedProducts.isEmpty()) {
            Map<UUID, Map<UUID, Integer>> storageUnitsWithEnoughProducts = getStorageUnitsWithEnoughProductsInInventory(wantedProducts);
            AbstractMap.SimpleEntry<UUID, Map<UUID, Integer>> nearestStorageUnitWithMostWantedProducts = getNearestStorageUnitWithMostProducts(storageUnitsWithEnoughProducts, orderId);

            for (OrderPart orderPart : order.getOrderParts()) {
                for (Map.Entry<UUID, Integer> nearestInventoryLevels : nearestStorageUnitWithMostWantedProducts.getValue().entrySet()) {
                    if (nearestInventoryLevels.getKey().equals(orderPart.getProduct().getId())) {
                        DeliveryPackagePart deliveryPackagePart = new DeliveryPackagePart();
                        deliveryPackagePart.setAmount(orderPart.getProductAmount());
                        deliveryPackagePart.setProduct(orderPart.getProduct());
                        deliveryPackagePartRepository.save(deliveryPackagePart);
                        deliveryPackageParts.add(deliveryPackagePart);

                        storageUnitService.removeFromInventory(nearestStorageUnitWithMostWantedProducts.getKey(), deliveryPackagePart.getProduct().getId(), deliveryPackagePart.getAmount());
                        wantedProducts.remove(nearestInventoryLevels.getKey());
                    }
                }
            }
            DeliveryPackage deliveryPackage = new DeliveryPackage();
            deliveryPackage.setOrder(orderRepository.findById(orderId).get());
            deliveryPackage.setHomeAddress(order.getClient().getHomeAddress());
            deliveryPackage.setStorageUnit(storageUnitRepository.findById(nearestStorageUnitWithMostWantedProducts.getKey()).get());
            deliveryPackage.setDeliveryPackageParts(deliveryPackageParts);
            deliveryPackageRepository.save(deliveryPackage);
            deliveryPackages.add(deliveryPackage);
            deliveryPackageParts = new ArrayList<>();
        }
        return deliveryPackages;
    }

    private Map<UUID, Map<UUID,Integer>> getStorageUnitsWithEnoughProductsInInventory(Map<UUID,Integer> wantedProductIdsAndAmount){
        System.out.println("MAKING MAP OF STORAGE UNITS CONTAINING PRODUCTS IN ID LIST");
        Map<UUID, Map<UUID, Integer>> storageUnitsWithSetOfProducts = new HashMap<>();
        for (Map.Entry<UUID, Integer> entry : wantedProductIdsAndAmount.entrySet()){
            System.out.println("For Product: " + entry.getKey() + " ");
            for (StorageUnit storageUnit : storageUnitRepository.findByInventoryLevelListProductId(entry.getKey())){
                for(InventoryLevel inventoryLevel : storageUnit.getInventoryLevelList()){
                    if(inventoryLevel.getProduct().getId().equals(entry.getKey()) && inventoryLevel.getProductQuantity() >= entry.getValue()) {
                        System.out.println("Product is in Inventory Level: " + inventoryLevel.getId() + " and with enough Quantity: " + inventoryLevel.getProductQuantity());
                        if (storageUnitsWithSetOfProducts.containsKey(storageUnit.getId())) {
                            Map<UUID, Integer> productIdsAndAmountOfStorageUnit = storageUnitsWithSetOfProducts.get(storageUnit.getId());
                            productIdsAndAmountOfStorageUnit.put(inventoryLevel.getProduct().getId(), inventoryLevel.getProductQuantity());
                            storageUnitsWithSetOfProducts.put(storageUnit.getId(), productIdsAndAmountOfStorageUnit);
                            System.out.println("added Product to List of Existing Storage Unit Key: " + storageUnit.getId());
                        } else {
                            Map<UUID, Integer> productIdsAndAmountOfStorageUnit = new HashMap<>();
                            productIdsAndAmountOfStorageUnit.put(inventoryLevel.getProduct().getId(), inventoryLevel.getProductQuantity());
                            storageUnitsWithSetOfProducts.put(storageUnit.getId(), productIdsAndAmountOfStorageUnit);
                            System.out.println("added Product to List of new Storage Unit Key: " + storageUnit.getId());
                        }
                    } else if(!inventoryLevel.getProduct().getId().equals(entry.getKey())) {
                        System.out.println("Product is not in Inventory Level: " + inventoryLevel.getId());
                    } else if(inventoryLevel.getProduct().getId().equals(entry.getKey()) && !(inventoryLevel.getProductQuantity() >= entry.getValue())){
                        System.out.println("Product is in Inventory Level: " + inventoryLevel + " but Quantity is: " + inventoryLevel.getProductQuantity());
                    }
                }
            }
        }
        System.out.println("RETURNING MAP");
        return storageUnitsWithSetOfProducts;
    }

    private AbstractMap.SimpleEntry<UUID, Map<UUID, Integer>> getNearestStorageUnitWithMostProducts(Map<UUID,Map<UUID, Integer>> storageUnitsWithSetOfProducts, UUID orderId){
        System.out.println("GETTING NEAREST STORAGE UNITS WITH MOST PRODUCTS FROM ORDER: "+ orderId);
        int longestProductList = 0;
        AbstractMap.SimpleEntry<UUID, Map<UUID, Integer>>nearestStorageUnit = new AbstractMap.SimpleEntry<>(null,null);
        Map<UUID, Map<UUID, Integer>> storageUnitsWithMostProducts = new HashMap<>();
        int iteration = 0;
        while (nearestStorageUnit.getKey() == null && iteration < storageUnitsWithSetOfProducts.size()) {
            System.out.println("Iteration: " + iteration + " nearestStorageId: " + nearestStorageUnit);
            System.out.println("Getting length of longest List of Products Contained in Storage Units");
            for (UUID storageUnitId : storageUnitsWithSetOfProducts.keySet()) {
                if (longestProductList < storageUnitsWithSetOfProducts.get(storageUnitId).size()) {
                    longestProductList = storageUnitsWithSetOfProducts.get(storageUnitId).size() - iteration;
                }
            }
            System.out.println("Longest Product List is: " + longestProductList);

            System.out.println("Getting all Storages with Product List Length of: " + longestProductList);
            for(Map.Entry<UUID, Map<UUID, Integer>> storageUnit : storageUnitsWithSetOfProducts.entrySet()){
                if(storageUnit.getValue().size() == longestProductList){
                    storageUnitsWithMostProducts.put(storageUnit.getKey(),storageUnit.getValue());
                    System.out.println("Storage Unit: " + storageUnit.getKey() + " has Product List Length of: " + longestProductList);
                }
            }
            nearestStorageUnit = getNearestStorageUnit(storageUnitsWithMostProducts, nearestStorageUnit, orderId);

            if (nearestStorageUnit.getKey() == null){
                System.out.println("Nearest Storage Unit Id is null. Beginning next iteration");
            }
        }
        return nearestStorageUnit;
    }

    private AbstractMap.SimpleEntry<UUID, Map<UUID, Integer>> getNearestStorageUnit(
            Map<UUID, Map<UUID, Integer>> storageUnitsWithMostProducts,
            AbstractMap.SimpleEntry<UUID, Map<UUID, Integer>> nearestCurrentUnit,
            UUID orderId){
        for (Map.Entry<UUID, Map<UUID, Integer>> storageUnit : storageUnitsWithMostProducts.entrySet()){
            if(nearestCurrentUnit.getKey()== null){
                nearestCurrentUnit = new AbstractMap.SimpleEntry<>(storageUnit.getKey(), storageUnit.getValue());
                System.out.println("Storage Unit: " + storageUnit.getKey() + " was the first to be checked, so is the closest for now");
            } else {
                ZipCodeType nearestStorageIdZipCode = storageUnitRepository.findById(nearestCurrentUnit.getKey()).get().getHomeAddress().getZipCode();
                System.out.println("Zipcode of current nearest Storage Unit: " + nearestStorageIdZipCode);
                ZipCodeType nearestStorageCandidateIdZipCode = storageUnitRepository.findById(storageUnit.getKey()).get().getHomeAddress().getZipCode();
                System.out.println("Zipcode of potential nearest Storage Unit: " + nearestStorageCandidateIdZipCode);
                ZipCodeType clientZipCode = orderRepository.findById(orderId).get().getClient().getHomeAddress().getZipCode();
                System.out.println("Zipcode of Client: " +clientZipCode);

                int differenceNearestStorageUnitClient = nearestStorageIdZipCode.difference(clientZipCode);
                System.out.println("Difference between current nearest Storage Unit and Client: " +differenceNearestStorageUnitClient);
                int differenceThisStorageUnitToClient = nearestStorageCandidateIdZipCode.difference(clientZipCode);
                System.out.println("Difference between potential nearest Storage Unit and Client: " + differenceThisStorageUnitToClient);
                if(differenceThisStorageUnitToClient < differenceNearestStorageUnitClient){
                    System.out.println("Difference between potential nearest Storage Unit and Client was smaller than old nearest Storage Unit");
                    nearestCurrentUnit = new AbstractMap.SimpleEntry<>(storageUnit.getKey(), storageUnit.getValue());
                    System.out.println("New nearest Storage Id is: " + nearestCurrentUnit.getKey());
                }else{
                    System.out.println("Current nearest Storage Unit: " + nearestCurrentUnit.getKey() + " is still the closest");
                }
            }
        }
        return nearestCurrentUnit;
    }

    private void isUuidNull(UUID uuid){
        if(uuid == null)
            throw new ShopException("UUID cannot be null!");
    }

    private void doesOrderExist(UUID uuid){
        if(!orderRepository.existsById(uuid))
            throw new ShopException("Order does not exist!");
    }

    private void doesStorageUnitExist(UUID uuid){
        if(!storageUnitRepository.existsById(uuid))
            throw new ShopException("Storage unit does not exist!");
    }

    @Override
    public void deleteAllDeliveries() {
        deliveryPackagePartRepository.deleteAll();
        deliveryPackageRepository.deleteAll();
    }

    @Override
    public List useCreateDeliveryPackagesForOrder(UUID orderId) {
        return createDeliveryPackagesForOrder(orderId);
    }
}
