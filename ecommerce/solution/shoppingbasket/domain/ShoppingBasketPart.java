package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;
import thkoeln.archilab.ecommerce.solution.product.domain.Product;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Getter
@Setter
@Entity
@ToString
public class ShoppingBasketPart extends AbstractEntity {
    @OneToOne
    private Product product;

    private Integer quantity;
}
