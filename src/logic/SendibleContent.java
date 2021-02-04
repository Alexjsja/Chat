package logic;

import http.HttpBuilder;
import http.HttpRequest;
import http.HttpBuilder.ContentType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static http.HttpBuilder.ContentType.*;


public enum SendibleContent {

    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    MESSAGE_EXCHANGER_JS("messageExchanger.js",JS,true,"messageExchanger.js"),
    REGISTER_JS("register.js", JS, false, "register.js"),
    PERSONAL_JS("personal.js",JS,true,"personal.js"),
    LOGIN_JS("login.js", JS, false, "login.js"),
    HOME_JS("home.js", JS, true, "home.js"),
    USER_JS("user.js",JS,true,"user.js"),
    REGISTER_CSS("register.css", CSS, false, "register.css"),
    LOGIN_CSS("login.css", CSS, false, "login.css"),
    HOME_CSS("home.css",CSS, true, "home.css"),
    CSS404("404.css", CSS, false, "404.css"),
    NULL_LENGTH_REQUEST("404.html",HTML,false,""),
    /*<--------------------------------MODIFIED RETURNS---------------------------->*/
    PERSONAL_CHAT("personal.html",HTML,true,"personal"){
        @Override
        public ByteBuffer doGet(HttpRequest request) throws Exception {
            if (request.getCookies().containsKey("last_time")){
                return PersonalChatLogic.getNewMessages(request);
            }else {
                return super.doGet(request);
            }
        }
        @Override
        public ByteBuffer doPost(HttpRequest request) throws Exception {
            return PersonalChatLogic.writeMessage(request);
        }
    },
    USER("user.html",HTML, true, "user"){
        @Override
        public ByteBuffer doGet(HttpRequest request) throws Exception {
            if (request.getParameters().containsKey("id")) {
                if (request.getDataConnector().containsUserById(request.getParameters().get("id"))){
                    return UserPageLogic.showUserPage(request,this.fullPath);
                }else {
                    return redirect("/user");
                }
            }else {
                return UserPageLogic.showMyPage(request,this.fullPath);
            }
        }
        @Override
        public ByteBuffer doPost(HttpRequest request) throws Exception {
            //todo
            return super.doPost(request);
        }
    },
    LOGIN("login.html",HTML,false,"login"){
        @Override
        public ByteBuffer doPost(HttpRequest request) throws Exception {
            return LoginAndRegisterLogic.loginOrRegister(request);
        }
    },
    REGISTER("register.html",HTML,false,"register"){
        @Override
        public ByteBuffer doPost(HttpRequest request) throws Exception {
            return LoginAndRegisterLogic.loginOrRegister(request);
        }
    },
    PAGE404("404.html",HTML,false,"404"){
        @Override
        public ByteBuffer doGet(HttpRequest request) throws Exception {
            return notFound();
        }
    },
    HOME("home.html",HTML,true,"home"){
        @Override
        public ByteBuffer doGet(HttpRequest request) throws Exception {
            Map<String, String> cookiesMap = request.getCookies();
            if(cookiesMap.containsKey("last_time")){
                return HomePageLogic.getNewMessages(request);
            }else if (cookiesMap.containsKey("logout")){
                return HomePageLogic.logout(request);
            }else {
                return super.doGet(request);
                //todo
                //return homePageLogic.getPage(cookiesMap);
            }
        }
        @Override
        public ByteBuffer doPost(HttpRequest request) throws Exception {
            return HomePageLogic.writeMessage(request);
        }
    };


    protected final String fullPath;
    protected final ContentType type;
    protected final boolean forAuth;
    protected final String mapping;

    SendibleContent(String fileName, ContentType type, boolean forAuth, String mapping){
        this.mapping=mapping;
        this.forAuth = forAuth;
        StringBuilder sb = new StringBuilder();
        String project = System.getProperty("user.dir");
        sb.append(project);
        switch (type){
            case HTML: sb.append("\\src\\view\\pages\\");break;
            case JS: sb.append("\\src\\view\\scripts\\");break;
            case CSS:sb.append("\\src\\view\\styles\\");break;
        }
        sb.append(fileName);
        this.fullPath=sb.toString();
        this.type = type;
    }

    private static final Set<SendibleContent> authContent = filterMappingsForAuth(true);
    private static final Set<SendibleContent> notAuthContent = filterMappingsForAuth(false);

    private static Set<SendibleContent> filterMappingsForAuth(boolean auth){
        return Arrays.stream(SendibleContent.values())
            .filter(SendibleContent -> SendibleContent.forAuth==auth)
            .collect(Collectors.toSet());
    }

    //now main
    public static ByteBuffer doResponse(HttpRequest request) throws Exception {
        String mapping0 = request.getMapping();
        String method = request.getMethod();
        boolean auth = request.isAuth();

        SendibleContent anyContent = Arrays.stream(SendibleContent.values())
            .filter(SendibleContent-> SendibleContent.mapping.equals(mapping0))
            .findFirst().orElse(PAGE404);

        if (authContent.contains(anyContent)&&!auth){
            return anyContent.redirect("/register");
        }else if(method.equals("GET")) {
            return anyContent.doGet(request);
        }else if(method.equals("POST")){
            return anyContent.doPost(request);
        }else {
            return anyContent.notFound();
        }
    }

    /*<---------------------------------DEFAULT RETURNS---------------------------->*/
    protected ByteBuffer doGet(HttpRequest request) throws Exception {
        boolean authentic  = request.isAuth();

        Path contentPath = Paths.get(this.fullPath);
        String contentValue = String.join("\n", Files.readAllLines(contentPath));

        if (this==NULL_LENGTH_REQUEST){
            return authentic ? redirect("/home") : redirect("/register");
        }
        return returnContent(contentValue,this.type);
    }
    protected ByteBuffer doPost(HttpRequest request)
        throws Exception {
        String[] allowMethods = {"GET"};
        String httpResponse =new HttpBuilder(405).setAllowMethods(allowMethods).build();
        return ByteBuffer.wrap(httpResponse.getBytes());
    }

    protected ByteBuffer returnContent(String content,ContentType type){
        String pageHeaders =  new HttpBuilder(200)
            .setResponseLength(content)
            .setResponseType(type)
            .setConnection()
            .setServer()
            .build();
        String fullResponse = pageHeaders+content;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }
    protected ByteBuffer redirect(String redirectPath){
        String redirect = new HttpBuilder(307)
            .setRedirect(redirectPath)
            .build();
        return ByteBuffer.wrap(redirect.getBytes());
    }
    //fixme?
    protected ByteBuffer notFound() throws IOException {
        Path contentPath = Paths.get(PAGE404.fullPath);
        String default404page = String.join("\n", Files.readAllLines(contentPath));
        String notFoundHeader =new HttpBuilder(404)
            .setResponseLength(default404page)
            .setResponseType(HTML).setConnection()
            .build();
        String fullResponse = notFoundHeader + default404page;
        return ByteBuffer.wrap(fullResponse.getBytes());
    }

}
