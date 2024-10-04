package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.EqualsAndHashCode;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.MoneyType;

import javax.persistence.Embeddable;

@Embeddable
@EqualsAndHashCode
public class Money implements MoneyType {
    Float amount;
    String currency;
    @Override
    public Float getAmount() {
        return amount;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public MoneyType add(MoneyType otherMoney) {
        isMoneyTypeNull(otherMoney);
        isCurrencyNotEqual(otherMoney);
        return of(amount + otherMoney.getAmount(), currency);
    }

    @Override
    public MoneyType subtract(MoneyType otherMoney) {
        isMoneyTypeNull(otherMoney);
        isCurrencyNotEqual(otherMoney);
        if(otherMoney.getAmount() > amount)
            throw new ShopException("Cannot subtract a larger amount of Money from a smaller amount");
        return of(amount - otherMoney.getAmount(), currency);
    }

    @Override
    public MoneyType multiplyBy(int factor) {
        if (factor < 0)
            throw new ShopException("Cannot multiply money with a negative factor!");
        return of(amount * factor, currency);
    }

    @Override
    public boolean largerThan(MoneyType otherMoney) {
        isMoneyTypeNull(otherMoney);
        isCurrencyNotEqual(otherMoney);
        return (amount > otherMoney.getAmount());
    }

     public static MoneyType of( Float amount, String currency ){
        if (amount == null || currency == null)
            throw new ShopException("Amount or currency cannot be null!");
        if (amount < 0)
            throw new ShopException("Amount cannot be smaller than 0!");
        if (!(currency.equals("EUR")|| currency.equals("CHF")))
            throw new ShopException("Currency is not in Euros or Swiss Franks!");
        return new Money(amount, currency);
     }

     private Money(Float amount, String currency){
         this.amount = amount;
         this.currency = currency;
     }

    protected Money (){}

    private void isMoneyTypeNull(MoneyType moneyType){
        if (moneyType == null) throw new ShopException("Money type cannot be null!");
    }

    private void isCurrencyNotEqual(MoneyType moneyType){
        if (!(moneyType.getCurrency().equals(currency)))
            throw new ShopException(moneyType.getCurrency() + " is unequal to this money types currency!");
    }
}
