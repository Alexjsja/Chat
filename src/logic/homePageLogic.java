package logic;

import database.dbConnector;
import models.Message;
import http.httpBuilder;
import models.RequestQueue;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Map;

import static http.httpBuilder.JSON;
import static http.httpBuilder.TEXT;


public class homePageLogic {

    private static final int chatId = 1;

    //todo
    public static ByteBuffer getPage(RequestQueue request){
        return null;
    }

    public static ByteBuffer getNewMessages(RequestQueue request) throws SQLException {
        String httpResponse = null;
        Map<String, String> cookiesMap = request.getCookies();

        String lastTime = cookiesMap.get("last_time");
        String session = cookiesMap.get("session");
        Message[] messages = null;
        if (lastTime.equals("start")){
            messages = dbConnector.getStartMessages(chatId);
        }else if(dbConnector.containsNewMessages(lastTime, chatId)){
            messages = dbConnector.getNewMessages(lastTime, chatId);
        }

        if(messages!=null){
            StringBuilder messagesInJson = new StringBuilder();
            for (int i = 0; i < messages.length; i++) {
                if (i==0)messagesInJson.append('[');
                messagesInJson.append(messages[i].toJsonFormat());
                if (i+1!=messages.length)messagesInJson.append(",");
                if (i+1==messages.length)messagesInJson.append(']');
            }
            httpResponse=new httpBuilder(200)
                .setResponseLength(messagesInJson.toString())
                .removeCookie("last_time")
                .setCookie("session",session)
                .setResponseType(JSON)
                .setServer().setConnection().build();
            httpResponse+=messagesInJson.toString();
        } else {
            httpResponse =new httpBuilder(204)
                .removeCookie("last_time")
                .setCookie("session",session)
                .setConnection()
                .setServer()
                .build();
        }
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer logout(RequestQueue request){
        String httpResponse = new httpBuilder(200)
            .removeCookie("logout")
            .removeCookie("session")
            .build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer writeMessage(RequestQueue request)
        throws SQLException {
        Map<String, String> cookiesMap = request.getCookies();
        Map<String, String> requestJson = request.getJson();

        String text = requestJson.get("text");
        String author = cookiesMap.get("session");
        //fixme
        dbConnector.putMessage(author, chatId,text);
        String test = "костыль";
        String httpResponse = new httpBuilder(200)
            .setServer()
            .setResponseType(TEXT)
            .setResponseLength(test)
            .setConnection().build();
        httpResponse+=test;
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
