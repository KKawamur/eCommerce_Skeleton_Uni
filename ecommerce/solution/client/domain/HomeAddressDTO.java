package thkoeln.archilab.ecommerce.solution.client.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeAddressDTO {
    private String street;
    private String city;
    private ZipCodeDTO zipCodeDTO;
}
