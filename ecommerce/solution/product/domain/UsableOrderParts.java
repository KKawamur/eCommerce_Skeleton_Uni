package thkoeln.archilab.ecommerce.solution.product.domain;

import java.util.List;
import java.util.UUID;

public abstract class UsableOrderParts {
     public abstract List findListOfOrderPartsByProductId(UUID productId);
     public abstract void deleteAllOrderParts();
}
