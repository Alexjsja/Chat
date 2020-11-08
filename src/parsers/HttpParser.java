package parsers;

import database.dbConnector;
import factories.ramUserFactory;
import logic.sendibleContent;

import java.sql.SQLException;
import java.util.HashMap;

public class HttpParser {

    private static HashMap<String,String> httpLines;
    private static String[] allLines;
    private static String firstLine;
    private static String method;


    public static void parseHttp(String httpRequest){
        httpLines = httpHashMap(httpRequest);
        firstLine = getLines(httpRequest)[0];
        allLines = getLines(httpRequest);
    }

    private static HashMap<String,String> httpHashMap(String httpRequest){
        String[] lines = getLines(httpRequest);
        HashMap<String,String> hm = new HashMap<>();
        for (int i =1 ;i<lines.length;i++){
            if(lines[i].equals("\r")||lines[i].length()==0){
                break;
            }
            String[] kv = lines[i].split(":",2);
            hm.put(kv[0].trim(),kv[1].trim());
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

    public static String getMapping() throws SQLException {
        String mapping = firstLine.substring(firstLine.indexOf('/') + 1, firstLine.indexOf('H')).replaceAll("\\s", "");

        boolean auth = dbConnector.containsUser(getCookieValue());

        if (methodIsPost()) {
            mapping = mapping.substring(mapping.indexOf('/') + 1);
        }
        if (mapping.length()==0){
            if (auth){
                mapping ="home";
            }else {
                mapping ="register";
            }
        }
        if(sendibleContent.authPages.toString().contains(mapping)&&!auth){
            mapping ="register";
        }
        if(!sendibleContent.getAllMappings().contains(mapping)){
            mapping = "404";
        }
        return mapping;
    }
    public static String getCookieValue() {
        String fullCookie = httpLines.get("Cookie");
        if (fullCookie != null) {
            return fullCookie.substring(fullCookie.indexOf('=')+1);
        }
        return "o";
    }
}
