package thkoeln.archilab.ecommerce.solution.client.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import thkoeln.archilab.ecommerce.MethodNotAllowedException;
import thkoeln.archilab.ecommerce.NotFoundException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientDTO;

import static org.springframework.http.HttpStatus.OK;

@RestController
@Transactional
public class ClientController {
    private final ClientRegistrationService clientRegistrationService;
    ClientDTOMapper clientDTOMapper = new ClientDTOMapper();

    @Autowired
    public ClientController (ClientRegistrationService clientRegistrationService){
        this.clientRegistrationService = clientRegistrationService;
    }

    @GetMapping("/clients")
    @ResponseBody
    public ResponseEntity<ClientDTO> getOneClient(@RequestParam(required = false) Email email){
        System.out.println("BLOOP!");
        if(email == null){
            throw new MethodNotAllowedException("Not allowed to access all clients");
        } else if (!clientRegistrationService.existsByEmail(email)) {
            throw new NotFoundException("Client not found");
        } else {
            Client client = clientRegistrationService.findByEmail(email);
            ClientDTO clientDTO = clientDTOMapper.mapToDTO(client);
            return new ResponseEntity<>(clientDTO,OK);
        }
    }

}
