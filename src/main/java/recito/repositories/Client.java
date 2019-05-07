package recito.repositories;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Client")
public class Client {

    @Id
    private String id;

    private String nom;
    private String password;
    private String email;
    @DBRef
    private List<Texte> biblio;

    public Client() {}

    public Client(String nom, String password,String email) {
        this.nom = nom;
        this.setPassword(password);
        this.email = email;
        this.biblio = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format(
                "Client[id=%s, Nom='%s', PasswordC='%s',Email='%s',Biblio:'%s']",
                id, nom, password,email,biblio);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Texte> getBiblio() { return biblio; }

    public void addTexte(Texte texte) {
        this.biblio.add(texte);
    }

    public void setPassword(String password) {
        this.password = getEncodedString(password);
    }

    public String getEncodedString(String s){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.encode(s);
    }
}
