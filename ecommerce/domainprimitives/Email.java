package thkoeln.archilab.ecommerce.domainprimitives;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@EqualsAndHashCode
@ToString
public class Email implements EmailType {
    String email;

    @Override
    public String toString() {
        return email;
    }

    @Override
    public EmailType sameIdentifyerDifferentDomain(String domainString) {
        checkStringIsNull(domainString);
        String[] splitEmail = email.split("@");
        return of(splitEmail[0] + "@" + domainString);
    }

    @Override
    public EmailType sameDomainDifferentIdentifyer(String identifyerString) {
        checkStringIsNull(identifyerString);
        String[] splitEmail = email.split("@");
        return of(identifyerString + "@" + splitEmail[1]);
    }

    public static EmailType of( String emailAsString ){
        checkStringIsNull(emailAsString);
        if((emailAsString.contains(" ") || emailAsString.contains("..")) || !emailAsString.contains("@"))
            throw new ShopException("Email contains invalid Characters!");

        String[] emailParts = emailAsString.split("@");
        if (emailParts.length == 2 && emailParts[0].matches("[a-zA-Z0-9.]+") && emailParts[1].length() >= 4){
                String postAtSubstring = emailParts[1].substring(emailParts[1].length()-5);
            if (postAtSubstring.contains(".de") || postAtSubstring.contains(".at") || postAtSubstring.contains(".ch")
            || postAtSubstring.contains(".com") || postAtSubstring.contains(".org")) {
                return new Email(emailAsString);
            }
        }
        throw new ShopException("Email is invalid!");
    }

    private static void checkStringIsNull(String string){
        if (string == null)
            throw new ShopException("Invalid input string. Input string is null!");
    }

    private Email(String emailString) {
        email = emailString;
    }

    protected Email(){}
}
