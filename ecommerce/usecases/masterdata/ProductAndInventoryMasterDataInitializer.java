package thkoeln.archilab.ecommerce.usecases.masterdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.usecases.ProductCatalogUseCases;
import thkoeln.archilab.ecommerce.usecases.StorageUnitUseCases;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.*;

/**
 * This is a test helper class that initializes and registers products (but without inventory)
 * in the system, using the given interface(s).
 */
@Component
@Slf4j
@SuppressWarnings("PMD")
public class ProductAndInventoryMasterDataInitializer {

    private ProductCatalogUseCases productCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;
    private static Random random = new Random();

    public static final String EUR = "EUR";

    @Autowired
    public ProductAndInventoryMasterDataInitializer( ProductCatalogUseCases productCatalogUseCases,
                                                                StorageUnitUseCases storageUnitUseCases ) {
        this.productCatalogUseCases = productCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
    }

    // Contains initialization data for product instances, and their inventory in the storage unit
    // The data is structured as follows:
    // { UUID, name, description, weight, inPrice, outPrice, storage units }
    // where "storage units" is a string with numbers between 0 and 9, representing the storage units
    // where the product is stored.
    //
    // There is a total of PRODUCT_NUMOF products in the data. The index is (for simplicity)
    // noted as a number at the beginning of the product name, like "0-TCD-34 v2.1" or "1-EFG-56".
    //
    // With regard to where the inventory is stored, the following rules apply:
    // - products 0, 1, 2, and 3 have fixed quantities of 0, 10, 20, and 30, respectively. For simplicity,
    //   they are ONLY available in storage unit 0.

    public static final int PRODUCT_NUMOF = 15;
    public static final Object[][] PRODUCT_DATA = new Object[][]{
            {UUID.randomUUID(), "0-TCD-34 v2.1", "Universelles Verbindungsstück für den einfachen Hausgebrauch bei der Schnellmontage",
                    1.5f, FactoryMethodInvoker.instantiateMoney( 5.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 10.0f, EUR ), "0"},
            {UUID.randomUUID(), "1-EFG-56", "Hochleistungsfähiger Kondensator für elektronische Schaltungen",
                    0.3f, FactoryMethodInvoker.instantiateMoney( 2.5f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 4.0f, EUR ), "0"},
            {UUID.randomUUID(), "2-MNP-89ff", "Langlebiger und robuster Motor für industrielle Anwendungen",
                    7.2f, FactoryMethodInvoker.instantiateMoney( 50.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 80.0f, EUR ), "0"},
            {UUID.randomUUID(), "3-Gh-25", "Kompakter und leichter Akku für mobile Geräte",
                    null, FactoryMethodInvoker.instantiateMoney( 6.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 8.0f, EUR ), "0"},
            {UUID.randomUUID(), "4-MultiBeast2", "Vielseitiger Adapter für verschiedene Steckertypen",
                    null, FactoryMethodInvoker.instantiateMoney( 0.6f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 1.0f, EUR ), "0"},
            {UUID.randomUUID(), "5-ABC-99 v4.2", "Leistungsstarker Prozessor für Computer und Server",
                    1.0f, FactoryMethodInvoker.instantiateMoney( 150.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 250.0f, EUR ), "0"},
            {UUID.randomUUID(), "6-Stuko22", "Ersatzteil Spitze für Präzisionswerkzeug zum Löten und Schrauben",
                    null, FactoryMethodInvoker.instantiateMoney( 0.3f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 0.5f, EUR ), "0"},
            {UUID.randomUUID(), "7-Btt2-Ah67", "Kraftstoffeffiziente und umweltfreundliche Hochleistungsbatterie",
                    6.0f, FactoryMethodInvoker.instantiateMoney( 80.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 120.0f, EUR ), "123"},
            {UUID.randomUUID(), "8-JKL-67", "Wasserdichtes Gehäuse",
                    3.0f, FactoryMethodInvoker.instantiateMoney( 1.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 1.2f, EUR ), "467"},
            {UUID.randomUUID(), "9-MNO-55-33", "Modulares Netzteil für flexible Stromversorgung",
                    5.5f, FactoryMethodInvoker.instantiateMoney( 25.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 45.0f, EUR ), "578"},
            {UUID.randomUUID(), "10-PQR-80", "Effizienter Kühler für verbesserte Wärmeableitung",
                    4.0f, FactoryMethodInvoker.instantiateMoney( 20.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 35.0f, EUR ), "567"},
            {UUID.randomUUID(), "11-STU-11 Ld", "Hochwertiger Grafikchip für leistungsstarke PCs",
                    null, FactoryMethodInvoker.instantiateMoney( 200.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 350.0f, EUR ), "478"},
            {UUID.randomUUID(), "12-VWX-90 FastWupps", "Schnellladegerät für eine Vielzahl von Geräten",
                    null, FactoryMethodInvoker.instantiateMoney( 15.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 25.0f, EUR ), "5"},
            {UUID.randomUUID(), "13-YZZ-22 v1.8", "Leichter und stabiler Rahmen aus Aluminium",
                    3.5f, FactoryMethodInvoker.instantiateMoney( 60.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 100.0f, EUR ), "78"},
            {UUID.randomUUID(), "14-Nosedive", "Klammer zum Verschließen der Nase beim Tauchen",
                    5.0f, FactoryMethodInvoker.instantiateMoney( 2.0f, EUR ),
                    FactoryMethodInvoker.instantiateMoney( 5.0f, EUR ), "457"}
    };


