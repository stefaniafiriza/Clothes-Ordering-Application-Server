package database;

import java.sql.Connection;
import java.sql.DriverManager;
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
}
