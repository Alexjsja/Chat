package parsers;

import database.dbConnector;

import java.sql.SQLException;
import java.util.HashMap;

public class HttpParser {

    private HashMap<String,String> httpMap;
    private HashMap<String,String> cookiesMap;
    private String[] allLines;
    private String firstLine;
    private String method;


    HttpParser(String httpRequest){
        this.cookiesMap = new HashMap<>();
        this.httpMap = httpHashMap(httpRequest);
        this.firstLine = getLines(httpRequest)[0];
        this.allLines = getLines(httpRequest);
    }

    public static HttpParser parseHttp(String httpRequest){
        if (httpRequest==null||httpRequest.length()==0)
                throw new IllegalArgumentException();
        return new HttpParser(httpRequest);
    }

    public HashMap<String,String> parseCookie(String str){
        String[] cookies = str.split(";");
        HashMap<String,String> cookieKeyValueMap = new HashMap<>();
        for (int i = 0; i < cookies.length; i++) {
            String[] cookieKeyValue = cookies[i].trim().split("=",2);
            if (cookieKeyValue[1].length()==0)continue;
            cookieKeyValueMap.put(cookieKeyValue[0].trim(),cookieKeyValue[1].trim());
        }
        return cookieKeyValueMap;
    }

    private HashMap<String,String> httpHashMap(String httpRequest){
        String[] lines = getLines(httpRequest);
        HashMap<String,String> hm = new HashMap<>();
        for (int i = 1 ;i<lines.length;i++){
            if(lines[i].equals("\r")||lines[i].length()==0){
                break;
            }
            String[] httpKeyValue = lines[i].split(":",2);
            if(httpKeyValue[0].equals("Cookie")){
                this.cookiesMap.putAll(parseCookie(httpKeyValue[1].trim()));
            }else{
                hm.put(httpKeyValue[0].trim(),httpKeyValue[1].trim());
            }
        }
        return hm;
    }
    private String[] getLines(String httpRequest){
        return httpRequest.split("\n");
    }

    public String getBody(){
        String[] str = this.allLines;
        StringBuilder body = new StringBuilder();
        boolean startBody=false;
        for (String s : str) {
            if (s.equals("\r")||s.length()==0) {
                startBody = true;
            }
            if (startBody) {
                body.append(s);
            }
        }
        return body.toString();
    }
    public String getMethod(){
        this.method = this.firstLine.substring(0,this.firstLine.indexOf('/')).replaceAll("\\s","");
        return this.method;
    }

    public boolean methodIsPost(){
        return this.method.equals("POST");
    }
    public boolean methodIsGet(){
        return this.method.equals("GET");
    }
    public HashMap<String,String> getCookiesMap(){
        return this.cookiesMap;
    }


    public String getMapping() throws SQLException {
        String mapping = this.firstLine.substring(this.firstLine.indexOf('/') + 1, this.firstLine.indexOf('H')).replaceAll("\\s", "");

        boolean auth = dbConnector.containsCookieSession(this.cookiesMap.get("session"));
        if (mapping.length()==0){
            if (auth){
                mapping ="home";
            }else {
                mapping ="register";
            }
        }
        return mapping;
    }
}
