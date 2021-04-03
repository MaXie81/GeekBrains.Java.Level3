package ru.geekbrains.database;

import java.sql.*;

public class Data {
    private final String SQL_TAB_SIZE = "select count(*) as cnt from tab_login;";
    private final String SQL_ADD_LOGIN = "insert into tab_login(login, nick, password) values(?, ?, ?);";

    private Connection conn;
    private Statement sqlQuery;
    private PreparedStatement sqlPrepQuery;
    private ResultSet dataQuery;

    Data(Connection conn) {
        this.conn = conn;

        try {
            sqlQuery = conn.createStatement();

            dataQuery = sqlQuery.executeQuery(SQL_TAB_SIZE);

            if (dataQuery.getInt("cnt") == 0) {
                sqlPrepQuery = conn.prepareStatement(SQL_ADD_LOGIN);

                for (int i = 1; i <= 3; i++) {
                    sqlPrepQuery.setString(1, "log" + i);
                    sqlPrepQuery.setString(2, "user" + i);
                    sqlPrepQuery.setString(3, "pass" + i);
                    sqlPrepQuery.addBatch();
                }
                sqlPrepQuery.executeBatch();

                conn.commit();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getPK(String login) {
        final String SQL_GET_ID = "select id from tab_login where lower(login) = lower(?)";
        int i = 0;

        try {
            sqlPrepQuery = conn.prepareStatement(SQL_GET_ID);

            sqlPrepQuery.setString(++i, login);

            dataQuery = sqlPrepQuery.executeQuery();
            if (dataQuery.next()) return dataQuery.getInt("id");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }
    public ResultSet getRowByPK(int id) {
        final String SQL_GET_ROW = "select login, nick, password from tab_login where id = ?;";
        int i = 0;

        try {
            sqlPrepQuery = conn.prepareStatement(SQL_GET_ROW);

            sqlPrepQuery.setInt(++i, id);

            return sqlPrepQuery.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public ResultSet getRows(String id, String login, String nick) {
        String sqlGetRows =
            "select id, login, nick, password from tab_login " +
            "where id = ? "  +
            "and login = ? " +
            "and nick = ?;";

        if (id == null) sqlGetRows = sqlGetRows.replace("id = ?", "id = id");
        if (login == null) sqlGetRows = sqlGetRows.replace("login = ?", "login = login");
        if (nick == null) sqlGetRows = sqlGetRows.replace("nick = ?", "nick = nick");

        final String SQL_GET_ROWS = sqlGetRows;
        int i = 0;

        try {
            sqlPrepQuery = conn.prepareStatement(SQL_GET_ROWS);

            if (id != null) sqlPrepQuery.setInt(++i, Integer.valueOf(id));
            if (login != null) sqlPrepQuery.setString(++i, login);
            if (nick != null) sqlPrepQuery.setString(++i, nick);

            dataQuery = sqlPrepQuery.executeQuery();

            return dataQuery;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    public void setRowByPK(int id, String login, String nick, String password) {
        String sqlSetRow =
            "update tab_login set " +
            "login = ?, " +
            "nick = ?, " +
            "password = ? " +
            "where id = ?;";

        if (login == null) sqlSetRow = sqlSetRow.replace("login = ?", "login = login");
        if (nick == null) sqlSetRow = sqlSetRow.replace("nick = ?", "nick = nick");
        if (password == null) sqlSetRow = sqlSetRow.replace("password = ?", "password = password");

        final String SQL_SET_ROW = sqlSetRow;
        int i = 0;

        try {
            sqlPrepQuery = conn.prepareStatement(SQL_SET_ROW);
            
            if (login != null) sqlPrepQuery.setString(++i, login);
            if (nick != null) sqlPrepQuery.setString(++i, nick);
            if (password != null) sqlPrepQuery.setString(++i, password);
            sqlPrepQuery.setInt(++i, id);
            
            sqlPrepQuery.execute();

            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
