package thkoeln.archilab.ecommerce.usecases.masterdata;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import thkoeln.archilab.ecommerce.usecases.*;

@Component
@Slf4j
@Profile("!test")
@SuppressWarnings("PMD")
public class StartupListener implements ApplicationListener<ContextRefreshedEvent>  {
    private ProductAndInventoryMasterDataInitializer productAndInventoryMasterDataInitializer;
    private ClientMasterDataInitializer clientMasterDataInitializer;

    private ClientRegistrationUseCases clientRegistrationUseCases;
    private ProductCatalogUseCases productCatalogUseCases;
    private StorageUnitUseCases storageUnitUseCases;
    private Purgatory purgatory;

    @Autowired
    public StartupListener( ClientRegistrationUseCases clientRegistrationUseCases,
                            ProductCatalogUseCases productCatalogUseCases,
                            StorageUnitUseCases storageUnitUseCases,
                            Purgatory purgatory ) {
        this.clientRegistrationUseCases = clientRegistrationUseCases;
        this.productCatalogUseCases = productCatalogUseCases;
        this.storageUnitUseCases = storageUnitUseCases;
        this.purgatory = purgatory;
        productAndInventoryMasterDataInitializer = new ProductAndInventoryMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases );
        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
    }

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ) {
        log.info( "StartupListener initializing master data ..." );
        purgatory.deleteEverything();
        clientMasterDataInitializer = new ClientMasterDataInitializer( clientRegistrationUseCases );
        clientMasterDataInitializer.registerAllClients();

        productAndInventoryMasterDataInitializer = new ProductAndInventoryMasterDataInitializer(
                productCatalogUseCases, storageUnitUseCases );
        productAndInventoryMasterDataInitializer.addAllProducts();
        productAndInventoryMasterDataInitializer.addAllStorageUnits();
        productAndInventoryMasterDataInitializer.addAllInventory();
    }
}
