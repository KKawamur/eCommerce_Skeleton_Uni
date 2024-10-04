package thkoeln.archilab.ecommerce.solution.order.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.solution.product.domain.Product;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OrderPart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderPartId;

    @OneToOne
    private Product product;

    private Integer productAmount;
}
