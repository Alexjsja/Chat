package database;

import http.cookieCipher;
import models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class dbConnector {

    private static Connection connection;


    public static void connect(Connection connect) {
        connection = connect;
    }

    public static boolean userLogin(String mail, String password) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call userLogin(?,?,?)}");
        callableStatement.setString(1, mail);
        callableStatement.setString(2, password);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    public static boolean userRegister(String mail,String name, String password) throws SQLException {
        if (!containsMail(mail)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into users(name,password,mail,cookie) values (?,?,?,?)");

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3,mail);
            String cookie = cookieCipher.encode(name + " 1trap1 " + password);
            preparedStatement.setString(4, cookie);
            preparedStatement.execute();
            return true;
        } else {
            return false;
        }
    }

    public static String getCookie(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getCookie(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.VARCHAR);
        callableStatement.execute();
        return callableStatement.getString(2);
    }

    public static boolean containsMail(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsMail(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    public static boolean containsCookieSession(String sessionCookie) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsCookie(?,?)");
        callableStatement.setString(1, sessionCookie);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    public static void putMessage(String authorCookie, String receiver, String text) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call putMessage(?,?,?)");
        callableStatement.setString(1, authorCookie);
        callableStatement.setString(2, receiver);
        callableStatement.setString(3, text);
        callableStatement.execute();
    }

    public static boolean containsNewMessages(String lastTime, String chat) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewChatMessages(?,?,?)");
        callableStatement.setString(1, chat);
        callableStatement.setString(2, lastTime);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    public boolean containsNewMessages(String lastTime, String user1, String user2) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewMessages(?,?,?,?)");
        callableStatement.setString(1, user1);
        callableStatement.setString(2, user2);
        callableStatement.setString(3, lastTime);
        callableStatement.registerOutParameter(4, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(4);
    }

    public static Message[] getNewMessages(String lastTime, String chat) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewChatMessages(?,?)");
        callableStatement.setString(1,chat);
        callableStatement.setString(2,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    public static Message[] getNewMessages(String lastTime, String user1, String user2) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewMessages(?,?,?)");
        callableStatement.setString(1,user1);
        callableStatement.setString(2,user2);
        callableStatement.setString(3,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    private static Message[] parseMessagesIntoDB(ResultSet resultSet) throws SQLException {
        List<Message> messageList = new ArrayList<>();
        while (resultSet.next()){
            String author = resultSet.getString("author");
            String role = resultSet.getString("role");
            String text = resultSet.getString("text");
            String sendTime = resultSet.getString("sendtime");
            Message message = new Message(text,author,sendTime,role);
            messageList.add(message);
        }
        return messageList.toArray(new Message[0]);
    }
}