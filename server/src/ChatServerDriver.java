import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Alex on 5/9/2016.
 */
public class ChatServerDriver {
    public static void main(String args[]) {
        System.setProperty("java.security.policy","file:out\\production\\server\\server.policy");
        System.setProperty("java.rmi.server.codebase","file:out\\production\\server\\");
        String url = "rmi://localhost:1099/RMIChatServer";
        ChatServerInterface srv;
        Facebook facebook = new FacebookFactory().getInstance();
        UserDao dao = new UserDao();
        System.out.println("Launching server");

        Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
        Timestamp lastPurge = dao.lastPurge();

        if( (now.getTime() - lastPurge.getTime())/ (24 * 60 * 60 * 1000) > 15) {
            dao.deleteOldFiles();
            dao.deleteOldMessages();
            dao.setLastPurge(now);
        }

        try {
            srv = new ChatServer();
            Naming.rebind(url,srv);
            System.out.println(System.getProperty("java.security.policy"));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
