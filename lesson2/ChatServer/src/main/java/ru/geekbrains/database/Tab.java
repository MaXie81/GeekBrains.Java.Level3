package ru.geekbrains.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tab {
    private final String SQL_TAB_EXIST = "select count(*) as cnt from sqlite_master where lower(name) = 'tab_login';";
    private final String SQL_TAB_CREATE = "create table tab_login(id integer primary key, login text unique, nick text unique, password text);";

    private final Connection conn;
    private Statement sqlQuery;
    private ResultSet dataQuery;

    public Tab(Connection conn) {
        this.conn = conn;

        try {
            sqlQuery = conn.createStatement();

            dataQuery = sqlQuery.executeQuery(SQL_TAB_EXIST);

            if (dataQuery.getInt("cnt") == 0)
                sqlQuery.executeUpdate(SQL_TAB_CREATE);

            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public Data getData() {
        return new Data(conn);
    }
}