    /**
     * Used for creating invalid input data for the products in the tests.
     */
    public enum InvalidReason {
        NULL, EMPTY;

        public Object getInvalidValue( Object originalValue ) {
            switch (this) {
                case NULL:
                    return null;
                case EMPTY:
                    return "";
                default:
                    return null;
            }
        }
    }

    public void addAllProducts() {
        log.info( "Adding all products to the catalog." );
        for ( Object[] productData : PRODUCT_DATA ) {
            addProductDataToCatalog( productData );
        }
    }

    public void addProductDataToCatalog( Object[] productData ) {
        productCatalogUseCases.addProductToCatalog( (UUID) productData[0], (String) productData[1], (String) productData[2],
                (Float) productData[3], (MoneyType) productData[4], (MoneyType) productData[5] );
        log.info( "... added " + productData[1] + " with id " + productData[0] + " to the catalog." );
    }

    public Object[] getProductDataInvalidAtIndex( int index, InvalidReason reason ) {
        Object[] productData = PRODUCT_DATA[1];
        Object[] productDataInvalid = new Object[productData.length];
        System.arraycopy( productData, 0, productDataInvalid, 0, productData.length );
        productDataInvalid[index] = productData[index].getClass().cast(
                reason.getInvalidValue( productData[index] ) );
        return productDataInvalid;
    }


