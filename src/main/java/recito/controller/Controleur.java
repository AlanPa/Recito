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
import recito.request.CreateAccountRequest;
import recito.request.SignInRequest;
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
