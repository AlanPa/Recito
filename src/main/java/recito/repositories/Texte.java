package recito.repositories;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Texte")
public class Texte {

    @Id
    private String id;

    private String nom;
    private String contenu;
    private String auteur;
    private int score;

    public Texte() {}

    public Texte(String nom, String contenu, String auteur) {
        this.nom = nom;
        this.contenu = contenu;
        this.auteur = auteur;
        this.score = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Texte[id=%s, Nom='%s', Contenu='%s', Score=%d]",
                id, nom, contenu,score);
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

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}