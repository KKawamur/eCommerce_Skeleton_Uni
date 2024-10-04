package thkoeln.archilab.ecommerce.solution.deliverypackage.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;
import thkoeln.archilab.ecommerce.solution.order.domain.Order;
import thkoeln.archilab.ecommerce.solution.storageunit.domain.StorageUnit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPackage extends AbstractEntity {
    @ManyToOne
    private StorageUnit storageUnit;

    @ManyToOne
    private Order order;

    @Embedded
    private HomeAddress homeAddress;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DeliveryPackagePart> deliveryPackageParts = new ArrayList<>();
}
