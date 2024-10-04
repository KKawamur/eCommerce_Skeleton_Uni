package thkoeln.archilab.ecommerce.solution.product.domain;

import lombok.*;
import thkoeln.archilab.ecommerce.domainprimitives.Money;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class Product {
    @Id
    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String description;

    private Float size;
    private Money purchasePrice, sellPrice;
}
