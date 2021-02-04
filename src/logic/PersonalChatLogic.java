package logic;

import data.DataConnector;
import http.HttpBuilder;
import models.Message;
import http.HttpRequest;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Map;

import static http.HttpBuilder.ContentType.*;

public class PersonalChatLogic {

    public static ByteBuffer getNewMessages(HttpRequest request) throws Exception {
        String httpResponse = null;
        Map<String, String> cookiesMap = request.getCookies();
        Map<String, String> parameters = request.getParameters();
        DataConnector dataConnector = request.getDataConnector();

        String lastTime = cookiesMap.get("last_time");
        String session = cookiesMap.get("session");
        String userId = parameters.get("id");

        int receiverId = Integer.parseInt(userId);
        Message[] messages = null;
        if (lastTime.equals("start")){
            messages = dataConnector.getStartMessages(receiverId,session);
        }else if(dataConnector.containsNewMessages(lastTime,session,receiverId)){
            messages = dataConnector.getNewMessages(lastTime,session,receiverId);
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
                .removeCookie("last_time")
                .setCookie("session",session)
                .setConnection()
                .setServer()
                .build();
        }
        return ByteBuffer.wrap(httpResponse.getBytes());
    }

    public static ByteBuffer writeMessage(HttpRequest request)
        throws SQLException {
        Map<String, String> cookiesMap = request.getCookies();
        Map<String, String> parameters = request.getParameters();
        Map<String, String> requestJson = request.getJson();
        DataConnector dataConnector = request.getDataConnector();

        String text = requestJson.get("text");
        String author = cookiesMap.get("session");
        int receiverId = Integer.parseInt(parameters.get("id"));

        new Thread(()->{
            try {
                dataConnector.putMessage(author, receiverId,text);
            } catch (Exception throwable) {
                throwable.printStackTrace();
            }
        }).start();

        String test = "костыль";
        String httpResponse = new HttpBuilder(200)
            .setServer()
            .setResponseType(TEXT)
            .setResponseLength(test)
            .setConnection().build();
        httpResponse+=test;
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
