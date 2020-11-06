package logic;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static http.HttpBlanks.*;

public enum pageLogic {
    xhr("xhr.js","script",false),
    home("home.html","page",true),
    page404("404.html","page",false),
    login("login.html","page",false),
    register("register.html","page",false),
    homeScript("homeJS.js","script",true),
    loginScript("loginJS.js","script",false),
    registerScript("registerJS.js","script",false);

    private StringBuilder fullPath;
    private String type;
    private boolean forAuth;

    public static Set<pageLogic> authPages = filterPages(true);
    public static Set<pageLogic> notAuthPages = filterPages(false);

    public static Set<pageLogic> filterPages(boolean auth){
        return Arrays.stream(pageLogic.values()).filter(pageLogic -> pageLogic.forAuth==auth).collect(Collectors.toSet());
    }

    pageLogic(String path, String type,boolean auth){
        forAuth = auth;
        fullPath = new StringBuilder();
        String project = System.getProperty("user.dir");
        this.fullPath.append(project);
        switch (type){
            case "page":
                fullPath.append("\\src\\front\\pages\\");
                break;
            case "script":
                fullPath.append("\\src\\front\\scripts\\");
                break;
        }
        this.fullPath.append(path);
        this.type = type;
    }
    public ByteBuffer getContentInBytes() throws Exception {
        Path pagePath = Paths.get(this.fullPath.toString());
        String content = String.join("\n", Files.readAllLines(pagePath));
        String contentWithHttp;
        switch (type){
            case "page":
                contentWithHttp = String.format(HeaderOK,code200, typeHtml, content.getBytes().length) + content;
                break;
            case "script":
                contentWithHttp = String.format(HeaderOK,code201,typeJS,content.getBytes().length)+content;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return ByteBuffer.wrap(contentWithHttp.getBytes());
    }
    public ByteBuffer postContentInBytes(){
        //todo
        return null;
    }
}
