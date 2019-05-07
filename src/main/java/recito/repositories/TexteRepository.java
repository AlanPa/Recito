package recito.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TexteRepository extends MongoRepository<Texte, String> {

    public Texte findByNom(String nom);
    public List<Texte> findByContenu(String contenu);

}
