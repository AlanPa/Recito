package recito.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import recito.repositories.Client;
import recito.repositories.ClientRepository;
import recito.repositories.Texte;
import recito.repositories.TexteRepository;
import recito.request.CreateAccountRequest;
import recito.request.SignInRequest;
import recito.utils.CompareText;
import recito.utils.Pair;
import recito.utils.PdfExtractor;

import java.io.IOException;
import java.util.*;

@RestController
public class Controleur {
    private static final String ERROR_MISSING_JSON_ATTRIBUTE ="Check the fields of the JSON send, some fields are missing or have a null value !";
    private static final String ERROR_MISSING_IN_DATABASE ="The %s was not found in the database !";

    @Value("${azure.token.speech:}")
    private static String SPEECHSUBSCRIPTION_KEY="5eae85560bb241b884f09a170d1a3214";
    @Value("${azure.service.region:francecentral}")
    private static String SERVICE_REGION="francecentral";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ClientRepository repositoryClient;
    private final TexteRepository repositoryTexte;

    public Controleur(ClientRepository repositoryClient, TexteRepository repositoryTexte) {
        this.repositoryClient = repositoryClient;
        this.repositoryTexte = repositoryTexte;
    }

    @GetMapping("/testDelete")
    public String deleteAllDataInDb(){
        repositoryClient.deleteAll();
        repositoryTexte.deleteAll();
        return "OK";
    }

    @GetMapping("/testInfo")
    public String toDelete(){
        return "Déployé par Mirabelle";
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

    @PostMapping("/getText")
    public Map<String,Object> getText(@RequestBody Map<String,Object> postData){
        Map<String,Object> m=new HashMap<>();

        if(postData.containsKey("idText")&&
                postData.containsKey("idClient"))
        {
            String idText = (String)postData.get("idText");
            Optional<Client> oc=repositoryClient.findById((String)postData.get("idClient"));
            if(oc.isPresent()){
                Client c=oc.get();
                for(Texte t:c.getBiblio()){
                    if(t.getId().equals(idText)){
                        m.put("text",t);
                        break;
                    }
                }
            }else{
                addErrorCustomMessage(m,String.format(ERROR_MISSING_IN_DATABASE,"user"));
            }

        }else{
            addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }
        return m;
    }

    @PostMapping("/getLibrary")
    public Map<String,Object> getLibrary(@RequestBody Map<String,Object> postData){
        Map<String,Object> m=new HashMap<>();

        if(postData.containsKey("idClient"))
        {
            Optional<Client> oc=repositoryClient.findById((String)postData.get("idClient"));
            if(oc.isPresent()){
                Client c=oc.get();
                m.put("library",c.getBiblio());
            }else{
                addErrorCustomMessage(m,String.format(ERROR_MISSING_IN_DATABASE,"user"));
            }

        }else{
            addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }
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

        if(postData.containsKey("textTitle")&&
                postData.containsKey("textAuthor")&&
                postData.containsKey("textContent")&&
                postData.containsKey("idClient"))
        {
            String title = (String)postData.get("textTitle");
            String author = (String)postData.get("textAuthor");
            String content = (String)postData.get("textContent");
            Optional<Client> oc=repositoryClient.findById((String)postData.get("idClient"));
            if(oc.isPresent()){
                Client c=oc.get();
                Texte t = new Texte(title,content,author);
                repositoryTexte.save(t);
                c.addTexte(t);
                repositoryClient.save(c);
                m.put("create",true);
            }else{
                addErrorCustomMessage(m,String.format(ERROR_MISSING_IN_DATABASE,"user"));
            }

        }else{
            addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }
        return m;
    }

    @PostMapping("/signIn")
    public Map<String, Object> connexion(@RequestBody SignInRequest requestInfo) {
        Map<String,Object> m=new HashMap<>();

        if(requestInfo.getLogin()==null||requestInfo.getPasswordClient()==null){
            return addErrorCustomMessage(m, ERROR_MISSING_JSON_ATTRIBUTE);
        }
        Client c=repositoryClient.findByNom(requestInfo.getLogin());

        m.put("signIn",false);
        if(c!=null&&c.checkPassword(requestInfo.getPasswordClient())){
            m.put("signIn",true);
            m.put("client",c);
        }

        return m;
    }

    @PostMapping("/signOut")
    public Map<String, Object> deconnexion() {
        Map<String,Object> m=new HashMap<>();
        boolean status=true;
        //TODO Implement logout with session
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
                !mail.matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
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

        m.put("create",true);
        return m;
    }

    @PostMapping("/RetrieveFile")
    public Map<String, Object> convertPdf(@RequestParam("idClient") String idClient,
                                          @RequestParam("textTitle") String titre,
                                          @RequestParam("textAuthor") String auteur,
                                          @RequestParam("file") MultipartFile file) {

        Map<String,Object> m=new HashMap<>();
        m.put("Status","Succes");
        m.put("Taille",file.getSize());
        m.put("Nom",file.getOriginalFilename());

        try{
            Optional<Client> oc=repositoryClient.findById(idClient);
            if(oc.isPresent()){
                String text=PdfExtractor.extract(file);
                m.put("Text", text);

                Texte t=new Texte(titre,text,auteur);
                repositoryTexte.insert(t);
                m.put("idText", t.getId());

                Client c=oc.get();
                c.addTexte(t);
                repositoryClient.save(c);

                m.put("create",true);
            }else{
                addErrorCustomMessage(m,String.format(ERROR_MISSING_IN_DATABASE,"user"));
            }

            //TODO remove old line
            //m.put("Language", PdfExtractor.getLanguage(text));
        }catch (IOException e){
            return addErrorCustomMessage(m,e,"File exception : ");
        }

        return m;
    }

    @PostMapping("/RetrieveTextComparison")
    public Map<String,Object> calculateFullResults(
            @RequestBody Map<String, List<String>> textsToCompare){

        Map<String,Object> m=new HashMap<>();

        List<String> idText = textsToCompare.get("idText");
        List<String> originalText = textsToCompare.get("originalText");
        List<String> textRead = textsToCompare.get("textRead");

        Pair<Integer, String> res = CompareText.calculateFullResult(originalText, textRead);

        m.put("Status","Succes");
        m.put("text",res.getValue());
        m.put("score",res.getKey());

        Optional<Texte> ot=repositoryTexte.findById(idText.get(0));
        if(ot.isPresent()){
            Texte t=ot.get();
            int score=(t.getScore()>Integer.parseInt(res.getValue()))?
                    (t.getScore()):(Integer.parseInt(res.getValue()));
            t.setScore(score);
            repositoryTexte.save(t);
        }else{
            addErrorCustomMessage(m,String.format(ERROR_MISSING_IN_DATABASE,"text"));
        }
        return m;
    }

    @GetMapping("/RetrieveSpeechKey")
    public Map<String,Object> getSpeechKeys(){
        Map<String,Object> m=new HashMap<>();
        m.put("speechSubscriptionKey",SPEECHSUBSCRIPTION_KEY);
        m.put("serviceRegion",SERVICE_REGION);
        return m;
    }

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
