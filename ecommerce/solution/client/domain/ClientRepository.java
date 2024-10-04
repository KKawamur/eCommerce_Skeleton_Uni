package thkoeln.archilab.ecommerce.solution.client.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import thkoeln.archilab.ecommerce.domainprimitives.Email;

import java.util.UUID;

public interface ClientRepository extends CrudRepository<Client, UUID> {
    Client findByEmail(@NonNull Email email);

    boolean existsByEmail(@NonNull Email email);
}
