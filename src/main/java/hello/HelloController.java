package hello;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String testUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        System.out.println("Size : "+file.getSize());
        System.out.println("Message : "+file.getOriginalFilename());
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

}
