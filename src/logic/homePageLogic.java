package logic;

import database.dbConnector;
import http.httpHeader;
import models.Message;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;

public class homePageLogic {
    public static ByteBuffer getNewMessages(HashMap<String, String> cookiesMap) throws SQLException {
        String httpResponse = null;
        String lastTime = null;
        if(cookiesMap.containsKey("last_time")){
            lastTime=cookiesMap.get("last_time");
        }
        if(dbConnector.containsNewMessages(lastTime)){
            Message[] messages = dbConnector.getNewMessages(lastTime);
            StringBuilder messagesInJson = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                if (i==0)messagesInJson.append('[');
                messagesInJson.append(messages[i].toJsonFormat());
                int a = i+1;
                if (a!=messages.length)messagesInJson.append(",");
                if (a==messages.length)messagesInJson.append(']');
            }
            httpResponse=httpHeader.startBuild(200)
                    .setResponseLength(messagesInJson.length())
                    .setResponseType(httpHeader.JSON)
                    .setServer().setConnection().build();
            httpResponse+=messagesInJson.toString();
        }else {
            httpResponse = httpHeader.startBuild(204)
                    .removeCookie("last_time")
                    .setServer()
                    .build();
        }
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer homeLogout(HashMap<String, String> cookiesMap){
        String httpResponse = httpHeader
                .startBuild(200)
                .removeCookie("logout")
                .removeCookie("session")
                .build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer homePostResponse(HashMap<String, String> requestJson, HashMap<String, String> cookiesMap, String mapping)
            throws SQLException {
        if (requestJson.containsKey("text")&&cookiesMap.containsKey("session")){
            String text = requestJson.get("text");
            String author = cookiesMap.get("session");
            dbConnector.putMessage(author,text);
        }
        String httpResponse = httpHeader.startBuild(200).build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
