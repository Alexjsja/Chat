package logic;

import database.dbConnector;
import http.httpHeader;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum sendibleContent {
    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    xhrJS("xhr.js","script",false,"xhr.js"),
    homeJS("home.js","script",false,"home.js"),
    loginJS("login.js","script",false,"login.js"),
    registerJS("register.js","script",false,"register.js"),
    /*<--------------------------------MODIFIED RETURNS---------------------------->*/
    login("login.html","page",false,"login"),
    register("register.html","page",false,"register"),
    home("home.html","page",false,"home"){
        public ByteBuffer getContentInBytes(HashMap<String, String> cookiesMap) throws Exception {
            if(cookiesMap.containsKey("last_time")){
                String response = httpHeader.startBuild(204)
                        .removeCookie("last_time")
                        .setServer()
                        .build();
                return ByteBuffer.wrap(response.getBytes());
            }else {
                return super.getContentInBytes(cookiesMap);
            }
        }
    },
    page404("404.html","page",false,"404"){
        public ByteBuffer getContentInBytes(String cookie) throws Exception {
            Path contentPath = Paths.get(super.fullPath);
            String contentValue = String.join("\n", Files.readAllLines(contentPath));
            String header = httpHeader
                    .startBuild(404)
                    .setResponseLength(contentValue.getBytes().length)
                    .setResponseType(httpHeader.HTML).build();

            String fullResponse = header + contentValue;
            return ByteBuffer.wrap(fullResponse.getBytes());
        }
    };

    private final String fullPath;
    private final String type;
    private final boolean forAuth;
    private final String mapping;
    public static final Set<String> authMappings = filterMappingsForAuth(true);
    public static final Set<String> notAuthMappings = filterMappingsForAuth(false);

    public static Set<String> filterMappingsForAuth(boolean auth){
        return Arrays.stream(sendibleContent.values()).filter(sendibleContent -> sendibleContent.forAuth==auth).map(el->el.mapping).collect(Collectors.toSet());
    }

    public static sendibleContent getContentOfMapping(String mapping){
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


    public ByteBuffer getContentInBytes(HashMap<String,String> cookiesMap) throws Exception {
        //todo map cookies
        boolean authentic;
        if (cookiesMap.containsKey("session")){
            authentic = dbConnector.containsUser(cookiesMap.get("session"));
        }else {
            authentic = false;
        }
        Path contentPath = Paths.get(this.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));
        String fullResponse =null;

        if(this.type.equals("page")){
            if(!authentic&&authMappings.contains(this.mapping)){
                fullResponse = httpHeader
                        .startBuild(301)
                        .setRedirect("/register")
                        .build();
            } else {
                fullResponse = httpHeader
                        .startBuild(200)
                        .setResponseLength(contentValue.getBytes().length)
                        .setResponseType(httpHeader.HTML)
                        .setConnection()
                        .setServer()
                        .build();
                fullResponse += contentValue;
            }
        }else if(this.type.equals("script")){
            fullResponse = httpHeader
                    .startBuild(200)
                    .setResponseLength(contentValue.getBytes().length)
                    .setResponseType(httpHeader.JS)
                    .setConnection()
                    .setServer()
                    .build();
            fullResponse+=contentValue;
        }
        assert (fullResponse != null);
        return ByteBuffer.wrap(fullResponse.getBytes());
    }

    //todo switch to logic
    public ByteBuffer postContentInBytes(HashMap<String,String> requestJson,String cookie,String mapping) throws Exception {
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
