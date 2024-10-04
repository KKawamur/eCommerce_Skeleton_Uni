package thkoeln.archilab.ecommerce.solution.client.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.usecases.ClientType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
public class Client implements ClientType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID clientId;

    @NotNull
    private String name;

    @Embedded
    private Email email;

    @Embedded
    private HomeAddress homeAddress;
}
