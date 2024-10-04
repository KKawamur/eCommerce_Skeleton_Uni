package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;

import java.util.List;
import java.util.UUID;

public interface ShoppingBasketRepository extends CrudRepository <ShoppingBasket, UUID> {
    ShoppingBasket findByClientEmail(@NonNull Email email);

    List<ShoppingBasket> findMultipleByShoppingBasketPartsProductId(UUID productId);

    long deleteByClient(@NonNull Client client);

    boolean existsByClientClientId(@NonNull UUID clientId);

    ShoppingBasket findByClientClientId(@NonNull UUID clientId);
}
