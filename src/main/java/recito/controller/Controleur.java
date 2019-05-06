package recito.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import recito.repositories.Client;
import recito.repositories.ClientRepository;
import recito.repositories.Texte;
import recito.repositories.TexteRepository;
import recito.utils.PdfExtractor;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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


        Texte t = new Texte("jdr", "texte du jdr");
        Client c = new Client("Gasiuk", "reci","gasiuk.a@insa-lyon.fr");


        repositoryClient.deleteAll();
        repositoryTexte.deleteAll();

        repositoryTexte.save(t);
        c.addTexte(t);
        repositoryClient.save(c);




        return "OK";
    }

    @PostMapping("/GetProfil")
    public Optional<Client> GetProfil(@RequestBody Map<String,Object> body){

        if(body.get("idClient")!= null){
            java.lang.Object o = body.get("idClient");
            String os = (String)o;
            return repositoryClient.findById(os);
        }
        else
        {
            return null;
        }
    }

    @PostMapping("/InsertNewText")
    public String InsertNewText(@RequestBody Map<String,Object> body){

        if(body.get("title")!= null){
            String o = (String)body.get("title");
            if(body.get("text")!= null){
                String ot = (String)body.get("text");
                if(body.get("idClient")!= null){
                    Client c = repositoryClient.findById((String)body.get("idClient")).get();
                    //If texte doesn't exist
                    Texte t = new Texte(o,ot);
                    repositoryTexte.save(t);
                    c.addTexte(t);
                    repositoryClient.save(c);
                    return "Ok";
                }
                else{
                    return null;
                }
            }
            else{
                return null;
            }
        }
        else
        {
            return null;
        }
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

    @GetMapping("/GetTest")
    public String getTest(@RequestParam("id") int id) {

        return "Works";
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