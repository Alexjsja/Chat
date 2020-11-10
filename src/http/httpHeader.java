package http;

public class httpHeader {
    public static final String HTML = "text/html";
    public static final String JS = "application/javascript";
    public static final String JSON = "application/json";
    public static final String CSS = "text/css";


    private StringBuilder header;

    private httpHeader(){}

    public static httpBuilder startBuild(int httpCode){
        return new httpHeader().new httpBuilder(httpCode);
    }

    public class httpBuilder{
        private httpBuilder(int httpCode){
            header = new StringBuilder();
            header.append("HTTP/1.1 ");
            switch (httpCode){
                case 204:
                    header.append("204 No Content\n");
                    break;
                case 100:
                    header.append("100 Continue\n");
                    break;
                case 200:
                    header.append("200 OK\n");
                    break;
                case 301:
                    header.append("301 Moved Permanently\n");
                    break;
                case 404:
                    header.append("404 Not Found\n");
                    break;
            }
        }

        public httpBuilder setRedirect(String mapping){
            header.append(String.format("Location: %s\n",mapping));
            return this;
        }

        public httpBuilder setResponseType(String type){
            assert !type.equals(CSS)&&!type.equals(HTML)&&!type.equals(JS)&&!type.equals(JSON);

            header.append(String.format("Content-type: %s\n",type));
            return this;
        }

        public httpBuilder setResponseLength(int length){
            header.append(String.format("Content-length: %s\n",length));
            return this;
        }

        public httpBuilder setCookie(String cookieKey,String cookieValue){
            header.append(String.format("Set-Cookie:%s=%s;Path=/;Max-Age=600;httponly\n",cookieKey,cookieValue));
            return this;
        }
        public httpBuilder removeCookie(String cookieKey){
            header.append(String.format("Set-Cookie: %s=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT \n",cookieKey));
            return this;
        }
        public httpBuilder setServer(){
            header.append("Server: best-server\n");
            return this;
        }
        public httpBuilder setConnection(){
            //todo: more connections
            header.append("Connection: keep-alive\n");
            return this;
        }

        public String build(){
            header.append("\n");
            return header.toString();
        }
    }
}
