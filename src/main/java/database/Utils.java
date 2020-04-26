package database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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
    public static String createResult(String type, String result){
        return"{\n" +
                "    \"result\":\n" +
                "    {\n" +
                String.format("        \"type\":\"%s\",\n",type) +
                String.format("        \"message\":\"%s\"\n", result) +
                "    }\n" +
                "}";
    }
    public static String convertToJSON(ResultSet rs) throws SQLException
    {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        String pass = "";
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<numColumns+1; i++) {
                String column_name = rsmd.getColumnName(i);
                if (column_name.equals("Password")) {
                    pass = rs.getString("Password");
                    continue;
                }
                if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                    obj.put(column_name, rs.getArray(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                    obj.put(column_name, rs.getBoolean(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                    obj.put(column_name, rs.getBlob(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                    obj.put(column_name, rs.getDouble(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                    obj.put(column_name, rs.getFloat(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                    obj.put(column_name, rs.getNString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                    obj.put(column_name, rs.getString(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                    obj.put(column_name, rs.getInt(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
                    obj.put(column_name, rs.getDate(column_name));
                }
                else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                    obj.put(column_name, rs.getTimestamp(column_name));
                }
                else{
                    obj.put(column_name, rs.getObject(column_name));
                }
            }
            json.add(obj);
        }
        return pass+json.toJSONString();
    }
}
