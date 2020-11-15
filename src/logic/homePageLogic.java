package logic;

import database.dbConnector;
import models.Message;
import http.httpBuilder;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;

import static http.httpBuilder.JSON;


public class homePageLogic {

    private static final String chatName = "home-chat";

    //todo
    public static ByteBuffer getPage(HashMap<String, String> cookiesMap){
        return null;
    }

    public static ByteBuffer getNewMessages(HashMap<String, String> cookiesMap) throws SQLException {
        String httpResponse = null;
        String lastTime = cookiesMap.get("last_time");
        if(dbConnector.containsNewMessages(lastTime,chatName)){
            Message[] messages = dbConnector.getNewMessages(lastTime,chatName);
            StringBuilder messagesInJson = new StringBuilder();
            //fixme
            for (int i = 0; i < messages.length; i++) {
                if (i==0)messagesInJson.append('[');
                messagesInJson.append(messages[i].toJsonFormat());
                if (i+1!=messages.length)messagesInJson.append(",");
                if (i+1==messages.length)messagesInJson.append(']');
            }
            httpResponse=new httpBuilder(200)
                .setResponseLength(messagesInJson.toString())
                .setResponseType(JSON)
                .setServer().setConnection().build();
            httpResponse+=messagesInJson.toString();
        }else {
            httpResponse =new httpBuilder(204)
                .removeCookie("last_time")
                .setConnection()
                .setServer()
                .build();
        }
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer logout(HashMap<String, String> cookiesMap){
        String httpResponse = new httpBuilder(200)
            .removeCookie("logout")
            .removeCookie("session")
            .build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer writeMessage(HashMap<String, String> requestJson, HashMap<String, String> cookiesMap, String mapping)
        throws SQLException {
        String text = requestJson.get("text");
        String author = cookiesMap.get("session");
        dbConnector.putMessage(author,chatName,text);
        String httpResponse = new httpBuilder(200).build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
