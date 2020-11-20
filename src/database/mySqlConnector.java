package database;

import http.cookieCipher;
import models.Message;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class mySqlConnector implements DataConnector {

    private final Connection connection;

    public mySqlConnector(Connection connection){
        this.connection=connection;
    }

    @Override
    public boolean userLogin(String mail, String password) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call userLogin(?,?,?)}");
        callableStatement.setString(1, mail);
        callableStatement.setString(2, password);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    @Override
    public boolean userRegister(String mail,String name, String password) throws SQLException {
        if (!containsMail(mail)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into users(name,password,mail,cookie) values (?,?,?,?)");

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3,mail);

            String cookie = cookieCipher.encode(name+" "+new Random().nextInt()+" "+ password);
            preparedStatement.setString(4, cookie);
            preparedStatement.execute();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getCookie(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getCookie(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.VARCHAR);
        callableStatement.execute();
        return callableStatement.getString(2);
    }

    @Override
    public boolean containsMail(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsMail(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
    @Override
    public boolean containsCookieSession(String sessionCookie) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsCookie(?,?)");
        callableStatement.setString(1, sessionCookie);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    @Override
    public void putMessage(String authorCookie, int receiverId, String text) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call putMessage(?,?,?)");
        callableStatement.setString(1, authorCookie);
        callableStatement.setInt(2, receiverId);
        callableStatement.setString(3, text);
        callableStatement.execute();
    }

    @Override
    public boolean containsNewMessages(String lastTime, int chatId) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewChatMessages(?,?,?)");
        callableStatement.setInt(1, chatId);
        callableStatement.setString(2, lastTime);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    @Override
    public boolean containsNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewMessages(?,?,?,?)");
        callableStatement.setString(1, user1Cookie);
        callableStatement.setInt(2, user2Id);
        callableStatement.setString(3, lastTime);
        callableStatement.registerOutParameter(4, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(4);
    }

    @Override
    public Message[] getNewMessages(String lastTime, int chatId) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewChatMessages(?,?)");
        callableStatement.setInt(1,chatId);
        callableStatement.setString(2,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public Message[] getStartMessages(int chatId) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getStartChatMessages(?)");
        callableStatement.setInt(1,chatId);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }
    public Message[] getStartMessages(int receiverId,String authorCookie) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getStartMessages(?,?)");
        callableStatement.setInt(1,receiverId);
        callableStatement.setString(2,authorCookie);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public Message[] getNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewMessages(?,?,?)");
        callableStatement.setString(1,user1Cookie);
        callableStatement.setInt(2,user2Id);
        callableStatement.setString(3,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public User getUserInfo(int id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getUserInfo(?)");
        callableStatement.setInt(1,id);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseUserIntoDB(resultSet);
    }

    @Override
    public boolean containsUserById(int id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsUserById(?,?)");
        callableStatement.setInt(1,id);
        callableStatement.registerOutParameter(2,Types.BOOLEAN);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    @Override
    public boolean containsUserById(String id) throws SQLException {
        if (id==null)return false;
        int id0;
        try {
            id0 = Integer.parseInt(id);
        }catch (Exception e){
            return false;
        }
        return containsUserById(id0);
    }

    private User parseUserIntoDB(ResultSet resultSet) throws SQLException {
        User user = new User();
        while (resultSet.next()){
            user.setId(resultSet.getInt("id"));
            user.setMail(resultSet.getString("mail"));
            user.setName(resultSet.getString("name"));
            user.setRole(resultSet.getString("role"));
        }
        return user;
    }

    private Message[] parseMessagesIntoDB(ResultSet resultSet) throws SQLException {
        List<Message> messageList = new ArrayList<>();
        while (resultSet.next()){
            String author = resultSet.getString("author");
            String role = resultSet.getString("role");
            String text = resultSet.getString("text");
            int authorId = resultSet.getInt("id");
            String sendTime = resultSet.getString("sendtime");
            Message message = new Message(text,author,sendTime,role,authorId);
            messageList.add(message);
        }
        if (messageList.isEmpty()){
            messageList.add(new Message("Сообщений нет,станьте первым кто его напишет",
                "SERVER","2010-10-10 10:10:10",
                "chat",99999999));
        }
        return messageList.toArray(new Message[0]);
    }

}
