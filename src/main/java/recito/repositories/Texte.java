package recito.repositories;

import org.springframework.data.annotation.Id;


public class Texte {

    @Id
    private String id;

    private String nom;
    private String contenu;

    public Texte() {}

    public Texte(String nom, String contenu) {
        this.nom = nom;
        this.contenu = contenu;
    }

    @Override
    public String toString() {
        return String.format(
                "Client[id=%s, Nom='%s', Contenu='%s']",
                id, nom, contenu);
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

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
