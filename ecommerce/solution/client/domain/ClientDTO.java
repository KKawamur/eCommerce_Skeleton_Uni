package thkoeln.archilab.ecommerce.solution.client.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ClientDTO {
    private UUID id;
    private String name;
    private EmailDTO emailDTO;
    private HomeAddressDTO homeAddressDTO;
}
