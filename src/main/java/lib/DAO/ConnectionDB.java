package lib.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDB {

    private ConnectionDB(){}

    private static Connection conn = null;

    public static Connection getConn(){
        if(conn == null){
            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medcloud_patients", "root", "admin");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

}