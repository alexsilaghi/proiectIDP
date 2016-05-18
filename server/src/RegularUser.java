import facebook4j.Facebook;

/**
 * Created by Alex on 5/15/2016.
 */
public class RegularUser {
    private Facebook facebook;
    private ChatClientInterface chatClientInterface;

    public RegularUser() {
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public void setFacebook(Facebook facebook) {
        this.facebook = facebook;
    }

    public ChatClientInterface getChatClientInterface() {
        return chatClientInterface;
    }

    public void setChatClientInterface(ChatClientInterface chatClientInterface) {
        this.chatClientInterface = chatClientInterface;
    }
}
