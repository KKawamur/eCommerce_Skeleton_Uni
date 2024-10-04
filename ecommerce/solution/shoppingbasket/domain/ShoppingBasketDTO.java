package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingBasketDTO {
    private UUID id;
    private String totalSellPrice;
    private ShoppingBasketPartDTO[] shoppingBasketParts;
}
