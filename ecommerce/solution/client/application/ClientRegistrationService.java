package thkoeln.archilab.ecommerce.solution.client.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thkoeln.archilab.ecommerce.ShopException;
import thkoeln.archilab.ecommerce.domainprimitives.Email;
import thkoeln.archilab.ecommerce.domainprimitives.HomeAddress;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientRepository;
import thkoeln.archilab.ecommerce.solution.product.domain.UsableShoppingBasketParts;
import thkoeln.archilab.ecommerce.usecases.ClientRegistrationUseCases;
import thkoeln.archilab.ecommerce.usecases.ClientType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.EmailType;
import thkoeln.archilab.ecommerce.usecases.domainprimitivetypes.HomeAddressType;

import java.util.List;
import java.util.UUID;

@Service
public class ClientRegistrationService implements ClientRegistrationUseCases {
    private ClientRepository clientRepository;
    private UsableShoppingBasketParts usableShoppingBasketParts;
    @Autowired
    public ClientRegistrationService(ClientRepository clientRepository,
                                     UsableShoppingBasketParts usableShoppingBasketParts){
        this.clientRepository = clientRepository;
        this.usableShoppingBasketParts = usableShoppingBasketParts;
    }

    @Override
    public void register(String name, EmailType email, HomeAddressType homeAddress) {
        //Error handling
        checkClientExists(email);
        if(name == null || email == null || homeAddress == null)
            throw new ShopException("Parameters cannot be null!");
        if(name.isEmpty())
            throw new ShopException("Parameters cannot be empty!");

        //Actual functionality
        Client c = new Client();
        c.setName(name);
        c.setEmail((Email) email);
        c.setHomeAddress((HomeAddress) homeAddress);

        clientRepository.save(c);

        UUID shoppingBasketId = usableShoppingBasketParts.getNewOrExistingShoppingBasketId(c.getClientId());

    }

    @Override
    public void changeAddress(EmailType clientEmail, HomeAddressType homeAddress) {
        //Error handling
        checkClientDoesNotExist(clientEmail);
        if (clientEmail == null || homeAddress == null)
            throw new ShopException("Parameters cannot be null!");

        //Actual functionality
        else {
            Client c = clientRepository.findByEmail((Email) clientEmail);
            c.setHomeAddress((HomeAddress) homeAddress);
        }
    }

    @Override
    public ClientType getClientData(EmailType clientEmail) {
        if(clientEmail == null)
            throw new ShopException("Client email cannot be null!");
        checkClientDoesNotExist(clientEmail);
        return clientRepository.findByEmail((Email) clientEmail);
    }

    @Transactional
    public void deleteAllClients() {
        for(Client client : clientRepository.findAll()) {
            usableShoppingBasketParts.deleteShoppingBasketFromClient(client.getClientId());
        }
        clientRepository.deleteAll();
    }

    private void checkClientExists(EmailType email){
        if(clientRepository.existsByEmail((Email) email))
            throw new ShopException("Client already exists!");
    }

    private void checkClientDoesNotExist(EmailType email) {
        if(!clientRepository.existsByEmail((Email) email))
            throw new ShopException("Client does not exist!");
    }

    public Client findByEmail(Email email){
        return clientRepository.findByEmail(email);
    }

    public List<Client> findAll() {
        return (List<Client>) clientRepository.findAll();
    }

    public boolean existsByEmail(Email email) {
        return clientRepository.existsByEmail(email);
    }

    public boolean existsByClientId(UUID clientId) {
        return clientRepository.existsById(clientId);
    }

    public Client findById(UUID clientId) {
        return clientRepository.findById(clientId).get();
    }
}
