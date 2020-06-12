import database.Manager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @BeforeClass
    public static void initDatabase(){
        database.Utils.getDatabaseCredentials();
    }

    @Test
    void verifyUser() {
        Manager man = new Manager();
        assertTrue(man.verifyUser("admin"));
        assertFalse(man.verifyUser("ad-min"));
    }

    @Test
    void loginUserNotFound() {
        Manager man = new Manager();
        String res = man.login("adm-in", "admin");
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
             json = (JSONObject) parser.parse(res);
        } catch (ParseException e) {
            fail();
            return;
        }
        String s = (String)((JSONObject)json.get("result")).get("message");
        assertEquals("User not found",s);

    }

    @Test
    void loginUserFound(){
        Manager man = new Manager();
        String res = man.login("admin", "admin");
        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(res);
            assertEquals("admin", json.get("Username"));
        } catch (ParseException e) {
            fail();
        }
    }

    @Test
    void verifyManagerCode() {
        Manager man = new Manager();
        assertFalse(man.verifyManagerCode("0"));
        assertTrue(man.verifyManagerCode("1"));
    }

    @Test
    void register() {
        Manager man = new Manager();
        assertFalse(man.verifyUser("testuser"));
        String res = man.register("testuser", "a","a","a","1","0","");
        assertFalse(res.contains("error"));
        assertTrue(man.verifyUser("testuser"));
        userCleanup(man);
    }

    @Test
    void addProduct() {
        Manager man = new Manager();
        String res = man.addProduct("testproduct", "1", "1", "1","1", "a");

        assertTrue(res.contains("successful"));
        assertTrue(man.deleteProduct("testproduct"));
    }

    JSONObject shoppingCartSetup(Manager man){

        // register and login to get the Id
        assertFalse(man.verifyUser("testuser"));
        man.register("testuser", "a","a","a","1","0","");
        assertTrue(man.verifyUser("testuser"));

        String res = man.login("testuser", "a");
        JSONParser parser = new JSONParser();
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(res);

        } catch (ParseException e) {
            userCleanup(man);
            fail();
        }

        return json;
    }

    void shoppingCartCleanUP(Manager man, String id){

        man.deleteShopingCart(id);
        userCleanup(man);
    }

    @Test
    void createShoppingCartID() {
        Manager man = new Manager();

        JSONObject json = shoppingCartSetup(man);

        // actual test
        String result = man.createShoppingCartID((String)json.get("Id"));

        assertTrue(result.contains("successful"));

        // cleanup
        JSONParser parser = new JSONParser();
        String id_json = man.getCartID("testuser");
        id_json = id_json.substring(id_json.indexOf('[') + 1, id_json.indexOf(']'));
        try {
            json = (JSONObject) parser.parse(id_json);

        } catch (ParseException e) {
            fail();
        }

        shoppingCartCleanUP(man, (String)json.get("Id"));

    }

    @Test
    void addToCart() {
        Manager man = new Manager();

        JSONObject json = shoppingCartSetup(man);

        man.createShoppingCartID((String)json.get("Id"));
        String id_json = man.getCartID("testuser");
        id_json = id_json.substring(id_json.indexOf('[') + 1, id_json.indexOf(']'));
        JSONParser parser = new JSONParser();
        try {
            json = (JSONObject) parser.parse(id_json);
        } catch (ParseException e) {
            userCleanup(man);
            fail();
        }
        String id = (String) json.get("Id");

        // actual test
        String result = man.addToCart(id,"1", "1");

        assertTrue(result.contains("successful"));

        //clean up

        shoppingCartCleanUP(man, id);
    }

    @Test
    void removeFromCart() {
        Manager man = new Manager();
        JSONObject json = shoppingCartSetup(man);

        // create the shopping cart
        man.createShoppingCartID((String)json.get("Id"));
        String id_json = man.getCartID("testuser");
        JSONParser parser = new JSONParser();
        try {
            json = (JSONObject) parser.parse(id_json);

        } catch (ParseException e) {
            fail();
        }
        String id = (String) json.get("Id");

        String addResult = man.addToCart(id,"1", "2");
        assertTrue(addResult.contains("successful"));

        //actual test
        String result = man.removeFromCart(id, "1", "1");
        assertTrue(result.contains("successful"));

        String shoppingCart = man.getShoppingCart(id);

        try{
            json = (JSONObject) parser.parse(shoppingCart);
        } catch (ParseException e) {
            fail();
        }

        assertEquals("1",((String[])json.get("Ammounts"))[0]);

        //clean up
        man.deleteShopingCart((String)json.get("Id"));

        userCleanup(man);
    }

    @Test
    void order() {
        Manager man = new Manager();

        JSONObject json = shoppingCartSetup(man);

        man.createShoppingCartID((String)json.get("Id"));
        String id_json = man.getCartID("testuser");
        JSONParser parser = new JSONParser();
        try {
            json = (JSONObject) parser.parse(id_json);
        } catch (ParseException e) {
            fail();
        }
        String id = (String) json.get("Id");
        String addresult = man.addToCart(id,"1", "1");
        assertTrue(addresult.contains("successful"));
        // actual test
        String result = man.order(id);
        assertTrue(result.contains("successful"));
        //clean up

        shoppingCartCleanUP(man, id);
    }

    void userCleanup(Manager man){
        assertTrue(man.deleteUser("testuser"));
        assertFalse(man.verifyUser("testuser"));
    }
}