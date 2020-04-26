package api;

import database.Manager;
import database.Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    Manager man;
    public Controller(){
        this.man = new Manager();
    }

    @GetMapping("/api/verifyUser")
    public String verifyUser(@RequestParam String key, @RequestParam String username){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        if(this.man.verifyUser(username))
            return Utils.createResult("successful", "User found");
        return  Utils.createResult("error", "User not found");
    }
}
