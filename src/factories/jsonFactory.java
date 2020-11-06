package factories;

import java.nio.ByteBuffer;

import static http.HttpBlanks.*;

public class jsonFactory {
    //todo
    public static ByteBuffer jsonInBytes(String json,String name) throws Exception {
        String jsonWithHttp = String.format(HeaderCookie,code201, typeJson,name,json.getBytes().length) + json;
        return ByteBuffer.wrap(jsonWithHttp.getBytes());
    }
    public static ByteBuffer jsonInBytes(String json) throws Exception {
        String jsonWithHttp = String.format(HeaderOK,code201, typeJson,json.getBytes().length) + json;
        return ByteBuffer.wrap(jsonWithHttp.getBytes());
    }
}
