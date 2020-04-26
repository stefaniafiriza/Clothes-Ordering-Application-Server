package api;

import database.Manager;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    Manager man;
    public Controller(){
        this.man = new Manager();
    }
}
