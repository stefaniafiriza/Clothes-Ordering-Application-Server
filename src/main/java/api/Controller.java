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

    @GetMapping("/api/login")
    public String login(@RequestParam String key, @RequestParam String username, @RequestParam String password){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.login(username,password);
    }

    @GetMapping("/api/register")
    public String register(@RequestParam String key,@RequestParam String username, @RequestParam String password, @RequestParam String name,@RequestParam String email, @RequestParam String type, @RequestParam String phoneNumber,String codeManager){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.register(username,password,name,email,type,phoneNumber,codeManager);
    }

    @GetMapping("/api/addProduct")
    public String addProduct(@RequestParam String key,@RequestParam String name,@RequestParam String type,@RequestParam String size,@RequestParam String price,@RequestParam String stock,@RequestParam String description){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.addProduct(name, type, size, price, stock, description);
    }
    @GetMapping("/api/getProducts")
    public String getProducts(@RequestParam String key){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getProducts();
    }

    @GetMapping("/api/search")
    public String search(@RequestParam String key,@RequestParam String name){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.search(name);
    }

    @GetMapping("/api/createShoppingCartID")
    public String createShoppingCartID(@RequestParam String key,@RequestParam String userID){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.createShoppingCartID(userID);
    }

    @GetMapping("/api/addToCart")
    public String addToCart(@RequestParam String key,@RequestParam String cartID, @RequestParam String productID, @RequestParam String amount){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.addToCart(cartID, productID, amount);
    }

    @GetMapping("/api/removeFromCart")
    public String removeFromCart(@RequestParam String key,@RequestParam String cartID, @RequestParam String productID, @RequestParam String amount){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.removeFromCart(cartID, productID, amount);
    }

    @GetMapping("/api/order")
    public String order(@RequestParam String key, @RequestParam String cartID)
    {
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.order(cartID);
    }
}
