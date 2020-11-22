package parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

//fixme
public class JsonParser {

    private String json;

    public JsonParser(String json){
        this.json=json;
    }

    public Map<String,String> jsonHashMap(){
        json = cutOut(json,'{','}');
        String[] keyValues = getKeyValueArray(json);
        Map<String,String> hm = new HashMap<>();

        for(int i = 0 ; i<keyValues.length;i++){
            if(hm.containsKey(keyValues[i])){
                throw new RuntimeException("Этот парсер поддерживает чтение только одного объекта json");
            }
            hm.put(keyValues[i],keyValues[i+1]);
            i++;
        }
        return hm;
    }

    public static String cutOut(String str,char start,char end){
        str = str.substring(str.indexOf(start)+1,str.lastIndexOf(end));
        return str;
    }
    private String[] getKeyValueArray(String str){
        str = str.replaceAll("\n","");
        StringTokenizer st = new StringTokenizer(str,",");
        String[] kv1 = new String[st.countTokens()];
        ArrayList<String> kv3 = new ArrayList<>();
        int i = 0;
        while (st.hasMoreTokens()){
            kv1[i] = st.nextToken().replaceAll("\"","");
            i++;
        }
        for (String kv : kv1) {
            String[] k_v = kv.split(":",2);
            kv3.add(k_v[0].trim());
            kv3.add(k_v[1].trim());
        }
        return kv3.toArray(new String [0]);
    }
}










