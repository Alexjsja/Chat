package logic;

import database.dbConnector;
import factories.messageFactory;
import models.Message;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static factories.jsonFactory.jsonInBytes;
import static http.HttpBlanks.*;

public enum sendibleContent {
    xhrJS("xhr.js","script",false,"xhr.js"),
    home("home.html","page",true,"home"),
    page404("404.html","page",false,"404"),
    login("login.html","page",false,"login"),
    register("register.html","page",false,"register"),
    homeJS("home.js","script",true,"home.js"),
    loginJS("login.js","script",false,"login.js"),
    registerJS("register.js","script",false,"register.js");

    private String fullPath;
    private String type;
    private boolean forAuth;
    private String mapping;

    public static Set<String> authPages = filterMappingsForAuth(true);
    public static Set<String> notAuthPages = filterMappingsForAuth(false);


    public static Set<String> filterMappingsForAuth(boolean auth){
        return Arrays.stream(sendibleContent.values()).filter(sendibleContent -> sendibleContent.forAuth==auth).map(el->el.mapping).collect(Collectors.toSet());
    }
    public static Set<String> getAllMappings(){
        return Arrays.stream(sendibleContent.values()).map(el->el.mapping).collect(Collectors.toSet());
    }

    private static sendibleContent getContentOfMapping(String mapping){
        Optional<sendibleContent> anyContent = Arrays.stream(sendibleContent.values()).filter(sendibleContent -> sendibleContent.mapping.equals(mapping)).findFirst();
        return anyContent.orElse(null);
    }

    sendibleContent(String fileName, String type, boolean auth, String mapping){
        this.mapping=mapping;
        this.forAuth = auth;
        StringBuilder sb = new StringBuilder();
        String project = System.getProperty("user.dir");
        sb.append(project);
        switch (type){
            case "page":
                sb.append("\\src\\front\\pages\\");
                break;
            case "script":
                sb.append("\\src\\front\\scripts\\");
                break;
        }
        sb.append(fileName);
        this.fullPath=sb.toString();
        this.type = type;
    }

    public static ByteBuffer getContentInBytes(String cookie,String mapping) throws Exception {

        sendibleContent content = getContentOfMapping(mapping);
        Path contentPath = Paths.get(content.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));
        String contentWithHttp =null;
        switch (content.type){
            case "page":
                switch (content.mapping){
                    //todo: dynamic pages
                }
                contentWithHttp = String.format(HeaderOK,code200, typeHtml, contentValue.getBytes().length) + contentValue;
                break;
            case "script":
                contentWithHttp = String.format(HeaderOK,code201,typeJS,contentValue.getBytes().length)+contentValue;
                break;
        }
        assert (contentWithHttp != null);
        return ByteBuffer.wrap(contentWithHttp.getBytes());
    }
    public static ByteBuffer postContentInBytes(HashMap<String,String> requestJson,String cookie,String mapping) throws Exception {
        ByteBuffer buffer = null;
        sendibleContent content = getContentOfMapping(mapping);
        switch (content){
            case home:
                messageFactory.putMes(requestJson.get("text"),cookie, LocalTime.now());
                Message m  = messageFactory.getMes(requestJson.get("text"));
                buffer = jsonInBytes(m.toJsonFormat(),cookie);
                break;
            case login:
                boolean loginSuccessful = dbConnector.userLogin(requestJson.get("name"),requestJson.get("password"));
                String logResponse = "{\"suc\":\""+loginSuccessful+"\"}";
                buffer = jsonInBytes(logResponse,requestJson.get("name"));
                break;
            case register:
                boolean registerSuccessful = dbConnector.userRegister(requestJson.get("name"),requestJson.get("password"));
                String regResponse = "{\"suc\":\""+registerSuccessful+"\"}";
                buffer = jsonInBytes(regResponse,requestJson.get("name"));
                break;
            default:
                break;
        }

        return buffer;
    }

}
