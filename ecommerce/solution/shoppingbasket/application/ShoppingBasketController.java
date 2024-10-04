package thkoeln.archilab.ecommerce.solution.shoppingbasket.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import thkoeln.archilab.ecommerce.*;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.solution.client.application.ClientRegistrationService;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.order.domain.OrderDTO;
import thkoeln.archilab.ecommerce.solution.product.application.ProductCatalogService;
import thkoeln.archilab.ecommerce.solution.shoppingbasket.domain.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Transactional
public class ShoppingBasketController {
    private ShoppingBasketRepository shoppingBasketRepository;
    private ShoppingBasketService shoppingBasketService;
    private ProductCatalogService productCatalogService;
    private ClientRegistrationService clientRegistrationService = null;
    ShoppingBasketDTOMapper shoppingBasketDTOMapper = new ShoppingBasketDTOMapper();

    @Autowired
    public ShoppingBasketController(ShoppingBasketRepository shoppingBasketRepository,
                                    ShoppingBasketService shoppingBasketService,
                                    ClientRegistrationService clientRegistrationService,
                                    ProductCatalogService productCatalogService){
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.productCatalogService = productCatalogService;
        this.shoppingBasketService = shoppingBasketService;
        this.clientRegistrationService = clientRegistrationService;
    }


    @GetMapping("/shoppingBaskets")
    @ResponseBody
    public ResponseEntity<ShoppingBasketDTO> getShoppingBasketByClientId(@RequestParam(required = false)UUID clientId){
        if(clientId == null){
            throw new MethodNotAllowedException("Not allowed to access all shopping baskets");
        }
        else if(!clientRegistrationService.existsByClientId(clientId)){
            throw new NotFoundException("Shopping basket of this client does not exist");
        }
        UUID shoppingBasketId = shoppingBasketService.getNewOrExistingShoppingBasketId(clientId);
        ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId);
        ShoppingBasketDTO shoppingBasketDTO = shoppingBasketDTOMapper.shoppingBasketToDTO(shoppingBasket);
        return new ResponseEntity<>(shoppingBasketDTO, OK);
    }

    @GetMapping("/")
    public void catchInvalidPath(){
        throw new NotFoundException("Page not found");
    }

    @PostMapping("/shoppingBaskets/{shoppingBasket-id}/parts")
    public ResponseEntity<ShoppingBasketPartDTO> addNewProductToShoppingBasket(@PathVariable("shoppingBasket-id") String shoppingBasketId,
                                                                               @RequestBody ShoppingBasketPartDTO parts){
        if(!productCatalogService.existsByProductId(parts.getProductId()))
            throw new NotFoundException("Product does not exist");
        UUID shoppingBasketUUID = UUID.fromString(shoppingBasketId);
        if(!shoppingBasketService.existsByShoppingBasketId(shoppingBasketUUID))
            throw new NotFoundException("Shopping Basket does not exist");
        System.out.println("ProductUUID: " +  shoppingBasketUUID);
        ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketUUID);
        Client client = shoppingBasket.getClient();
        if(parts.getQuantity() < 0)
            throw new UnprocessableEntityException("Quantity cannot be negative");
        try {
            shoppingBasketService.addProductToShoppingBasket(client.getEmail(), parts.getProductId(), parts.getQuantity());
        } catch (ShopException e){
            switch (e.getMessage()) {
                case "Too many Products reserved by other Customers!": {
                    throw new ConflictException("Too many Products reserved by other customers");
                }
                case "Not as many Products in stock!": {
                    throw new ConflictException("Not as many Products in stock");
                }
                default:
                    throw new ShopException(e.getMessage());
            }

        }
        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{shoppingBasket-id}")
                .buildAndExpand(shoppingBasket.getId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(parts);

    }

    @DeleteMapping("/shoppingBaskets/{shoppingBasket-id}/parts/{product-id}")
    public HttpStatus deleteProductFromShoppingBasket(@PathVariable("shoppingBasket-id") String shoppingBasketId,
                                                                                 @PathVariable("product-id") String productId){
        UUID shoppingBasketUUID = UUID.fromString(shoppingBasketId);
        if (!shoppingBasketService.existsByShoppingBasketId(shoppingBasketUUID))
            throw new NotFoundException("Shopping Basket not found");
        UUID productIdUUID = UUID.fromString(productId);
        if(!productCatalogService.existsByProductId(productIdUUID))
            throw new NotFoundException("Product not found");
        ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketUUID);
        Email clientEmail = shoppingBasket.getClient().getEmail();
        int deleteQuantity = 0;
        Map<UUID, Integer> shoppingBasketMap = shoppingBasketService.getShoppingBasketAsMap(clientEmail);
        if(!shoppingBasketMap.isEmpty()) {
            deleteQuantity = shoppingBasketMap.get(productIdUUID);
        } else {
            throw new ConflictException("No Products in shopping basket");
        }
        if (deleteQuantity == 0){
            throw new ConflictException("Product is not in shopping basket");
        }
        shoppingBasketService.useRemoveProductFromShoppingBasket(clientEmail, productIdUUID, deleteQuantity);
        return OK;
    }

    @PostMapping("/shoppingBaskets/{shoppingBasket-id}/checkout")
    public ResponseEntity<OrderDTO> checkoutShoppingBasket(@PathVariable("shoppingBasket-id") UUID shoppingBasketId){
        if (!shoppingBasketService.existsByShoppingBasketId(shoppingBasketId))
            throw new ConflictException("Shopping Basket does not exist");
        ShoppingBasket shoppingBasket = shoppingBasketService.findById(shoppingBasketId);
        UUID orderId = shoppingBasketService.checkout(shoppingBasket.getClient().getEmail());

        OrderDTO orderDTO = new OrderDTO(orderId);

        URI returnURI = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{shoppingBasket-id}")
                .buildAndExpand(shoppingBasket.getId())
                .toUri();
        return ResponseEntity
                .created(returnURI)
                .body(orderDTO);
    }
}
