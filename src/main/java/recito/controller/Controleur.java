package recito.controller;

import javafx.util.Pair;
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
import recito.utils.CompareText;
import recito.utils.PdfExtractor;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class Controleur {
    private static final String ERROR_MISSING_JSON_ATTRIBUTE ="Check the fields of the JSON send, some fields are missing or have a null value !";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ClientRepository repositoryClient;
    private final TexteRepository repositoryTexte;

    public Controleur(ClientRepository repositoryClient, TexteRepository repositoryTexte) {
        this.repositoryClient = repositoryClient;
        this.repositoryTexte = repositoryTexte;
    }

    @GetMapping("/testSet")
    public String insertDataIntoDb(){

        repositoryClient.deleteAll();
        repositoryClient.save(new Client("Gasiuk", "reci", "lol@lol.com"));
        repositoryClient.save(new Client("Gasiuk2", "reci2","lol@lol.com"));
        repositoryClient.save(new Client("Gasiuk3", "reci3","lol@lol.com"));

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

    @PostMapping("/GetProfil")
    public Optional<Client> getProfil(@RequestBody Map<String,Object> postData){
        if(postData.containsKey("idClient")&&postData.get("idClient")!=null){
            Object o = postData.get("idClient");
            String os = (String)o;
            return repositoryClient.findById(os);
        }
        return Optional.empty();
    }

    @PostMapping("/InsertNewText")
    public Map<String,Object> insertNewText(@RequestBody Map<String,Object> postData){
        Map<String,Object> m=new HashMap<>();

        if(postData.containsKey("title")&&
                postData.containsKey("text")&&
                postData.containsKey("idClient"))
        {
            String o = (String)postData.get("title");
            String ot = (String)postData.get("text");
            Optional<Client> oc=repositoryClient.findById((String)postData.get("idClient"));
            if(oc.isPresent()){
                Client c=oc.get();
                //If texte doesn't exist
                Texte t = new Texte(o,ot);
                repositoryTexte.save(t);
                c.addTexte(t);
                repositoryClient.save(c);
                m.put("create",true);
            }else{
                addErrorCustomMessage(m,"The user was not found in the database !");
            }

        }else{
            addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }
        return m;
    }

    @PostMapping("/signIn")
    public Map<String, Object> connexion(@RequestBody SignInRequest requestInfo) {
        Map<String,Object> m=new HashMap<>();
        //TODO Implement login
        if(requestInfo.getLogin()==null||requestInfo.getPasswordClient()==null){
            return addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
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

        String mail=creationInfo.getEmailClient();
        String pseudo=creationInfo.getLoginClient();
        String password=creationInfo.getPasswordClient();
        if(mail==null||
                !mail.matches("/^[^\\W][a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*\\@[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*\\.[a-zA-Z]{2,4}$/")
        ){
            return addErrorCustomMessage(m,"Check the fields of the JSON send, the mail adress is invalid or null !");
        }

        if(pseudo==null||password==null){
            return addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }

        if(repositoryClient.findByEmail(mail)!=null||repositoryClient.findByNom(pseudo)!=null){
            return addErrorCustomMessage(m,"An user with the same mail or pseudonyme was found in the database !");
        }

        repositoryClient.insert(new Client(pseudo,password,mail));

        //Todo create user
        m.put("create",true);
        return m;
    }

    @PostMapping("/RetrieveFile")
    public Map<String, Object> convertPdf(@RequestParam("file") MultipartFile file) {

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

    @PostMapping("/RetrieveTextComparison")
    public Map<String,Object> calculateFullResults(@RequestParam Map<String, Object> textsToCompare){
        String originalText = textsToCompare.get("originalText").toString();
        String textRead = textsToCompare.get("textRead").toString();
        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);
        Map<String,Object> m=new HashMap<>();
        m.put("text",res.getValue());
        m.put("score",res.getKey());
        return m;
    }

    /*private Map<String,Object> addErrorMessage(Map<String,Object> m, Exception e){
        LOG.error("An exception was encountered : {}",e.getMessage());
        m.put("Status","Error");
        m.put("Message","An exception was encountered : "+e.getMessage());
        return m;
    }*/

    private Map<String,Object> addErrorCustomMessage(Map<String,Object> m, String message){
        log.error(message);
        m.put("Status","Error");
        m.put("Message",message);
        return m;
    }

    private Map<String,Object> addErrorCustomMessage(Map<String,Object> m, Exception e, String header){
        log.error(header,e.getMessage());
        m.put("Status","Error");
        m.put("Message",header+e.getMessage());
        return m;
    }
}
