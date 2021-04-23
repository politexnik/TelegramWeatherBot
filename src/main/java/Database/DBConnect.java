package Database;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class DBConnect {
    private  static Connection connectionDB;
    private static Statement statement;

    private static void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            connectionDB = DriverManager.getConnection("jdbc:sqlite:UserBase.db");
            System.out.println("Connection success");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static{
        connect();
    }

    public static ResultSet executeSelectQuery(String queryString) {
        try {
            statement = connectionDB.createStatement();
            return statement.executeQuery(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int executeUpdateQuery(String queryString) {
        try {
            statement = connectionDB.createStatement();
            return statement.executeUpdate(queryString);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
