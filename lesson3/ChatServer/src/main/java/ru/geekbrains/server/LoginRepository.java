package ru.geekbrains.server;

import java.sql.ResultSet;
import java.sql.SQLException;

import ru.geekbrains.database.*;

public class LoginRepository {
    Conn conn = new Conn();
    Tab tab = conn.getTab();
    Data data = tab.getData();

    ResultSet resultSet;

    public void close() {conn.close();}

    public int getId(String login, String password) {
        int id = data.getPK(login);
        if (id == -1) return -1;

        try {
            resultSet = data.getRowByPK(id);
            if (resultSet.next())
                if (resultSet.getString("password").equals(password))
                    return id;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }
    public String getNickById(int id) {
        try {
            resultSet = data.getRowByPK(id);
            return resultSet.getString("nick");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }
    public boolean setNickForId(int id, String newNick) {
        try {
            resultSet = data.getRows(null, null, newNick);

            if (resultSet.next())
                return false;

            data.setRowByPK(id, null, newNick, null);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

}
