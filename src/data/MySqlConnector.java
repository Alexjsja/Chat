package data;

import http.CookieCipher;
import models.Message;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MySqlConnector implements DataConnector {

    private final Connection connection;

    public MySqlConnector(Connection connection){
        this.connection=connection;
    }

    @Override
    public synchronized boolean userLogin(String mail, String password) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call userLogin(?,?,?)}");
        callableStatement.setString(1, mail);
        callableStatement.setString(2, password);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    @Override
    public synchronized boolean userRegister(String mail,String name, String password) throws SQLException {
        if (!containsMail(mail)) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into users(name,password,mail,cookie) values (?,?,?,?)");

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3,mail);

            String cookie = CookieCipher.encode(name+" "+new Random().nextInt()+" "+ password);
            preparedStatement.setString(4, cookie);
            preparedStatement.execute();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized String getCookie(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getCookie(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.VARCHAR);
        callableStatement.execute();
        return callableStatement.getString(2);
    }

    @Override
    public synchronized boolean containsMail(String mail) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsMail(?,?)");
        callableStatement.setString(1, mail);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
    @Override
    public synchronized boolean containsCookieSession(String sessionCookie) throws SQLException {
        if (sessionCookie==null)return false;
        CallableStatement callableStatement = connection.prepareCall("call containsCookie(?,?)");
        callableStatement.setString(1, sessionCookie);
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    @Override
    public synchronized void putMessage(String authorCookie, int receiverId, String text) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call putMessage(?,?,?)");
        callableStatement.setString(1, authorCookie);
        callableStatement.setInt(2, receiverId);
        callableStatement.setString(3, text);
        callableStatement.execute();
    }

    @Override
    public synchronized boolean containsNewMessages(String lastTime, int chatId) throws SQLException{
        CallableStatement callableStatement = connection.prepareCall("call containsNewChatMessages(?,?,?)");
        callableStatement.setInt(1, chatId);
        callableStatement.setString(2, lastTime);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }

    @Override
    public synchronized boolean containsNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsNewMessages(?,?,?,?)");
        callableStatement.setString(1, user1Cookie);
        callableStatement.setInt(2, user2Id);
        callableStatement.setString(3, lastTime);
        callableStatement.registerOutParameter(4, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(4);
    }

    @Override
    public synchronized Message[] getNewMessages(String lastTime, int chatId) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewChatMessages(?,?)");
        callableStatement.setInt(1,chatId);
        callableStatement.setString(2,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public synchronized Message[] getStartMessages(int chatId) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getStartChatMessages(?)");
        callableStatement.setInt(1,chatId);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }
    public synchronized Message[] getStartMessages(int receiverId,String authorCookie) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getStartMessages(?,?)");
        callableStatement.setInt(1,receiverId);
        callableStatement.setString(2,authorCookie);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public synchronized Message[] getNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getNewMessages(?,?,?)");
        callableStatement.setString(1,user1Cookie);
        callableStatement.setInt(2,user2Id);
        callableStatement.setString(3,lastTime);
        ResultSet resultSet = callableStatement.executeQuery();
        return parseMessagesIntoDB(resultSet);
    }

    @Override
    public synchronized User getUserInfo(int id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call getUserInfo(?)");
        callableStatement.setInt(1,id);
        ResultSet resultSet = callableStatement.executeQuery();

        return parseUserIntoDB(resultSet);
    }

    @Override
    public User getUserInfo(String cookie) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement(
            "select name,role,id,mail from users where cookie=?");

        preparedStatement.setString(1, cookie);
        ResultSet resultSet = preparedStatement.executeQuery();
        return parseUserIntoDB(resultSet);
    }

    @Override
    public synchronized void updateUserInfo(String name, String password, String mail, String cookie) throws Exception {
        return;
    }

    @Override
    public synchronized boolean containsUserById(int id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("call containsUserById(?,?)");
        callableStatement.setInt(1,id);
        callableStatement.registerOutParameter(2,Types.BOOLEAN);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }

    @Override
    public synchronized boolean containsUserById(String id) throws SQLException {
        if (id==null)return false;
        int id0;
        try {
            id0 = Integer.parseInt(id);
        }catch (Exception e){
            return false;
        }
        return containsUserById(id0);
    }
    private synchronized User parseUserIntoDB(ResultSet resultSet) throws SQLException {
        resultSet.next();
        return User.newUser()
            .setId(resultSet.getInt("id"))
            .setMail(resultSet.getString("mail"))
            .setRole(resultSet.getString("role"))
            .setName(resultSet.getString("name")).build();
    }

    private synchronized Message[] parseMessagesIntoDB(ResultSet resultSet) throws SQLException {
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
