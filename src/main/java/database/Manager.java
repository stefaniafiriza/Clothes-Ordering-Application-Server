package database;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;

import static database.Utils.convertToJSON;

public class Manager {
    Connection c;
    Statement stmt;
    boolean connected = false;
    public static boolean ADD_TO_CART_DISABLED = false;

    public Manager() {
        this.connect();
    }

    public void connect() {

        if(Utils.DatabaseUser.isEmpty() || Utils.DatabasePassword.isEmpty()){
            Utils.getDatabaseCredentials();
        }
        int retry = 5;
        while (!connected && retry > 0) {
            try {
                Class.forName("org.postgresql.Driver");
                this.c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/ClothesOrderingApp",
                                Utils.DatabaseUser, Utils.DatabasePassword);
                this.stmt = this.c.createStatement();
                this.connected = true;
            } catch (Exception e) {
                retry -= 1;
            }
        }
    }

    private String escapeString(String str) {
        return str.replaceAll("'", "\\'");
    }

    public boolean verifyUser(String username) {
        while (!this.connected) {

            this.connect();
        }
        try {
            String sql = "SELECT \"Id\" FROM \"Users\" WHERE \"Username\"='" + escapeString(username) + "';";
            ResultSet rs = this.stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    public String login(String username, String pass) {
        while (!this.connected) {
            this.connect();
        }
        if (!verifyUser(username)) {
            return Utils.createResult("error", "User not found");
        }
        try {
            String sql = "SELECT * FROM \"Users\" WHERE \"Username\"='" + escapeString(username) + "';";
            ResultSet rs = stmt.executeQuery(sql);
            String user = convertToJSON(rs);
            String password = user.substring(0, user.indexOf('['));
            user = user.substring(user.indexOf('[') + 1, user.indexOf(']'));
            if (pass.equals(password)) {
                return user;
            }
            return Utils.createResult("error", "Incorrect password");
        } catch (Exception e) {
            return Utils.createResult("error", "Database error");
        }
    }

    private BigInteger generateID(String table, String field) {
        Random rand = new Random();
        BigInteger result = new BigInteger(24, rand);
        try {
            String sql = String.format("SELECT \"%s\" FROM \"%s\" WHERE \"%s\"=%s;", field, table, result, field);
            ResultSet rs = this.stmt.executeQuery(sql);
            if (rs.next())
                return generateID(table, field);
        } catch (Exception ignored) {

        }
        return result;
    }

    public boolean verifyManagerCode(String code) {
        try {
            String sql = "SELECT \"Id\" FROM \"ManagerCodes\" WHERE \"code\"='" + escapeString(code) + "';";
            ResultSet rs = this.stmt.executeQuery(sql);
            return rs.next();
        } catch (Exception e) {
            return false;
        }

    }

    public String register(String username, String password, String name, String email, String type, String phoneNumber, String codeManager) {
        while (!this.connected) {
            this.connect();
        }
        if (this.verifyUser(username)) {
            return Utils.createResult("error", "User already exists.");
        }
        if (!codeManager.equals("") && !verifyManagerCode(codeManager))
            return Utils.createResult("error", "Invalid Manager Code.");
        try {
            String id = this.generateID("Users", "Id").toString();
            String sql = String.format("INSERT INTO \"Users\"(" +
                            "\"Id\", \"Name\", \"Code Manager\", \"Type\", \"Email\", \"PhoneNumber\", \"Username\", \"Password\")" +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    id , escapeString(name), escapeString(codeManager), escapeString(type),
                    escapeString(email), escapeString(phoneNumber), escapeString(username), escapeString(password));
            stmt.executeUpdate(sql);
            this.createShoppingCartID(id);
        } catch (Exception e) {

            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Database error");
        }
        return Utils.createResult("successful", "User registered.");
    }

    public String addProduct(String name, String type, String size, String price, String stock, String description) {
        while (!this.connected) {
            this.connect();
        }
        try {
            String id = generateID("Product", "Id").toString();
            String sql = String.format("INSERT INTO public.\"Product\"(\n" +
                            "\t\"Id\", \"Name\", \"Type\", \"Size\", \"Price\", \"Stock\", \"Description\")\n" +
                            "\tVALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s');",
                    id, escapeString(name), escapeString(type), escapeString(size), escapeString(price), escapeString(stock), escapeString(description));
            stmt.executeUpdate(sql);
            return Utils.createResult("successful", String.format("%s", id));
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Malformed Query");
        }

    }

    public String getProducts() {
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = "SELECT * FROM \"Product\";";
            ResultSet rs = stmt.executeQuery(sql);
            return convertToJSON(rs);
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }
    }

    public String search(String name) {
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = String.format("SELECT * FROM \"Product\" WHERE \"Name\" LIKE '%%%s%%'", escapeString(name));
            ResultSet rs = stmt.executeQuery(sql);
            return convertToJSON(rs);
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }
    }

    public String createShoppingCartID(String userId) {

        while (!this.connected) {
            this.connect();
        }
        try {
            String id = generateID("ShoppingBasket", "Id").toString();
            String sql = String.format("UPDATE \"Users\"\n" +
                    "\tSET \"ShoppingCartId\"=%s\n" +
                    "\tWHERE \"Id\"='%s';", id, escapeString(userId));
            stmt.executeUpdate(sql);
            sql = String.format("INSERT INTO \"ShoppingBasket\"(\"Id\", \"UserId\") VALUES(%s, %s)", id, userId);
            stmt.executeUpdate(sql);
            return Utils.createResult("successful", "Created ShoppingCartID");
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Unable to update and create ShoppingCartID");
        }
    }

    public String addToCart(String cartID, String productID, String amount) {

        if(ADD_TO_CART_DISABLED){
            return Utils.createResult("error", "This api has been disabled.");
        }


        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = String.format("UPDATE \"ShoppingBasket\"\n" +
                    "\tSET \"Cart\"=\"Cart\" || Cast(%s as bigint), \"Ammounts\"= \"Ammounts\" || Cast(%s as bigint)\n" +
                    "\tWHERE \"Id\"=%s;", productID, amount, cartID);
            stmt.executeUpdate(sql);
            return Utils.createResult("successful", String.format("Added %s to the shopping cart %s", productID, cartID));
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Malformed Query");
        }
    }

    private String linkedListToString(LinkedList<Long> list){
        StringBuilder str = new StringBuilder();
        for (Long o : list) {
            str.append(o + "").append(",");
        }
        if(str.length()> 0)
            str.deleteCharAt(str.length() - 1);
        return str.toString();
    }

    public String removeFromCart(String cartID, String productID, String amount) {
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = String.format("SELECT * FROM \"ShoppingBasket\" WHERE \"Id\"=%s", cartID);
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                LinkedList<Long> cart = new LinkedList<Long>(Arrays.asList((Long[])rs.getArray("Cart").getArray()));
                LinkedList<Long> amounts = new LinkedList<Long>(Arrays.asList((Long[])rs.getArray("Ammounts").getArray()));

                int index;
                boolean ok = false;
                for (index = 0; index < cart.size(); index++) {
                    if ((cart.get(index) + "").equals(productID))
                    {
                        ok = true;
                        break;
                    }

                }
                if(!ok)
                    return Utils.createResult("error", "Could not find the product in the cart.");

                long newAmount = amounts.get(index) - Long.parseLong(amount);
                if (newAmount <= 0) {
                    amounts.remove(index);
                    cart.remove(index);
                }else{
                    amounts.set(index, newAmount);
                }
                sql = String.format("UPDATE \"ShoppingBasket\"\n" +
                        "\tSET \"Cart\"='{%s}', \"Ammounts\"= '{%s}'\n" +
                        "\tWHERE \"Id\"=%s;", linkedListToString(cart), linkedListToString(amounts), cartID);

                stmt.executeUpdate(sql);
                return Utils.createResult("successful", String.format("Removed %s to the shopping cart %s", productID, cartID));
            }
            return Utils.createResult("error", String.format("Product %s is not in the shopping cart %s", productID, cartID));
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Malformed Query");
        }
    }

    public String orderNextStep(String orderID){
        while (!this.connected) {
            this.connect();
        }
        try {

            String sql = String.format("SELECT \"Status\" FROM \"Order\" WHERE \"Id\"=%s", orderID);

            ResultSet rs = stmt.executeQuery(sql);
            String json = Utils.convertToJSON(rs);
            json = json.substring(json.indexOf('[') + 1, json.indexOf(']'));
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(json);

            int status = Integer.parseInt((String)jo.get("Status"));

            sql = String.format("UPDATE \"Order\" SET \"Status\" = '%s' WHERE \"Id\"=%s", status+1, orderID);
            stmt.executeUpdate(sql);

            return Utils.createResult("successful", "Updated order");
        } catch (SQLException | ParseException ignored) {
            try {
                c.rollback();
            } catch (SQLException ignored1) {
            }
        }

        return Utils.createResult("error", "Malformed Query");
    }

    public String getOrderByUserID(String userID){
        while (!this.connected) {
            this.connect();
        }
        try{
            String sql = String.format("SELECT * FROM \"Order\" WHERE \"UserID\"=%s", userID);
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);
        } catch (SQLException ignored) {
        }
        return Utils.createResult("error", "Malformed Query");
    }


    public String order(String cartID){
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = String.format("SELECT * FROM \"ShoppingBasket\" WHERE \"Id\"=%s", cartID);
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                LinkedList<Long> cart = new LinkedList<Long>(Arrays.asList((Long[])rs.getArray("Cart").getArray()));
                LinkedList<Long> amounts = new LinkedList<Long>(Arrays.asList((Long[])rs.getArray("Ammounts").getArray()));
                BigInteger user = BigInteger.valueOf(rs.getInt("UserId"));

                sql = String.format("INSERT INTO public.\"Order\"(\n" +
                        "\t\"Id\", \"Status\", \"UserID\", \"Cart\", \"Ammounts\") VALUES (%s, '%s', '%s', '{%s}', '{%s}');",
                        cartID, "0", user.toString(), linkedListToString(cart), linkedListToString(amounts));

                stmt.executeUpdate(sql);

                sql = String.format("UPDATE \"ShoppingBasket\"\n" +
                        "\tSET \"Cart\"='{}', \"Ammounts\"='{}'\n" +
                        "\tWHERE \"Id\"=%s;", cartID);
                stmt.executeUpdate(sql);
                return Utils.createResult("successful", String.format("Ordered %s", cartID));
            }
            return Utils.createResult("error", String.format("Shopping cart %s not found", cartID));
        }
        catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
            return Utils.createResult("error", "Malformed Query");
        }
    }


    public String getOrders(){
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = "SELECT * FROM \"Order\";";
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Utils.createResult("error", "Malformed Query");
    }

    // Used only in tests
    public boolean deleteUser(String username){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = "DELETE FROM \"Users\" WHERE \"Username\"='" + escapeString(username) + "';";
            stmt.executeUpdate(sql);
            return true;
        }catch (Exception e){
            try{ c.rollback(); }catch (SQLException ignored){}
        }
        return false;
    }


    public boolean deleteProduct(String name){
        while(!this.connected){
            this.connect();
        }

        try {
            String sql = "DELETE FROM \"Product\" WHERE \"Name\"='" + escapeString(name) + "';";
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
        }
        return false;
    }


    // Used only in tests
    public boolean deleteShopingCart(String cartID){
        while(!this.connected){
            this.connect();
        }

        try {
            String sql = "DELETE FROM \"ShoppingBasket\" WHERE \"Id\"='" + escapeString(cartID) + "';";
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            try{ c.rollback(); }catch (SQLException ignored){}
        }
        return false;
    }


    // Used only in tests
    public String getCartID(String username){
        while(!this.connected){
            this.connect();
        }

        try{
            String sql = String.format("SELECT \"ShoppingCartId\" FROM \"Users\" WHERE \"Username\"='%s';",username);
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);


        } catch (SQLException ignored) {

        }
        return "";

    }

    public String getShoppingCart(String cartID) {
        while(!this.connected){
            this.connect();
        }

        try{
            String sql = String.format("SELECT * FROM \"ShoppingBasket\" WHERE \"Id\"='%s';",cartID);
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);


        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return Utils.createResult("error", "Malformed Query");
    }

    // Used only in tests
    public boolean deleteOrder(String orderID){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = "DELETE FROM \"Order\" WHERE \"Id\"='" + escapeString(orderID) + "';";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException throwables) {
            try{ c.rollback(); }catch (SQLException ignored){}
        }
        return false;
    }

    public String loginWithFacebook(String email, String id, String name) {
        while(!this.connected){
            this.connect();
        }
        if (!verifyUser(id)) {
            this.registerWithFacebook(email, id, name);
        }
        try{
            String sql = "SELECT * FROM \"Users\" WHERE \"Email\"='" + escapeString(email) + "' AND \"Username\"='"+escapeString(id)+ "' AND \"Name\"='"+escapeString(name)+"';";
            ResultSet rs = stmt.executeQuery(sql);
            String user = convertToJSON(rs);
            user.substring(0, user.indexOf('[')); // delete password from json
            user = user.substring(user.indexOf('[') + 1, user.indexOf(']'));
            return user;
        } catch (Exception ignored) {
            return Utils.createResult("error", "Malformed Query");
        }
    }

    public String registerWithFacebook(String email, String id, String name){
        return this.register(id,name,name,email,"0","","");
    }

    public String editProduct(String id, String name, String type, String size, String price, String stock, String description){
        while(!this.connected){
            this.connect();
        }
        try{
            String sql = String.format("UPDATE public.\"Product\"\n" +
                    "\tSET \"Name\"='%s', \"Size\"='%s', \"Type\"='%s', \"Price\"='%s', \"Stock\"='%s', \"Description\"='%s'\n" +
                    "\tWHERE \"Id\"=%s;", name, size,type, price, stock, description, id);
            stmt.executeUpdate(sql);
            return Utils.createResult("successful", "Edited the item.");
        }catch (Exception e){
            try{ c.rollback(); }catch (SQLException ignored){}
        }
        return Utils.createResult("error", "Malformed Query.");
    }

    public String toggleAddToCart(){
        ADD_TO_CART_DISABLED = !ADD_TO_CART_DISABLED;
        return Utils.createResult("successful", "The api has been " + (ADD_TO_CART_DISABLED ? "disabled." : "enabled."));
    }
}

