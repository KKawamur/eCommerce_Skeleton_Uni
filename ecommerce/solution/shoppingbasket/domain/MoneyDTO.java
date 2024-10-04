package thkoeln.archilab.ecommerce.solution.shoppingbasket.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.ShopException;

@Getter
@Setter
public class MoneyDTO {
    String value;
    String currency;

    public MoneyDTO(Float value, String currency){
        if (value == null || currency == null)
            throw new ShopException("Amount or currency cannot be null!");
        if (value < 0)
            throw new ShopException("Amount cannot be smaller than 0!");
        if (!(currency.equals("EUR")|| currency.equals("CHF")))
            throw new ShopException("Currency is not in Euros or Swiss Franks!");
        if (currency.equals("EUR"))
            this.currency = "€";
        this.value = String.format( "%.2f", value);
    }

    protected MoneyDTO() {}
}
