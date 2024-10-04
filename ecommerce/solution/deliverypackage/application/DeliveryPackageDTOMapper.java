package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageDTO;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePart;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackagePartDTO;

import java.util.ArrayList;
import java.util.List;

public class DeliveryPackageDTOMapper {

    public DeliveryPackageDTO deliveryPackageToDTO(DeliveryPackage deliveryPackage){
        DeliveryPackageDTO deliveryPackageDTO = new DeliveryPackageDTO();
        deliveryPackageDTO.setId(deliveryPackage.getId());
        deliveryPackageDTO.setStorageUnitId(deliveryPackage.getStorageUnit().getId());
        deliveryPackageDTO.setOrderId(deliveryPackage.getOrder().getId());

        List<DeliveryPackagePartDTO> deliveryPackagePartDTOs = new ArrayList<>();
        for (DeliveryPackagePart deliveryPackagePart : deliveryPackage.getDeliveryPackageParts()){
            DeliveryPackagePartDTO deliveryPackagePartDTO = new DeliveryPackagePartDTO(
                    deliveryPackagePart.getProduct().getId(),
                    deliveryPackagePart.getAmount()
            );
            deliveryPackagePartDTOs.add(deliveryPackagePartDTO);
        }

        DeliveryPackagePartDTO[] deliveryPackagePartDTOArray = new DeliveryPackagePartDTO[deliveryPackagePartDTOs.size()];
        deliveryPackagePartDTOs.toArray(deliveryPackagePartDTOArray);

        deliveryPackageDTO.setDeliveryPackageParts(deliveryPackagePartDTOArray);

        return deliveryPackageDTO;
    }
}
