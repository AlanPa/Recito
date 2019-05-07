package recito.repositories;

import org.springframework.data.annotation.Id;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class Client {

    @Id
    private String id;

    private String nom;
    private String password;

    public Client() {}

    public Client(String nom, String password) {
        this.nom = nom;
        this.setPassword(password);
    }

    @Override
    public String toString() {
        return String.format(
                "Client[id=%s, Nom='%s', PasswordC='%s']",
                id, nom, password);
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

    public void setPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String encodedPassword = encoder.encode(password);
        this.password = encodedPassword;
    }
}
