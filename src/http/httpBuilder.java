package http;

public class httpBuilder{
    private StringBuilder header;

    public static final String HTML = "text/html";
    public static final String JS = "application/javascript";
    public static final String JSON = "application/json";
    public static final String CSS = "text/css";
    public static final String TEXT = "text/plain";

    public static final String noCache = "no-store";


    public httpBuilder(int httpCode){
        header = new StringBuilder();
        header.append("HTTP/1.1 ");
        switch (httpCode){
            case 202: header.append("202 Accepted\n");break;
            case 307: header.append("307 Temporary Redirect\n");break;
            case 405: header.append("405 Method Not Allowed\n");break;
            case 204: header.append("204 No Content\n");break;
            case 404: header.append("404 Not Found\n");break;
            case 100: header.append("100 Continue\n");break;
            case 200: header.append("200 OK\n");break;
        }
    }

    public httpBuilder setRedirect(String mapping){
        header.append(String.format("Location: %s\n",mapping));
        return this;
    }
    public httpBuilder setCacheControl(String control){
        header.append(String.format("Cache-Control: %s\n",control));
        return this;
    }

    public httpBuilder setResponseType(String type){
        assert !type.equals(CSS)&&!type.equals(HTML)&&!type.equals(JS)&&!type.equals(JSON);

        header.append(String.format("Content-type: %s\n",type));
        return this;
    }

    public httpBuilder setResponseLength(String response){
        int length = response.getBytes().length;
        header.append(String.format("Content-length: %s\n",length));
        return this;
    }

    public httpBuilder setCookie(String cookieKey,String cookieValue){
        header.append(String.format("Set-Cookie:%s=%s;Path=/;Max-Age=2400;httponly\n",cookieKey,cookieValue));
        return this;
    }
    public httpBuilder removeCookie(String cookieKey){
        header.append(String.format("Set-Cookie: %s=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\n",cookieKey));
        return this;
    }
    public httpBuilder setServer(){
        header.append("Server: best-server\n");
        return this;
    }
    public httpBuilder setConnection(){
        header.append("Connection: close\n");
        return this;
    }

    public httpBuilder setAllowMethods(String[] methods) {
        StringBuilder allow = new StringBuilder("Allow: ");
        for (int i = 0; i<methods.length; i++){
            allow.append(methods[i]);

            if (i+1!=methods.length) allow.append(", ");
        }
        header.append(allow);
        return this;
    }

    public String build(){
        header.append("\n");
        return header.toString();
    }
}

