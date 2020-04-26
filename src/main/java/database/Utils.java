package database;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static String DatabaseUser = "";
    public static String DatabasePassword = "";
    public static String API_KEY = "";

    public static void getDatabaseCredentials(){
        try {
            JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader("secret.json"));
            Utils.DatabaseUser =(String) obj.get("username");
            Utils.DatabasePassword =(String) obj.get("password");
            Utils.API_KEY = (String) obj.get("api_key");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
