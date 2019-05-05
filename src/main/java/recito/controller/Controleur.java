package recito.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import recito.repositories.Client;
import recito.repositories.ClientRepository;
import recito.repositories.Texte;
import recito.repositories.TexteRepository;
import recito.utils.PdfExtractor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controleur {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final ClientRepository repositoryClient;
    private final TexteRepository repositoryTexte;

    public Controleur(ClientRepository repositoryClient, TexteRepository repositoryTexte) {
        this.repositoryClient = repositoryClient;
        this.repositoryTexte = repositoryTexte;
    }

    @GetMapping("/testSet")
    public String insertDataIntoDb(){

        repositoryClient.deleteAll();
        repositoryClient.save(new Client("Gasiuk", "reci"));
        repositoryClient.save(new Client("Gasiuk2", "reci2"));
        repositoryClient.save(new Client("Gasiuk3", "reci3"));

        repositoryTexte.deleteAll();
        repositoryTexte.save(new Texte("jdr", "texte du jdr"));

        return "OK";
    }

    @GetMapping("/testGet")
    public Map<String,Object> getDataFromDb(){

        Client c=repositoryClient.findByNom("Gasiuk");
        Texte t=repositoryTexte.findByNom("jdr");

        Map<String,Object> m=new HashMap<>();
        m.put("client",c);
        m.put("texte",t);

        return m;
    }

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    //Spring se lance sur localhost:8080
    @PostMapping("/Connexion")
    public Map<String, String> connexion() {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }

    @GetMapping("/GetId")
    public Map<String, Integer> connexionGet(@RequestParam("id") int id) {
        Map<String,Integer> m=new HashMap<>();
        m.put("Status",id);
        return m;
    }


    @PostMapping("/PostId")
    public Map<String, Integer> connexionPost(@RequestParam("id") int id) {
        Map<String,Integer> m=new HashMap<>();
        m.put("Status",id);
        return m;
    }

    @PostMapping("/PostBody")
    public Map<String, String> checkBody(@RequestBody String s) {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        m.put("body_receive",s);
        return m;
    }

    @PostMapping("/RetrieveText")
    public Map<String, String> test() {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }

    @PostMapping("/RetrieveFile")
    public Map<String, Object> testUpload(@RequestParam("file") MultipartFile file) {

        Map<String,Object> m=new HashMap<>();
        m.put("Status","Succes");
        m.put("Taille",file.getSize());
        m.put("Nom",file.getOriginalFilename());

        try{
            String text=PdfExtractor.extract(file);
            m.put("Text", text);
            //m.put("Language", PdfExtractor.getLanguage(text));
        }catch (IOException e){
            LOG.error("File exception : "+e.getMessage());
            m.put("Status","Error");
            m.put("Message","File exception : "+e.getMessage());
            return m;
        }



        return m;
    }

}
