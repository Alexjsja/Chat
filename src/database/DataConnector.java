package database;

import models.Message;
import models.User;

import java.sql.SQLException;


public interface DataConnector {

    boolean userLogin(String mail, String password) throws SQLException;

    boolean userRegister(String mail,String name, String password) throws SQLException;

    String getCookie(String mail) throws SQLException;

    boolean containsMail(String mail) throws SQLException;

    boolean containsCookieSession(String sessionCookie) throws SQLException;

    void putMessage(String authorCookie, int receiverId, String text) throws SQLException;

    boolean containsNewMessages(String lastTime, int chatId) throws SQLException;

    boolean containsNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException;

    Message[] getNewMessages(String lastTime, int chatId) throws SQLException;
    //fixme
    Message[] getNewMessages(String lastTime, String user1Cookie, int user2Id) throws SQLException;

    Message[] getStartMessages(int chatId) throws SQLException;

    public Message[] getStartMessages(int receiverId,String authorCookie) throws SQLException;

    User getUserInfo(int id) throws SQLException;

    boolean containsUserById(int id) throws SQLException;

    boolean containsUserById(String id) throws SQLException;

}