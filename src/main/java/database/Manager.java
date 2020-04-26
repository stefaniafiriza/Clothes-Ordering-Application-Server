package database;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Manager {
    Connection c;
    Statement stmt;
    boolean connected = false;

    public Manager() {
        this.connect();
    }

    public void connect() {
        int retry = 5;
        while (!connected && retry > 0) {
            try {
                Class.forName("org.postgresql.Driver");
                this.c = DriverManager
                        .getConnection("jdbc:postgresql://localhost:5432/ClothesOrderingApp",
                                Utils.DatabaseUser, Utils.DatabasePassword);
                this.c.setAutoCommit(false);
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
            String user = Utils.convertToJSON(rs);
            String password = user.substring(0, user.indexOf('{'));
            user = "{" + user.substring(user.indexOf('{'));
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
            String sql = String.format("INSERT INTO \"Users\"(\n" +
                            "\t\"Id\", \"Name\", \"Code Manager\", \"Type\", \"Email\", \"PhoneNumber\", \"Username\", \"Password\")\n" +
                            "\tVALUES (%s, '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    this.generateID("Users", "Id").toString(), escapeString(name), escapeString(codeManager), escapeString(type),
                    escapeString(email), escapeString(phoneNumber), escapeString(username), escapeString(password));
            stmt.executeUpdate(sql);
            c.commit();
        } catch (Exception e) {
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
            c.commit();
            return Utils.createResult("successful", String.format("%s", id));
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }

    }
    public String getProducts() {
        while (!this.connected) {
            this.connect();
        }
        try {
            String sql = "SELECT * FROM \"PRODUCTS\";";
            ResultSet rs = stmt.executeQuery(sql);
            return Utils.convertToJSON(rs);
        } catch (Exception e) {
            return Utils.createResult("error", "Malformed Query");
        }
    }
}
