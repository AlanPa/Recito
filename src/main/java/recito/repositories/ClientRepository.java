package recito.repositories;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface ClientRepository extends MongoRepository<Client, String> {

    public Client findByNom(String Nom);
    public List<Client> findByPassword(String Password);

}
