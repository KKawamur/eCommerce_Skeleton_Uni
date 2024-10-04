package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import thkoeln.archilab.ecommerce.domainprimitives.Money;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.*;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import java.util.ArrayList;
import java.util.List;

public class ShoppingBasketDTOMapper {
    private final ModelMapper modelMapper;

    public ShoppingBasketDTOMapper(){
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    public ShoppingBasketDTO shoppingBasketToDTO(ShoppingBasket shoppingBasket){
        ShoppingBasketDTO shoppingBasketDTO = new ShoppingBasketDTO();
        shoppingBasketDTO.setId(shoppingBasket.getId());

        MoneyType totalSellPrice = getShoppingBasketMoneyValue(shoppingBasket);
        float amount = totalSellPrice.getAmount();
        String currency = totalSellPrice.getCurrency();
        MoneyDTO moneyDTO = new MoneyDTO(amount, currency);
        shoppingBasketDTO.setTotalSellPrice(moneyDTO.getValue() + " " + moneyDTO.getCurrency());
        ShoppingBasketPartDTO[] shoppingBasketPartDTOs = getShoppingBasketPartDTOs(shoppingBasket);
        shoppingBasketDTO.setShoppingBasketParts(shoppingBasketPartDTOs);

        return shoppingBasketDTO;
    }

    public ShoppingBasketPart shoppingBasketPartDTOToShoppingBasketPart(ShoppingBasketPartDTO shoppingBasketPartDTO){
        ShoppingBasketPart shoppingBasketPart = new ShoppingBasketPart();
        modelMapper.map(shoppingBasketPartDTO, shoppingBasketPart);
        return shoppingBasketPart;
    }

    private MoneyType getShoppingBasketMoneyValue(ShoppingBasket shoppingBasket){
        float shoppingBasketValue = 0;
        String currency = "EUR";
        if(!shoppingBasket.getShoppingBasketParts().isEmpty()){
            for (ShoppingBasketPart shoppingBasketPart : shoppingBasket.getShoppingBasketParts()){
                shoppingBasketValue += shoppingBasketPart.getProduct().getSellPrice().getAmount() * shoppingBasketPart.getQuantity();
                currency = shoppingBasketPart.getProduct().getSellPrice().getCurrency();
            }
        }
        return Money.of(shoppingBasketValue, currency);
    }

    private ShoppingBasketPartDTO[] getShoppingBasketPartDTOs (ShoppingBasket shoppingBasket){
        List<ShoppingBasketPartDTO> shoppingBasketPartDTOs = new ArrayList<>();
            for (ShoppingBasketPart shoppingBasketPart : shoppingBasket.getShoppingBasketParts()){
                ShoppingBasketPartDTO shoppingBasketPartDTO = new ShoppingBasketPartDTO(shoppingBasketPart.getProduct().getId(), shoppingBasketPart.getQuantity());
                shoppingBasketPartDTOs.add(shoppingBasketPartDTO);
            }
            ShoppingBasketPartDTO[] returnArray = new ShoppingBasketPartDTO[shoppingBasketPartDTOs.size()];
        shoppingBasketPartDTOs.toArray(returnArray);
        return returnArray;
    }
}