    // These home addresss are used for the storage units. The storage unit name will equal
    // the zip code of the home address. Their index number will be visible in the house number.
    // The storage units are used as such:
    // - storage unit 0 is holds all products 0 - 6, and is used for all tests where multiple
    //   delivery packages are irrelevant.
    // - storage units 1 - 3 are used for the proximity tests, where you can deliver products 7 to
    //   to a client from the closest storage unit.
    // - storage units 4 - 8 are used for the tests where you need to deliver products 8 - 14 in
    //   the most cost-efficient way, as multiple delivery packages.
    // - storage unit 9 is empty.
    public final static int STORAGE_UNIT_NUMOF = 10;
    public final static HomeAddressType[] STORAGE_UNIT_ADDRESS = new HomeAddressType[]{
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Stapelallee 0", "Potsdam",
                    FactoryMethodInvoker.instantiateZipCode( "14476" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Lagerhausstr. 1", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02345" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Speicherplatz 2", "Viertelstadt",
                    FactoryMethodInvoker.instantiateZipCode( "02313" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Ablageweg 3", "Reichswürgen",
                    FactoryMethodInvoker.instantiateZipCode( "44923" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Paketstellenallee 4", "Düsseldorf",
                    FactoryMethodInvoker.instantiateZipCode( "40588" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Kaputte-Sachen-Straße 5", "Düren",
                    FactoryMethodInvoker.instantiateZipCode( "52355" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Aufbewahrungsweg 6", "Viernheim",
                    FactoryMethodInvoker.instantiateZipCode( "68519" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Paketallee 7", "Baden-Baden",
                    FactoryMethodInvoker.instantiateZipCode( "76532" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Sendenstr. 8", "Senden",
                    FactoryMethodInvoker.instantiateZipCode( "89250" ) ),
            FactoryMethodInvoker.instantiateHomeAddress(
                    "Schickweg 9", "Hohenroth",
                    FactoryMethodInvoker.instantiateZipCode( "97618" ) )
    };
    public final static UUID[] STORAGE_UNIT_ID = new UUID[STORAGE_UNIT_NUMOF];

    public void addAllStorageUnits() {
        log.info( "Adding all storage units." );
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) {
            STORAGE_UNIT_ID[i] = storageUnitUseCases.addNewStorageUnit(
                    STORAGE_UNIT_ADDRESS[i], STORAGE_UNIT_ADDRESS[i].getZipCode().toString() );
            log.info( "... added storage unit Nr. " + i + " at zip code " + STORAGE_UNIT_ADDRESS[i].getZipCode()
                    + " with id " + STORAGE_UNIT_ID[i] );
        }
    }


    // These data structures contain the inventory of the products in the storage units.
    // PRODUCT_INVENTORY is a map product id -> Integer[STORAGE_UNIT_NUMOF].
    // The Integer[STORAGE_UNIT_NUMOF] contains the inventory of the product in each of
    // the storage units.
    //
    // The following rules apply:
    // - product 0 is out of inventory
    // - product 1 / 2 / 3 have fixed quantities of 10 / 20 / 30 respectively, all ONLY in storage unit 0
    // - product 4 / 5 / 6 have a random inventory between 30 and 130, also all ONLY in storage unit 0
    //   (these are the products used for tests on how to add and remove inventory)
    // - the others have a random inventory between 30 and 130, distributed over several
    //   storage units. Here we follow this convention for simplicity:
    //   - Assume that the product is available in <n> storage units. Then the first <n-1> storage units
    //     in the list (in ascending sequence) contain 3, and all the remaining inventory is in the
    //     last storage unit.

    public static final Map<UUID, Integer[]> PRODUCT_INVENTORY = new HashMap<>();

    static {
        // products 0, 1, 2, and 3 have fixed quantities of 0, 10, 20, and 30.
        PRODUCT_INVENTORY.put( (UUID) PRODUCT_DATA[0][0],
                getInventoryDistribution( 0, (String) PRODUCT_DATA[0][6] ) );
        PRODUCT_INVENTORY.put( (UUID) PRODUCT_DATA[1][0],
                getInventoryDistribution( 10, (String) PRODUCT_DATA[1][6] ) );
        PRODUCT_INVENTORY.put( (UUID) PRODUCT_DATA[2][0],
                getInventoryDistribution( 20, (String) PRODUCT_DATA[2][6] ) );
        PRODUCT_INVENTORY.put( (UUID) PRODUCT_DATA[3][0],
                getInventoryDistribution( 30, (String) PRODUCT_DATA[3][6] ) );

        // The other products have a random inventory between 30 and 130,
        for ( int i = 4; i < PRODUCT_NUMOF; i++ ) {
            Integer totalNumber = random.nextInt( 100 ) + 30;
            Integer[] inventoryInStorageUnits =
                    getInventoryDistribution( totalNumber, (String) PRODUCT_DATA[i][6] );
            PRODUCT_INVENTORY.put( (UUID) PRODUCT_DATA[i][0], inventoryInStorageUnits );
        }
    }


    /**
     * This method creates a random inventory distribution for the given product.
     *
     * @param totalQuantity - the total number of products in the storage units
     * @param zeroToNine  - a string with numbers between 0 and 9, representing the storage units
     * @return an Integer array with the inventory distribution for the product, according to
     * the rules described above.
     */
    private static Integer[] getInventoryDistribution( Integer totalQuantity, String zeroToNine ) {
        Integer[] inventoryInStorageUnits = new Integer[STORAGE_UNIT_NUMOF];
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) inventoryInStorageUnits[i] = 0;
        TreeSet<Integer> storageUnitIndices = getStorageUnitIndices( zeroToNine );
        int numOfIndices = storageUnitIndices.size();
        int currentIndexNumber = 0;
        int currentQuantity = totalQuantity;
        for ( Integer storageUnitIndex : storageUnitIndices ) {
            currentIndexNumber++;
            if ( currentIndexNumber < numOfIndices ) {
                inventoryInStorageUnits[storageUnitIndex] = 3;
                currentQuantity -= 3;
            } else {
                inventoryInStorageUnits[storageUnitIndex] = currentQuantity;
            }
        }
        return inventoryInStorageUnits;
    }


    private static TreeSet<Integer> getStorageUnitIndices( String zeroToNine ) {
        TreeSet<Integer> storageUnitIndices = new TreeSet<>();
        for ( int i = 0; i < zeroToNine.length(); i++ ) {
            storageUnitIndices.add( Integer.parseInt( zeroToNine.substring( i, i + 1 ) ) );
        }
        return storageUnitIndices;
    }


    public void addAllInventory() {
        log.info( "Adding all inventorys to the storage units." );
        for ( Object[] productData : PRODUCT_DATA ) {
            Integer[] inventoryInStorageUnits =
                    PRODUCT_INVENTORY.get( productData[0] );
            for ( int iStorageUnit = 0; iStorageUnit < STORAGE_UNIT_NUMOF; iStorageUnit++ ) {
                if ( inventoryInStorageUnits[iStorageUnit] > 0 )
                    storageUnitUseCases.addToInventory(
                            STORAGE_UNIT_ID[iStorageUnit], (UUID) productData[0],
                            inventoryInStorageUnits[iStorageUnit] );
            }
        }
    }


    public Integer findStorageUnitIndex( UUID storageUnitId ) {
        for ( int i = 0; i < STORAGE_UNIT_NUMOF; i++ ) {
            if ( STORAGE_UNIT_ID[i].equals( storageUnitId ) ) {
                return i;
            }
        }
        return null;
    }

    /**
     * This method is used to get the total price of a shopping basket, given as a map of product id -> quantity.
     * @param quantityProductMap
     * @return the total price of the shopping basket, as Float
     */
    public Float getTotalSellPrice( Map<UUID, Integer> quantityProductMap ) {
        Float totalPrice = 0.0f;
        for ( UUID productId : quantityProductMap.keySet() ) {
            Integer quantity = quantityProductMap.get( productId );
            int productIndex = -1;
            for ( int i = 0; i < PRODUCT_NUMOF; i++ ) {
                if ( PRODUCT_DATA[i][0].equals( productId ) ) {
                    productIndex = i;
                    break;
                }
            }
            totalPrice += ((MoneyType) PRODUCT_DATA[productIndex][5]).getAmount() * quantity;
        }
        return totalPrice;
    }
}
