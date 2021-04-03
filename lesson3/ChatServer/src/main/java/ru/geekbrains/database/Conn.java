package ru.geekbrains.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn {
    private final String DRIVER = "org.sqlite.JDBC";
    private final String URL = "jdbc:sqlite:C:\\java\\Core3\\lesson3\\ChatServer\\src\\DB\\Logins.db";

    private Connection conn;

    public Conn() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public Tab getTab() {
        return new Tab(conn);
    }
}
