import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
    private  static Connection connectionDB;
    private static Statement statement;

    public static void main(String[] args) throws Exception {
        System.out.println("success");
    }

    private static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connectionDB = DriverManager.getConnection("jdbc:sqlite:MyBase.db");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static{
        connect();
    }

    public static void executeQuery(String queryString) {
        try {
            statement = connectionDB.createStatement();
            statement.executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeDB() {
        try {
            connectionDB.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
