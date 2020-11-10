import logic.Server;
import models.Message;
import models.User;

import java.time.LocalTime;


public class Main {

    public static void main(String[] args) throws Exception {
        /*Message message = new Message("value","admin",LocalTime.now(),"user");
        System.out.println(message.toJsonFormat());

        User user = new User("admin",12,"password");
        System.out.println(user.toJsonFormat());*/
        Server.run();
    }
}
