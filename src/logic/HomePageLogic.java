package logic;

import data.DataConnector;
import models.Message;
import http.HttpBuilder;
import http.HttpRequest;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Map;

import static http.HttpBuilder.*;
import static http.HttpBuilder.ContentType.*;


public class HomePageLogic {

    private static final int chatId = 1;

    //todo
    public static ByteBuffer getPage(HttpRequest request){
        return null;
    }

    public static ByteBuffer getNewMessages(HttpRequest request) throws Exception {
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

            httpResponse=new HttpBuilder(200)
                .setResponseLength(messagesInJson)
                .removeCookie("last_time")
                .setCookie("session",session)
                .setResponseType(JSON)
                .setServer().setConnection().build();
            httpResponse+=messagesInJson;
        } else {
            httpResponse =new HttpBuilder(204)
                .setCacheControl(noCache)
                .removeCookie("last_time")
                .setCookie("session",session)
                .setConnection()
                .setServer()
                .build();
        }
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer logout(HttpRequest request){
        String httpResponse = new HttpBuilder(200)
            .removeCookie("logout")
            .removeCookie("session")
            .build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
    public static ByteBuffer writeMessage(HttpRequest request) throws SQLException {
        Map<String, String> cookiesMap = request.getCookies();
        Map<String, String> requestJson = request.getJson();
        DataConnector dataConnector = request.getDataConnector();

        String text = requestJson.get("text");
        String author = cookiesMap.get("session");

        new Thread(()->{
            try {
                dataConnector.putMessage(author, chatId,text);
            } catch (Exception throwable) {
                throwable.printStackTrace();
            }
        }).start();

        String test = "kostyl";
        String httpResponse = new HttpBuilder(200)
            .setServer()
            .setResponseType(TEXT)
            .setResponseLength(test)
            .setConnection().build();
        httpResponse+=test;
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
