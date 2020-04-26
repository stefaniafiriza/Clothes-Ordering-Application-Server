package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
}
