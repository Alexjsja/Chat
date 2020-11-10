package parsers;

import database.dbConnector;

import java.sql.SQLException;
import java.util.HashMap;

public class HttpParser {

    private static HashMap<String,String> httpMap;
    private static HashMap<String,String> cookiesMap;
    private static String[] allLines;
    private static String firstLine;
    private static String method;


    public static void parseHttp(String httpRequest){
        cookiesMap = new HashMap<>();
        httpMap = httpHashMap(httpRequest);
        firstLine = getLines(httpRequest)[0];
        allLines = getLines(httpRequest);
    }

    public static HashMap<String,String> parseCookie(String str){
        String[] cookies = str.split(";");
        HashMap<String,String> cookieKeyValueMap = new HashMap<>();
        for (int i = 0; i < cookies.length; i++) {
            String[] cookieKeyValue = cookies[i].trim().split("=",2);
            if (cookieKeyValue[1].length()==0)continue;
            cookieKeyValueMap.put(cookieKeyValue[0].trim(),cookieKeyValue[1].trim());
        }
        return cookieKeyValueMap;
    }

    private static HashMap<String,String> httpHashMap(String httpRequest){
        String[] lines = getLines(httpRequest);
        HashMap<String,String> hm = new HashMap<>();
        for (int i = 1 ;i<lines.length;i++){
            if(lines[i].equals("\r")||lines[i].length()==0){
                break;
            }
            String[] httpKeyValue = lines[i].split(":",2);
            if(httpKeyValue[0].equals("Cookie")){
                cookiesMap.putAll(parseCookie(httpKeyValue[1].trim()));
            }else{
                hm.put(httpKeyValue[0].trim(),httpKeyValue[1].trim());
            }
        }
        return hm;
    }
    private static String[] getLines(String httpRequest){
        return httpRequest.split("\n");
    }

    public static String getBody(){
        String[] str = allLines;
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
    public static String getMethod(){
        method = firstLine.substring(0,firstLine.indexOf('/')).replaceAll("\\s","");
        return method;
    }

    public static boolean methodIsPost(){
        return method.equals("POST");
    }
    public static boolean methodIsGet(){
        return method.equals("GET");
    }
    public static HashMap<String,String> getCookiesMap(){
        return cookiesMap;
    }


    public static String getMapping() throws SQLException {
        String mapping = firstLine.substring(firstLine.indexOf('/') + 1, firstLine.indexOf('H')).replaceAll("\\s", "");

        boolean auth = dbConnector.containsUser(cookiesMap.get("session"));

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
