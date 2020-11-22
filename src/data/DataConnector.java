package data;

import models.Message;
import models.User;


public interface DataConnector {

    boolean userLogin(String mail, String password) throws Exception;

    boolean userRegister(String mail,String name, String password) throws Exception;

    String getCookie(String mail) throws Exception;

    boolean containsMail(String mail) throws Exception;

    boolean containsCookieSession(String sessionCookie) throws Exception;

    void putMessage(String authorCookie, int receiverId, String text) throws Exception;

    boolean containsNewMessages(String lastTime, int chatId) throws Exception;

    boolean containsNewMessages(String lastTime, String user1Cookie, int user2Id) throws Exception;

    Message[] getNewMessages(String lastTime, int chatId) throws Exception;

    Message[] getNewMessages(String lastTime, String user1Cookie, int user2Id) throws Exception;

    Message[] getStartMessages(int chatId) throws Exception;

    Message[] getStartMessages(int receiverId,String authorCookie) throws Exception;

    User getUserInfo(int id) throws Exception;

    User getUserInfo(String cookie) throws Exception;

    void updateUserInfo(String name,String password,String mail,String cookie) throws Exception;

    boolean containsUserById(int id) throws Exception;

    boolean containsUserById(String id) throws Exception;

}