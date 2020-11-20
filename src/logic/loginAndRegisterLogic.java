package logic;

import database.DataConnector;
import http.*;
import models.RequestQueue;


import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Map;

import static http.httpBuilder.*;

//todo split to different files
public class loginAndRegisterLogic {
    public static ByteBuffer loginOrRegister(RequestQueue request) throws SQLException {
        boolean success = false;
        String mapping = request.getMapping();
        Map<String, String> requestJson = request.getJson();
        DataConnector dataConnector = request.getDataConnector();

        String mail =  requestJson.get("mail");
        String password = requestJson.get("password");
        if(mapping.equals("login")) {
            success = dataConnector.userLogin(mail,password);
        }else if(mapping.equals("register")) {
            String name = requestJson.get("name");
            success = dataConnector.userRegister(mail,name,password);
        }
        String successResponse = "{\"suc\":"+success+"}";
        String httpResponse;
        if(success){
            httpResponse = new httpBuilder(200)
                    .setResponseLength(successResponse)
                    .setCookie("session", dataConnector.getCookie(mail))
                    .setResponseType(JSON)
                    .setConnection()
                    .setServer()
                    .build();
        }else{
            httpResponse = new httpBuilder(200)
                    .setResponseLength(successResponse)
                    .setResponseType(JSON)
                    .setConnection()
                    .setServer()
                    .build();
        }
        String fullResponse = httpResponse+successResponse;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
}
