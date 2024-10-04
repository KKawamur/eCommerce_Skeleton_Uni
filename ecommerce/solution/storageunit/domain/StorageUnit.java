package thkoeln.archilab.ecommerce.solution.storageunit.domain;

import lombok.Getter;
import lombok.Setter;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.solution.core.AbstractEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class StorageUnit extends AbstractEntity {
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<InventoryLevel> inventoryLevelList = new ArrayList<>();

    @Embedded
    private HomeAddress homeAddress;
}
