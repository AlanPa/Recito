package recito.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import recito.repositories.Client;
import recito.repositories.ClientRepository;
import recito.repositories.Texte;
import recito.repositories.TexteRepository;
import recito.request.CreateAccountRequest;
import recito.request.SignInRequest;
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

    @PostMapping("/signIn")
    public Map<String, Object> connexion(@RequestBody SignInRequest requestInfo) {
        Map<String,Object> m=new HashMap<>();
        //TODO Implement login
        if(requestInfo.getLogin()==null||requestInfo.getPasswordClient()==null){
            return addErrorCustomMessage(m,"Check the fields of the JSON send, some fields are missing or have a null value !");
        }
        m.put("signIn",true);
        return m;
    }

    @PostMapping("/signOut")
    public Map<String, Object> deconnexion() {
        Map<String,Object> m=new HashMap<>();
        boolean status=true;
        //TODO Implement logout
        m.put("logout",status);
        return m;
    }

    @PostMapping("/createAccount")
    public Map<String, Object> createUser(@RequestBody CreateAccountRequest creationInfo) {
        Map<String,Object> m=new HashMap<>();

        if(creationInfo.getEmailClient()==null||
                !creationInfo.getEmailClient().matches("/^[^\\W][a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*\\@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*\\.[a-zA-Z]{2,4}$/")
        ){
            return addErrorCustomMessage(m,"Check the fields of the JSON send, the mail adress is invalid or null !");
        }

        if(creationInfo.getLoginClient()==null||creationInfo.getPasswordClient()==null){
            return addErrorCustomMessage(m,"Check the fields of the JSON send, some fields are missing or have a null value !");
        }
        //Todo create user
        m.put("create",true);
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
            //TODO create text
            //m.put("Language", PdfExtractor.getLanguage(text));
        }catch (IOException e){
            return addErrorCustomMessage(m,e,"File exception : ");
        }

        return m;
    }

    private Map<String,Object> addErrorMessage(Map<String,Object> m, Exception e){
        LOG.error("An exception was encountered : "+e.getMessage());
        m.put("Status","Error");
        m.put("Message","An exception was encountered : "+e.getMessage());
        return m;
    }

    private Map<String,Object> addErrorCustomMessage(Map<String,Object> m, String message){
        LOG.error(message);
        m.put("Status","Error");
        m.put("Message",message);
        return m;
    }

    private Map<String,Object> addErrorCustomMessage(Map<String,Object> m, Exception e, String header){
        LOG.error(header+e.getMessage());
        m.put("Status","Error");
        m.put("Message",header+e.getMessage());
        return m;
    }
}
