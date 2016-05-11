package ua.ipt.database;

import com.mysql.fabric.jdbc.FabricMySQLDriver;

import java.sql.*;


/**
 * Created by Roman Horilyi on 18.03.2016.
 */
public class Connector {

    private static final String HOST = "jdbc:mysql://localhost:3306/users?useUnicode=true&characterEncoding=utf8";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "YOUR_PASSWORD_HERE"; // TODO change into your own password of host

    static Connection connection;

    public static void connect() {
        System.out.println("\nConnecting with the DB...");
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);

            connection = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            if (!connection.isClosed()) {
                System.out.println("Connection with the DB has been set.");
            }
        } catch (SQLException e) {
            System.out.println("Error! Can't download a class of driver!");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                if (connection.isClosed()) {
                    System.out.println("Connection with the DB has been closed.");
                }

            } catch (SQLException sqle) {
                System.out.println("Error! Can't download a class of driver!");
                sqle.printStackTrace();

            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}