package server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {

    public static Connection con;
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/bantau";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "123456";

    public DAO() {
        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}