package thkoeln.archilab.ecommerce.solution.storageunit.domain;//package thkoeln.archilab.ecommerce.solution.inventory.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;
import thkoeln.archilab.ecommerce.solution.product.domain.Product;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class InventoryLevel extends AbstractEntity {
    @ManyToOne
    private Product product;

    private Integer productQuantity = 0;

}

