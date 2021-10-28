package lib.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class ReplicaDB {

    public ReplicaDB() {}

    private static Connection conn = null;

    public static Connection getConn(){
        if(conn == null){
            try {
                conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/medcloud_patients_replica", "root", "admin");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

}
