package logic;

import database.DataConnector;
import models.Message;
import http.httpBuilder;
import models.RequestQueue;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Map;

import static http.httpBuilder.*;


public class homePageLogic {

    private static final int chatId = 1;

    //todo
    public static ByteBuffer getPage(RequestQueue request){
        return null;
    }

    public static ByteBuffer getNewMessages(RequestQueue request) throws SQLException {
        String httpResponse = null;
        Map<String, String> cookiesMap = request.getCookies();
        DataConnector dataConnector = request.getDataConnector();

        String lastTime = cookiesMap.get("last_time");
        String session = cookiesMap.get("session");
        Message[] messages = null;

        if (lastTime.equals("start")){
            messages = dataConnector.getStartMessages(chatId);
        }else if(dataConnector.containsNewMessages(lastTime, chatId)){
            messages = dataConnector.getNewMessages(lastTime, chatId);
        }

        if(messages!=null){

            String messagesInJson = Message.toJsonArray(messages);

            httpResponse=new httpBuilder(200)
                .setResponseLength(messagesInJson)
                .removeCookie("last_time")
                .setCookie("session",session)
                .setResponseType(JSON)
                .setServer().setConnection().build();
            httpResponse+=messagesInJson.toString();
        } else {
            httpResponse =new httpBuilder(204)
                .setCacheControl(noCache)
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
        DataConnector dataConnector = request.getDataConnector();

        String text = requestJson.get("text");
        String author = cookiesMap.get("session");

        new Thread(()->{
            try {
                dataConnector.putMessage(author, chatId,text);
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }).start();

        String test = "kostyl";
        String httpResponse = new httpBuilder(200)
            .setServer()
            .setResponseType(TEXT)
            .setResponseLength(test)
            .setConnection().build();
        httpResponse+=test;
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
