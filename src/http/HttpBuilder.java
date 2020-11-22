package http;

public class HttpBuilder {

    public enum ContentType {
        HTML("text/html"),
        JS("application/javascript"),
        JSON("application/json"),
        CSS("text/css"),
        TEXT("text/plain");
        private final String type;
        ContentType(String type){
            this.type=type;
        }
        public String getHttpValue(){
            return this.type;
        }
    }

    private StringBuilder header;

    public static final String noCache = "no-store";


    public HttpBuilder(int httpCode){
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

    public HttpBuilder setRedirect(String mapping){
        header.append(String.format("Location: %s\n",mapping));
        return this;
    }
    public HttpBuilder setCacheControl(String control){
        header.append(String.format("Cache-Control: %s\n",control));
        return this;
    }

    public HttpBuilder setResponseType(ContentType type){

        header.append(String.format("Content-type: %s\n",type.getHttpValue()));
        return this;
    }

    public HttpBuilder setResponseLength(String response){
        int length = response.getBytes().length;
        header.append(String.format("Content-length: %s\n",length));
        return this;
    }

    public HttpBuilder setCookie(String cookieKey, String cookieValue){
        header.append(String.format("Set-Cookie:%s=%s;Path=/;Max-Age=2400;httponly\n",cookieKey,cookieValue));
        return this;
    }
    public HttpBuilder removeCookie(String cookieKey){
        header.append(String.format("Set-Cookie: %s=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT\n",cookieKey));
        return this;
    }
    public HttpBuilder setServer(){
        header.append("Server: best-server\n");
        return this;
    }
    public HttpBuilder setConnection(){
        header.append("Connection: close\n");
        return this;
    }

    public HttpBuilder setAllowMethods(String[] methods) {
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

