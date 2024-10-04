package thkoeln.archilab.ecommerce.solution.deliverypackage.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import thkoeln.archilab.ecommerce.MethodNotAllowedException;
import thkoeln.archilab.ecommerce.NotFoundException;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackage;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageDTO;
import thkoeln.archilab.ecommerce.solution.deliverypackage.domain.DeliveryPackageRepository;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@Transactional
public class DeliveryPackageController {

    private final DeliveryPackageRepository deliveryPackageRepository;
    private DeliveryPackageDTOMapper deliveryPackageDTOMapper = new DeliveryPackageDTOMapper();
    private final OrderRepository orderRepository;
    @Autowired
    public DeliveryPackageController(DeliveryPackageRepository deliveryPackageRepository,
                                     OrderRepository orderRepository) {
        this.deliveryPackageRepository = deliveryPackageRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/deliveryPackages")
    public ResponseEntity<DeliveryPackageDTO[]> getDeliveryPackagesByOrder(@RequestParam(required = false) UUID orderId){
        if(orderId == null){
            throw new MethodNotAllowedException("Not allowed to access all delivery packages");
        }
        if(!orderRepository.existsById(orderId)) {
            throw new NotFoundException("Order does not exist");
        }
        Set<DeliveryPackage> deliveryPackages = deliveryPackageRepository.findByOrderIdWithParts(orderId);
        List<DeliveryPackageDTO> deliveryPackageDTOs = new ArrayList<>();
        for (DeliveryPackage deliveryPackage : deliveryPackages){
            DeliveryPackageDTO deliveryPackageDTO = deliveryPackageDTOMapper.deliveryPackageToDTO(deliveryPackage);
            deliveryPackageDTOs.add(deliveryPackageDTO);
        }
        DeliveryPackageDTO[] deliveryPackageDTOArray = new DeliveryPackageDTO[deliveryPackageDTOs.size()];
        deliveryPackageDTOs.toArray(deliveryPackageDTOArray);

        return new ResponseEntity<>(deliveryPackageDTOArray, HttpStatus.OK);
    }
}
