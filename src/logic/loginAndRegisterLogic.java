package logic;

import database.dbConnector;
import http.httpHeader;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.HashMap;

public class loginAndRegisterLogic {
    public static ByteBuffer loginAndRegisterPostResponse(HashMap<String,String> requestJson,
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
            String namePass = requestJson.get("name")+requestJson.get("password");
            String cookieHash =Integer.toString(namePass.hashCode());
            httpResponse = httpHeader.startBuild(200)
                    .setResponseLength(successResponse.getBytes().length)
                    .setCookie("session",cookieHash)
                    .setResponseType(httpHeader.JSON)
                    .setConnection()
                    .setServer()
                    .build();
        }else{
            httpResponse = httpHeader.startBuild(200)
                    .setResponseLength(successResponse.getBytes().length)
                    .setResponseType(httpHeader.JSON)
                    .setConnection()
                    .setServer()
                    .build();
        }
        String fullResponse = httpResponse+successResponse;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
}
