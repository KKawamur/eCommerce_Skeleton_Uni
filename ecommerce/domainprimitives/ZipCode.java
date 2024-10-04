package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import javax.persistence.Embeddable;

import static java.lang.Math.abs;

@Getter
@Embeddable
@EqualsAndHashCode
public class ZipCode implements ZipCodeType {
    String zipCode;

    @Override
    public String toString(){
        return zipCode;
    }

    @Override
    public int difference(ZipCodeType otherZipCode) {
        isZipCodeNull(otherZipCode);
        int difference = differenceInFirstDigit((ZipCode) otherZipCode);
        if (difference == 0)
            difference = differenceInOtherDigits((ZipCode) otherZipCode);
        return difference;
    }

    private int differenceInFirstDigit(ZipCode otherZipCode){
        int otherFirstDigit = otherZipCode.toString().charAt(0);
        int thisFirstDigit = zipCode.charAt(0);
        int difference = Math.min(abs(otherFirstDigit - thisFirstDigit - 10) % 10, abs(thisFirstDigit - otherFirstDigit - 10) % 10) * 10;
        if (difference == 90) difference = 10;
        return difference;
    }

    private int differenceInOtherDigits(ZipCode otherZipCode){
        String otherZipCodeString = otherZipCode.toString();
        int difference = 5;
        for (int i = 1; i < otherZipCodeString.length(); i++){
            if (otherZipCodeString.charAt(i) != zipCode.charAt(i)){
                return difference - i;
            }
        }
        return 0;
    }

    public static ZipCodeType of( String zipCodeAsString ){
        isZipCodeNull(zipCodeAsString);
        if (zipCodeAsString.length() == 5 && zipCodeAsString.matches("[0-9]+")){
            String lastFourDigits = zipCodeAsString.substring(1);
            if (!lastFourDigits.equals("0000"))
                return new ZipCode(zipCodeAsString);
        }
        throw new ShopException("Invalid zip code!");
    }

    private ZipCode(String zipCodeString){
        zipCode = zipCodeString;
    }

    protected ZipCode(){}

    private static void isZipCodeNull (ZipCodeType zipCode){
        if (zipCode == null)
            throw new ShopException("Zip code is null!");
    }

    private static void isZipCodeNull (String zipCode){
        if (zipCode == null)
            throw new ShopException("Zip code is null!");
    }
}
