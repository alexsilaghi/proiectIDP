import facebook4j.Facebook;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Alex on 5/9/2016.
 */
public interface ChatServerInterface extends Remote {
    UserDetails registerChatClient(ChatClientInterface chatClient,String code) throws RemoteException;
    void removeChatClient(String clientName) throws RemoteException;
    void shareFileWithUser(String fromUsername,String toUsername, File f) throws RemoteException;
    void sendUserMessage(String message, String fromUsername, String toUsername) throws RemoteException;
    void sendGroupMessage(String message,String fromUsername, String groupName) throws RemoteException;
    String getUserChatHistory(String formUsername,String toUsername) throws RemoteException;
    String getGroupChatHistory(String groupName) throws RemoteException;
}
