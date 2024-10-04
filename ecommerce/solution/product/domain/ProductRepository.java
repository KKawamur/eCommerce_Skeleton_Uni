package thkoeln.archilab.ecommerce.solution.product.domain;


import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProductRepository extends CrudRepository<Product, UUID> {
}
