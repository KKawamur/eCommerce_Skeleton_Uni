package thkoeln.archilab.ecommerce.solution.client.application;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import thkoeln.archilab.ecommerce.solution.client.domain.Client;
import thkoeln.archilab.ecommerce.solution.client.domain.ClientDTO;

public class ClientDTOMapper {
    private final ModelMapper modelMapper;
    public ClientDTOMapper(){
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    }

    public ClientDTO mapToDTO (Client client){
        ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);
        return clientDTO;
    }
}
