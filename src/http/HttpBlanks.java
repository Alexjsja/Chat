package http;

public class HttpBlanks {
    //todo: builder
    public static final String typeHtml = "text/html";
    public static final String typeJson = "application/json";
    public static final String typeJS = "application/javascript";
    public static final String code200 = "200 OK";
    public static final String code201 = "201 Created";
    public static final String HeaderOK =
            "HTTP/1.1 %s\n" +
                    "Server: test\n" +
                    "Content-type: %s\n" +
                    "Content-length: %s\n" +
                    "Connection: keep-alive\n\n";
    public static final String HeaderCookie =
            "HTTP/1.1 %s\n" +
                    "Server: test\n" +
                    "Content-type: %s\n" +
                    "Set-Cookie:session=%s;Path=/;Max-Age=600;httponly\n"+
                    "Content-length: %s\n" +
                    "Connection: keep-alive\n\n";
}
