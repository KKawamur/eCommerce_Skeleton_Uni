package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingBasketPartDTO {
    private UUID productId;
    private Integer quantity;
}
