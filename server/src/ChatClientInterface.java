import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Created by Alex on 5/9/2016.
 */
public interface ChatClientInterface{
    public void retrieveUserMessage(String message,String fromUsername);
    public void retrieveFileFromServer(byte[] fileContent, String fileName);
    public void retrieveFileFromUser(ChatClientInterface fromUser,String filename, String filepath) throws FileNotFoundException;
    public void setTextArea(JTextArea textArea);
}
