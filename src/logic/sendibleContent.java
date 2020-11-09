package logic;

import database.dbConnector;
import factories.messageFactory;
import http.httpHeader;
import models.Message;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum sendibleContent {
    xhrJS("xhr.js","script",false,"xhr.js"),
    home("home.html","page",true,"home"),
    page404("404.html","page",false,"404"),
    login("login.html","page",false,"login"),
    register("register.html","page",false,"register"),
    homeJS("home.js","script",true,"home.js"),
    loginJS("login.js","script",false,"login.js"),
    registerJS("register.js","script",false,"register.js");

    private final String fullPath;
    private final String type;
    private final boolean forAuth;
    private final String mapping;

    public static Set<String> authPages = filterMappingsForAuth(true);
    public static Set<String> notAuthPages = filterMappingsForAuth(false);


    public static Set<String> filterMappingsForAuth(boolean auth){
        return Arrays.stream(sendibleContent.values()).filter(sendibleContent -> sendibleContent.forAuth==auth).map(el->el.mapping).collect(Collectors.toSet());
    }

    private static sendibleContent getContentOfMapping(String mapping){
        Optional<sendibleContent> anyContent = Arrays.stream(sendibleContent.values()).filter(sendibleContent -> sendibleContent.mapping.equals(mapping)).findFirst();
        return anyContent.orElse(page404);
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
        boolean auth = dbConnector.containsUser(cookie);
        Path contentPath = Paths.get(content.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));
        String responseWithHttp =null;

        if(content.type.equals("page")){
            if(!auth&&authPages.contains(content.mapping)){
                responseWithHttp = httpHeader
                        .startBuild(301)
                        .setRedirect("/register")
                        .build();
            }else if(content==page404){
                responseWithHttp = httpHeader
                        .startBuild(404)
                        .setResponseType(httpHeader.HTML)
                        .setResponseLength(contentValue.getBytes().length)
                        .build();
                responseWithHttp += contentValue;
            } else {
                switch (content.mapping){
                    //todo: dynamic pages
                }
                responseWithHttp = httpHeader
                        .startBuild(200)
                        .setResponseLength(contentValue.getBytes().length)
                        .setResponseType(httpHeader.HTML)
                        .setConnection()
                        .setServer()
                        .build();
                responseWithHttp += contentValue;
            }
        }else if(content.type.equals("script")){
            responseWithHttp = httpHeader
                    .startBuild(200)
                    .setResponseLength(contentValue.getBytes().length)
                    .setResponseType(httpHeader.JS)
                    .setConnection()
                    .setServer()
                    .build();
            responseWithHttp+=contentValue;
        }


        assert (responseWithHttp != null);
        return ByteBuffer.wrap(responseWithHttp.getBytes());
    }


    public static ByteBuffer postContentInBytes(HashMap<String,String> requestJson,String cookie,String mapping) throws Exception {
        ByteBuffer buffer = null;
       /* sendibleContent content = getContentOfMapping(mapping);
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
        }*/

        return buffer;
    }

}
