package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;
import thkoeln.archilab.ecommerce.solution.product.domain.Product;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class DeliveryPackagePart extends AbstractEntity {
    @ManyToOne
    private Product product;

    private int amount = 0;
}
