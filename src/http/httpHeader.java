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
            switch (httpCode){
                case 200:
                    header.append("HTTP/1.1 200 OK\n");
                    break;
                case 301:
                    header.append("HTTP/1.1 301 Moved Permanently\n");
                    break;
                case 404:
                    header.append("HTTP/1.1 404 Not Found\n");
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

        public httpBuilder setCookie(String cookie){
            header.append(String.format("Set-Cookie:session=%s;Path=/;Max-Age=600;httponly\n",cookie));
            return this;
        }
        public httpBuilder removeCookie(){
            header.append("Set-Cookie: session=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT \n");
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
