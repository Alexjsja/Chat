package logic;

import database.dbConnector;
import http.*;


import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;

import static http.httpBuilder.*;

public class loginAndRegisterLogic {
    public static ByteBuffer loginOrRegister(HashMap<String,String> requestJson,
                                             HashMap<String,String> cookiesMap,
                                             String mapping) throws SQLException {
        boolean success = false;
        if(mapping.equals("login")) {
            success = dbConnector.userLogin(requestJson.get("name"),requestJson.get("password"));
        }else if(mapping.equals("register")) {
            success = dbConnector.userRegister(requestJson.get("name"),requestJson.get("password"));
        }
        String successResponse = "{\"suc\":"+success+"}";
        String httpResponse;
        if(success){
            String namePass = "user:"+requestJson.get("name")+" password:"+requestJson.get("password");
            String cookieCode = cookieCipher.encode(namePass);
            httpResponse = new httpBuilder(200)
                    .setResponseLength(successResponse)
                    .setCookie("session",cookieCode)
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
