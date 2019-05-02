package hello;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    //Spring se lance sur localhost:8080
    @PostMapping("/Connexion")
    Map<String, String> connexion() {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }

    @GetMapping("/GetId")
    Map<String, String> connexionGet(@RequestParam("id") int id) {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }


    @PostMapping("/PostId")
    Map<String, String> connexionPost(@RequestParam("id") int id) {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }

    @PostMapping("/PostBody")
    Map<String, String> checkBody(@RequestBody String s) {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        m.put("body_receive",s);
        return m;
    }

    @PostMapping("/RetrieveText")
    Map<String, String> test() {
        Map<String,String> m=new HashMap<>();
        m.put("Status","Set");
        return m;
    }

}
