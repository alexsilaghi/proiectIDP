import java.io.Serializable;

/**
 * Created by Alex on 4/9/2016.
 */
public class UserFriend implements Serializable {
    private static final long serialVersionUID = -7701222417193016386L;
    private String userFriendName;

    public UserFriend(String userFriendName) {
        this.userFriendName = userFriendName;
    }

    public String getUserFriendName() {
        return userFriendName;
    }

    public void setUserFriendName(String userFriendName) {
        this.userFriendName = userFriendName;
    }
}
