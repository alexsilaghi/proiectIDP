import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alex on 5/10/2016.
 */
public class UserDetails implements Serializable {

    private static final long serialVersionUID = -8127560171482587205L;


    private ArrayList<String> friends;
    private ArrayList<UserGroup> groups;
    private String userFullName;

    public UserDetails(ArrayList<String> friends, ArrayList<UserGroup> groups, String userFullName) {
        this.friends = friends;
        this.groups = groups;
        this.userFullName = userFullName;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<UserGroup> groups) {
        this.groups = groups;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}

