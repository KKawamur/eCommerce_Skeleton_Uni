package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@ToString
public class ShoppingBasket extends AbstractEntity {
    @OneToOne
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ShoppingBasketPart> shoppingBasketParts = new ArrayList<>();
}
