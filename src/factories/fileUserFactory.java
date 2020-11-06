/*FIXME
package factories;

import models.User;

import java.io.*;
import java.util.HashMap;

public class fileUserFactory {
    private HashMap<String,User> userMap;
    private long iterator;
    private final File file;

    public fileUserFactory(String fileName) throws Exception {
        String project = System.getProperty("user.dir");
        file = new File(project+"\\src\\data\\"+fileName);
        if (!file.exists()){
            this.userMap = new HashMap<>();
            this.iterator = 0;
        }else{
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream obi = new ObjectInputStream(fis);
            Class<?> clazz = obi.readObject().getClass();

            assert(!clazz.equals(HashMap.class));

            userMap = (HashMap<String, User>) obi.readObject();
            iterator = userMap.values().stream().mapToLong(User::getId).max().getAsLong()+1;
            fis.close();
            obi.close();
        }
    }
    public void close() throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream ous = new ObjectOutputStream(fos);
        ous.writeObject(userMap);
        fos.close();
        ous.close();
    }
    public boolean addUser(String name,String password){
        if(!userMap.containsKey(name)){
            User user = new User(name,iterator,password);
            iterator++;
            userMap.put(user.getName(),user);
            return true;
        }else{
            System.err.println("ERROR:models.User already exists");
            return false;
        }
    }

    public long getIterator() {
        return iterator;
    }

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

}
*/
