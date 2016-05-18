import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alex on 4/9/2016.
 */
public class UserGroup implements Serializable{
    private static final long serialVersionUID = 7782726242781501461L;
    private String userGroupName;
    private ArrayList<String> usersInGroup;

    public UserGroup() {
    }

    public String getUserGroupName() {
        return userGroupName;
    }

    public void setUserGroupName(String userGroupName) {
        this.userGroupName = userGroupName;
    }

    public ArrayList<String> getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(ArrayList<String> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }
}
