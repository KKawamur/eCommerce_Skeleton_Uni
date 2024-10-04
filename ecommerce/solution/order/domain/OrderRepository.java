package thkoeln.archilab.ecommerce.solution.order.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import thkoeln.archilab.ecommerce.domainprimitives.Email;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    List<Order> findMultipleByClientEmail(@NonNull Email email);

    List<Order> findByOrderPartsProductId(@NonNull UUID id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderParts WHERE o.id IN :orderIds")
    List<Order> findOrdersWithParts(@Param("orderIds") List<UUID> orderIds);

    @Query("SELECT o FROM Order o JOIN FETCH o.orderParts WHERE o.id = :orderId")
    Order findByIdWithOrderParts(@Param("orderId") UUID orderId);
}
