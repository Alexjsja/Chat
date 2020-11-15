package database;

import com.mysql.cj.xdevapi.JsonArray;
import com.mysql.cj.xdevapi.JsonParser;
import com.mysql.cj.xdevapi.JsonString;
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

    public static boolean userLogin(String name, String password) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call userLogin(?,?,?)}");
        callableStatement.setString(1, name);
        callableStatement.setString(2, password);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    public static boolean userRegister(String name, String password) throws SQLException {
        if (!containsUser(name)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into users(name,password,cookie) values (?,?,?)");

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            String cookie = cookieCipher.encode(name + " 1trap1 " + password);
            preparedStatement.setString(3, cookie);
            preparedStatement.execute();
            return true;
        } else {
            return false;
        }
    }

    public static String getCookie(String name) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getCookie(?,?)");
        callableStatement.setString(1, name);
        callableStatement.registerOutParameter(2, Types.VARCHAR);
        callableStatement.execute();
        return callableStatement.getString(2);
    }

    public static boolean containsUser(String name) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsUser(?,?)");
        callableStatement.setString(1, name);
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
            String text = resultSet.getString("text");
            String sendTime = resultSet.getString("sendtime");
            Message message = new Message(text,author,sendTime);
            messageList.add(message);
        }
        return messageList.toArray(new Message[0]);
    }
}