package recito.repositories;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ClientRepository extends MongoRepository<Client, String> {

    public Optional<Client> findById(String id);
    public Client findByNom(String nom);
    public Client findByPassword(String password);
    public Client findByEmail(String email);
}