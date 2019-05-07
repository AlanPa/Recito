package recito.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface TexteRepository extends MongoRepository<Texte, String> {

    public Optional<Texte> findById(String id);
    public Texte findByNom(String nom);
    public List<Texte> findByContenu(String contenu);
    public List<Texte> findByAuteur(String auteur);

}
