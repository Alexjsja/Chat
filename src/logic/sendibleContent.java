package logic;

import database.dbConnector;
import http.httpBuilder;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static http.httpBuilder.*;


public enum sendibleContent {

    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    XHR_JS("xhr.js","script",false,"xhr.js"),
    HOME_JS("home.js","script",true,"home.js"),
    LOGIN_JS("login.js","script",false,"login.js"),
    REGISTER_JS("register.js","script",false,"register.js"),
    HOME_CSS("home.css","style",true,"home.css"),
    /*<--------------------------------MODIFIED RETURNS---------------------------->*/
    LOGIN("login.html","page",false,"login"){
        @Override
        public ByteBuffer postContentInBytes(Map<String, String> requestJson, Map<String, String> cookiesMap, String mapping) throws Exception {
            return loginAndRegisterLogic.loginOrRegister(requestJson,cookiesMap,mapping);
        }
    },
    REGISTER("register.html","page",false,"register"){
        @Override
        public ByteBuffer postContentInBytes(Map<String, String> requestJson, Map<String, String> cookiesMap, String mapping) throws Exception {
            return loginAndRegisterLogic.loginOrRegister(requestJson,cookiesMap,mapping);
        }
    },
    PAGE404("404.html","page",false,"404"){
        @Override
        public ByteBuffer getContentInBytes(Map<String, String> cookiesMap) throws Exception {
            Path contentPath = Paths.get(super.fullPath);
            String contentValue = String.join("\n", Files.readAllLines(contentPath));
            String header =new httpBuilder(404)
                .setResponseLength(contentValue)
                .setResponseType(HTML).setConnection()
                .build();
            String fullResponse = header + contentValue;
            return ByteBuffer.wrap(fullResponse.getBytes());
        }
    },
    PROFILE("profile.html","page",true,"profile"),
    HOME("home.html","page",true,"home"){
        @Override
        public ByteBuffer getContentInBytes(Map<String, String> cookiesMap) throws Exception {
            if(cookiesMap.containsKey("last_time")){
                return homePageLogic.getNewMessages(cookiesMap);
            }else if (cookiesMap.containsKey("logout")){
                return homePageLogic.logout(cookiesMap);
            }else {
                return super.getContentInBytes(cookiesMap);
                //todo
//                return homePageLogic.getPage(cookiesMap);
            }
        }
        @Override
        public ByteBuffer postContentInBytes(Map<String, String> requestJson, Map<String, String> cookiesMap, String mapping) throws Exception {
            return homePageLogic.writeMessage(requestJson,cookiesMap,mapping);
        }
    };

    private final String fullPath;
    private final String type;
    private final boolean forAuth;
    private final String mapping;

    sendibleContent(String fileName, String type, boolean forAuth, String mapping){
        this.mapping=mapping;
        this.forAuth = forAuth;
        StringBuilder sb = new StringBuilder();
        String project = System.getProperty("user.dir");
        sb.append(project);
        switch (type){
            case "page": sb.append("\\src\\front\\pages\\");break;
            case "script": sb.append("\\src\\front\\scripts\\");break;
            case "style":sb.append("\\src\\front\\styles\\");break;
        }
        sb.append(fileName);
        this.fullPath=sb.toString();
        this.type = type;
    }

    public static final Set<String> authMappings = filterMappingsForAuth(true);
    public static final Set<String> notAuthMappings = filterMappingsForAuth(false);

    public static Set<String> filterMappingsForAuth(boolean auth){
        return Arrays.stream(sendibleContent.values())
            .filter(sendibleContent->sendibleContent.forAuth==auth)
            .map(el->el.mapping)
            .collect(Collectors.toSet());
    }

    public static sendibleContent getContentOfMapping(String mapping){
        Optional<sendibleContent> anyContent = Arrays.stream(sendibleContent.values())
            .filter(sendibleContent -> sendibleContent.mapping.equals(mapping))
            .findFirst();
        return anyContent.orElse(PAGE404);
    }

    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    public ByteBuffer getContentInBytes(Map<String,String> cookiesMap) throws Exception {
        boolean authentic;
        if (cookiesMap.containsKey("session")){
            authentic = dbConnector.containsCookieSession(cookiesMap.get("session"));
        }else {
            authentic = false;
        }
        Path contentPath = Paths.get(this.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));
        String fullResponse = null;

        if(!authentic&&authMappings.contains(this.mapping)){
            fullResponse =new httpBuilder(307)
                .setRedirect("/register")
                .build();
        }else if(this.type.equals("page")){
            fullResponse =new httpBuilder(200)
                .setResponseLength(contentValue)
                .setResponseType(HTML)
                .setConnection()
                .setServer()
                .build();
            fullResponse += contentValue;
        }else if(this.type.equals("script")){
            fullResponse =new httpBuilder(200)
                .setResponseLength(contentValue)
                .setResponseType(JS)
                .setConnection()
                .setServer()
                .build();
            fullResponse+=contentValue;
        }else if(this.type.equals("style")){
            fullResponse =new httpBuilder(200)
                .setResponseLength(contentValue)
                .setResponseType(CSS)
                .setConnection()
                .setServer()
                .build();
            fullResponse+=contentValue;
        }
        assert (fullResponse != null);
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
    public ByteBuffer postContentInBytes(Map<String,String> requestJson, Map<String,String> cookiesMap, String mapping)
        throws Exception {
        String[] allowMethods = {"GET"};
        String httpResponse =new httpBuilder(405).setAllowMethods(allowMethods).build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }
}
