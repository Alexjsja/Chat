package database;

import com.mysql.cj.xdevapi.JsonParser;
import models.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class dbConnector {

    private static Connection connection;


    public static void connect(Connection connect){
        connection = connect;
    }

    public static boolean userLogin(String name,String password) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call userLogin(?,?,?)}");
        callableStatement.setString(1,name);
        callableStatement.setString(2,password);
        callableStatement.registerOutParameter(3,Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }
    public static boolean userRegister(String name,String password) throws SQLException {
        if (!containsUser(name)){
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into users(name,password,cookie) values (?,?,?)");

            preparedStatement.setString(1,name);
            preparedStatement.setString(2,password);
            String cookie = name+password;
            String cookieHash = Integer.toString( cookie.hashCode());
            preparedStatement.setString(3,cookieHash);
            preparedStatement.execute();
            return true;
        }else {
            return false;
        }
    }
    public static boolean containsUser(String name) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsUser(?,?)");
        callableStatement.setString(1,name);
        callableStatement.registerOutParameter(2,Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    public static boolean containsCookieSession(String session) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsCookie(?,?)");
        callableStatement.setString(1,session);
        callableStatement.registerOutParameter(2,Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }


    //fixme
    public static void putMessage(String author,String text) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into messages(author,text,sendtime) values (?,?,now())");

        preparedStatement.setString(1,author);
        preparedStatement.setString(2,text);
        preparedStatement.execute();
    }


    public static boolean containsNewMessages(String lastTime) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewMessages(?,?)");
        callableStatement.setString(1,lastTime);
        callableStatement.registerOutParameter(2,Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
    public static Message[] getNewMessages(String lastTime) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call get50lastMesgHomeChat(?,?,?,?)");
        callableStatement.setString(1,lastTime);
        callableStatement.registerOutParameter(2,Types.VARCHAR);
        callableStatement.registerOutParameter(3,Types.VARCHAR);
//        fixme
        callableStatement.registerOutParameter(4,Types.VARCHAR);
//        fixme
        ResultSet newMessages = callableStatement.executeQuery();
        List<Message> messageList = new ArrayList<>();
        while (newMessages.next()){
            String text = newMessages.getString(1);
            String author = newMessages.getString(2);
            String sendTime = newMessages.getTimestamp(3).toString();
            messageList.add(new Message(text,author,sendTime));
        }
        return messageList.toArray(new Message[0]);
    }
}
