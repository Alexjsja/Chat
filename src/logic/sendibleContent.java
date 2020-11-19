package logic;

import database.dbConnector;
import http.httpBuilder;
import models.RequestQueue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static http.httpBuilder.*;


public enum sendibleContent {

    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    HOME_JS("home.js", "script", true, "home.js"),
    LOGIN_JS("login.js", "script", false, "login.js"),
    REGISTER_JS("register.js", "script", false, "register.js"),
    HOME_CSS("home.css", "style", true, "home.css"),
    CSS404("404.css", "style", false, "404.css"),
    REGISTER_CSS("register.css", "style", false, "register.css"),
    LOGIN_CSS("login.css", "style", false, "login.css"),
    MESSAGE_EXCHANGER_JS("messageExchanger.js","script",true,"messageExchanger.js"),
    PERSONAL_JS("personal.js","script",true,"personal.js"),
    USER_JS("user.js","script",true,"user.js"),
    //todo
    PROFILE("profile.html","page",true,"profile")
    /*<--------------------------------MODIFIED RETURNS---------------------------->*/,
    PERSONAL_CHAT("personal.html","page",true,"personal"){
        @Override
        public ByteBuffer getContentInBytes(RequestQueue request) throws Exception {
            Map<String, String> cookiesMap = request.getCookies();
            if (cookiesMap.containsKey("receiver")){
                return super.getContentInBytes(request);
            }else {
                return redirect("/home");
            }
        }

        @Override
        public ByteBuffer postContentInBytes(RequestQueue request) throws Exception {
            return personalChatLogic.writeMessage(request);
        }
    },
    USER("user.html", "page", true, "user"){
        @Override
        public ByteBuffer getContentInBytes(RequestQueue request) throws Exception {
            Map<String, String> cookiesMap = request.getCookies();
            if(cookiesMap.containsKey("user")&&dbConnector.containsUserById(Integer.parseInt(cookiesMap.get(("user"))))){
                //fixme
                return userPageLogic.showUserPage(cookiesMap,super.fullPath);
            }else {
                return notFound();
            }
        }
    },
    LOGIN("login.html","page",false,"login"){
        @Override
        public ByteBuffer postContentInBytes(RequestQueue request) throws Exception {
            return loginAndRegisterLogic.loginOrRegister(request);
        }
    },
    REGISTER("register.html","page",false,"register"){
        @Override
        public ByteBuffer postContentInBytes(RequestQueue request) throws Exception {
            return loginAndRegisterLogic.loginOrRegister(request);
        }
    },
    PAGE404("404.html","page",false,"404"){
        @Override
        public ByteBuffer getContentInBytes(RequestQueue request) throws Exception {
            return notFound();
        }
    },
    HOME("home.html","page",true,"home"){
        @Override
        public ByteBuffer getContentInBytes(RequestQueue request) throws Exception {
            Map<String, String> cookiesMap = request.getCookies();
            if(cookiesMap.containsKey("last_time")){
                return homePageLogic.getNewMessages(request);
            }else if (cookiesMap.containsKey("logout")){
                return homePageLogic.logout(request);
            }else {
                return super.getContentInBytes(request);
                //todo
//                return homePageLogic.getPage(cookiesMap);
            }
        }
        @Override
        public ByteBuffer postContentInBytes(RequestQueue request) throws Exception {
            return homePageLogic.writeMessage(request);
        }
    };


    protected final String fullPath;
    protected final String type;
    protected final boolean forAuth;
    protected final String mapping;

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
    public ByteBuffer getContentInBytes(RequestQueue request) throws Exception {
        boolean authentic;
        Map<String, String> cookiesMap = request.getCookies();
        if (cookiesMap.containsKey("session")){
            authentic = dbConnector.containsCookieSession(cookiesMap.get("session"));
        }else {
            authentic = false;
        }
        Path contentPath = Paths.get(this.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));

        String type = this.type;

        if(!authentic&&authMappings.contains(this.mapping)){

            return redirect("/register");

        }else if(type.equals("page")){

            return returnPage(contentValue);

        }else if(type.equals("script")){

            return returnScript(contentValue);

        }else if(type.equals("style")){

            return returnStyle(contentValue);

        }else {

            return notFound();

        }
    }
    public ByteBuffer postContentInBytes(RequestQueue request)
        throws Exception {
        String[] allowMethods = {"GET"};
        String httpResponse =new httpBuilder(405).setAllowMethods(allowMethods).build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }

    protected ByteBuffer returnPage(String html){
        String pageHeaders =  new httpBuilder(200)
            .setResponseLength(html)
            .setResponseType(HTML)
            .setConnection()
            .setServer()
            .build();
        String fullResponse = pageHeaders+html;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
    protected ByteBuffer returnScript(String script){
        String scriptHeaders =  new httpBuilder(200)
            .setResponseLength(script)
            .setResponseType(JS)
            .setConnection()
            .setServer()
            .build();
        String fullResponse = scriptHeaders + script;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
    protected ByteBuffer returnStyle(String css){
        String styleHeaders = new httpBuilder(200)
            .setResponseLength(css)
            .setResponseType(CSS)
            .setConnection()
            .setServer()
            .build();
        String fullResponse = styleHeaders + css;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
    protected ByteBuffer redirect(String redirectPath){
        String redirect = new httpBuilder(307)
            .setRedirect(redirectPath)
            .build();
        return ByteBuffer.wrap(redirect.getBytes());
    }
    protected ByteBuffer notFound() throws IOException {
        Path contentPath = Paths.get(PAGE404.fullPath);
        String default404page = String.join("\n", Files.readAllLines(contentPath));
        String notFoundHeader =new httpBuilder(404)
            .setResponseLength(default404page)
            .setResponseType(HTML).setConnection()
            .build();
        String fullResponse = notFoundHeader + default404page;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
}
