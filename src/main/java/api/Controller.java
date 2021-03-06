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
    public String register(@RequestParam String key,@RequestParam String username, @RequestParam String password, @RequestParam String name,@RequestParam String email, @RequestParam String type, @RequestParam String phoneNumber,@RequestParam String codeManager){
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

    @GetMapping("/api/getShoppingCart")
    public String getShoppingCart(@RequestParam String key, @RequestParam String cartID){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getShoppingCart(cartID);
    }

    @GetMapping("/api/orderNextStep")
    public String orderNextStep(@RequestParam String key, @RequestParam String orderID){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.orderNextStep(orderID);
    }

    @GetMapping("/api/getOrderByUserID")
    public String getOrderByUserID(@RequestParam String key, @RequestParam String userID){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getOrderByUserID(userID);
    }

    @GetMapping("/api/facebooklogin")
    public String loginWithFacebook(@RequestParam String key, @RequestParam String email, @RequestParam String name,@RequestParam String id){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.loginWithFacebook(email, id, name);
    }

    @GetMapping("/api/facebookregister")
    public String registerWithFacebook(@RequestParam String key, @RequestParam String email, @RequestParam String name,@RequestParam String id){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.registerWithFacebook(email, id, name);
    }

    @GetMapping("/api/deleteProduct")
    public String deleteProduct(@RequestParam String key, @RequestParam String name){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        if(this.man.deleteProduct(name))
            return Utils.createResult("successful", "Deleted product");
        return  Utils.createResult("error", "An error has occured");
    }
    @GetMapping("/api/editProduct")
    public String editProduct(@RequestParam String key,@RequestParam String id,
                              @RequestParam String name,@RequestParam String type,
                              @RequestParam String size,@RequestParam String price,
                              @RequestParam String stock,@RequestParam String description){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.editProduct(id, name, type,size, price, stock, description);
    }

    @GetMapping("/api/getOrders")
    public String getOrders(@RequestParam String key){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.getOrders();
    }

    @GetMapping("/api/toggleAddToCart")
    public String toggleAddToCart(@RequestParam String key){
        if(!key.equals(Utils.API_KEY)){
            return Utils.createResult("error", "API Key is not valid.");
        }
        return this.man.toggleAddToCart();
    }

}
