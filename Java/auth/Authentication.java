package auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import mysql.SqlConnectionFactory;
import utility.*;

public class Authentication {
    private Connection con;
    private static final Map<String, String> CACHE = new HashMap<>();

    Authentication() throws Exception {
        con = SqlConnectionFactory.getConnection();
    }

    public static Authentication getInstance() throws Exception {
        return new Authentication();
    }

    public String verifyPasswordAndCreateTicket(String usermail, String password) throws Exception {
        String ticket = "";
        String storedPassHash = "";
        String providedPassHash = Utils.hashHex(password);

        PreparedStatement PreparedStatement = con.prepareStatement("SELECT pass_hash FROM Users WHERE mail = ?");
        PreparedStatement.setString(1, usermail);

        ResultSet rs = PreparedStatement.executeQuery();
        if (rs.next()) {
            storedPassHash = rs.getString("pass_hash");
        }

        if (storedPassHash.equals(providedPassHash)) {
            ticket = Utils.hashHex(String.valueOf(System.currentTimeMillis()));
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO Session (mail, ticket) VALUES ( ?, ? )");
            preparedStatement.setString(1, usermail);
            preparedStatement.setString(2, ticket);

            preparedStatement.executeUpdate();
        }

        return ticket;
    }

    public boolean isVaildTicket(String mail, String ticket) throws Exception {
        if (!Utils.stringIsEmpty(mail) && !Utils.stringIsEmpty(ticket)) {
            if (CACHE.getOrDefault(mail, "").equals(ticket)) {
                return true;
            }
            PreparedStatement preparedStatement = con.prepareStatement("SELECT ticket FROM Session WHERE mail = ?");
            preparedStatement.setString(1, mail);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                if (ticket.equals(result.getString("ticket"))) {
                    CACHE.put(mail, ticket);
                    return true;
                }
            }
        }

        return false;
    }

    public void removeSessionTicket(String mail) throws Exception {
        PreparedStatement preparedStatement = con.prepareStatement("DELETE FROM Session WHERE mail = ?");
        preparedStatement.setString(1, mail);

        preparedStatement.executeUpdate();
    }

    public boolean userExists(String mail) throws Exception {
        PreparedStatement PreparedStatement = con.prepareStatement("SELECT pass_hash FROM Users WHERE mail = ?");
        PreparedStatement.setString(1, mail);

        ResultSet rs = PreparedStatement.executeQuery();

        return rs.next();
    }

    public boolean addNewUser(String mail, String pass) throws Exception {
        if (userExists(mail)) {
            return false;
        }


        PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO Users (mail, pass_hash) VALUES ( ?, ? )");
        preparedStatement.setString(1, mail);
        preparedStatement.setString(2, pass);

        preparedStatement.executeUpdate();

        return true;
    }
}