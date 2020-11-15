package logic;

import database.dbConnector;
import http.*;


import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;

import static http.httpBuilder.*;

//todo split to different files
public class loginAndRegisterLogic {
    public static ByteBuffer loginOrRegister(HashMap<String,String> requestJson,
                                             HashMap<String,String> cookiesMap,
                                             String mapping) throws SQLException {
        boolean success = false;
        String name = requestJson.get("name");
        String password = requestJson.get("password");
        if(mapping.equals("login")) {
            success = dbConnector.userLogin(name,password);
        }else if(mapping.equals("register")) {
            success = dbConnector.userRegister(name,password);
        }
        String successResponse = "{\"suc\":"+success+"}";
        String httpResponse;
        if(success){
            httpResponse = new httpBuilder(200)
                    .setResponseLength(successResponse)
                    .setCookie("session",dbConnector.getCookie(name))
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
