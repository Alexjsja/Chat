package http;

import data.DataConnector;

import java.nio.channels.SocketChannel;
import java.util.Map;

public class HttpRequest {
    private SocketChannel socketChannel;
    private Map<String,String> jsonBody;
    private Map<String,String> cookies;
    private Map<String,String> parameters;
    private boolean isAuth;
    private String method;
    private String mapping;
    private DataConnector dataConnector;

    HttpRequest(){}

    public static requestBuilder newRequest(){ return new HttpRequest().new requestBuilder(); }

    public DataConnector getDataConnector() { return dataConnector; }

    public Map<String, String> getCookies(){ return this.cookies; }

    public Map<String, String> getJson(){ return this.jsonBody; }

    public SocketChannel getChannel(){ return this.socketChannel; }

    public String getMapping() { return this.mapping; }

    public String getMethod(){ return this.method; }

    public Map<String, String> getParameters(){ return this.parameters; }

    public boolean isAuth(){ return this.isAuth; }


    public class requestBuilder{
        requestBuilder(){}

        public requestBuilder setCookies(Map<String,String> cookies) throws Exception {
            HttpRequest.this.cookies=cookies;
            String session = cookies.get("session");
            HttpRequest.this.isAuth=HttpRequest.this.dataConnector.containsCookieSession(session);
            return this;
        }
        public requestBuilder setParameters(Map<String,String> parameters){
            HttpRequest.this.parameters=parameters;
            return this;
        }
        public requestBuilder setChannel(SocketChannel channel){
            HttpRequest.this.socketChannel=channel;
            return this;
        }
        public requestBuilder setBody(Map<String,String> body){
            HttpRequest.this.jsonBody = body;
            return this;
        }
        public requestBuilder setMapping(String mapping){
            HttpRequest.this.mapping=mapping;
            return this;
        }
        public requestBuilder setMethod(String method){
            HttpRequest.this.method=method;
            return this;
        }
        public requestBuilder setDataConnector(DataConnector dataConnector) {
            HttpRequest.this.dataConnector=dataConnector;
            return this;
        }

        public HttpRequest build(){
            return HttpRequest.this;
        }
    }
}

