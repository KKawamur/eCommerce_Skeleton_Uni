package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ShoppingBasketPartRepository extends CrudRepository<ShoppingBasketPart, UUID> {
    @Transactional
    @Modifying
    @Query("update ShoppingBasketPart s set s.quantity = ?1 where s.id = ?2")
    int updateProductQuantityById(@NonNull Integer quantity, @NonNull UUID id);
}
