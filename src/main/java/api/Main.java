package api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String [] args){
        database.Utils.getDatabaseCredentials();
        SpringApplication.run(Main.class, args);
    }
}