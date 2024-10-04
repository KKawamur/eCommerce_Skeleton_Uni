package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.ZipCodeType;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@EqualsAndHashCode
public class HomeAddress implements HomeAddressType {
    String street;
    String city;
    ZipCode zipCode;

    @Override
    public String getStreet() {
        return street;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public ZipCodeType getZipCode() {
        return zipCode;
    }

    public static HomeAddressType of( String street, String city, ZipCodeType zipCode ){
        if(street == null || city == null || zipCode == null)
            throw new ShopException("Street, City or zip code cannot be null!");
        if (street.isEmpty() || city.isEmpty())
            throw new ShopException("Street or city cannot be empty!");
        return new HomeAddress(street, city, zipCode);
    }

    private HomeAddress(String street, String city, ZipCodeType zipCode){
        this.street = street;
        this.city = city;
        this.zipCode = (ZipCode) zipCode;
    }

    protected HomeAddress(){}
}
