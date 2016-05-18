import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import facebook4j.*;
import facebook4j.auth.AccessToken;
import facebook4j.auth.OAuthAuthorization;
import facebook4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alex on 5/9/2016.
 */
public class ChatServer extends UnicastRemoteObject implements  ChatServerInterface,Serializable{
    private static final long serialVersionUID = -4673285658057314738L;
    private static String appId = "1692837794299403";
    private static String appSecret = "ff6fc6ba050b31277234a8a4ae6b1cf2";

    UserDao dao = new UserDao();
    HashMap<String,RegularUser> clients;
    HashMap<String,UserGroup> serverGroups;

    protected ChatServer() throws RemoteException {
        this.clients = new HashMap<>();
        this.serverGroups = new HashMap<>();
    }

    @Override
    public UserDetails registerChatClient(ChatClientInterface chatClient,String code) throws RemoteException {
        Facebook fb = getFacebook(code);
        RegularUser user = new RegularUser();
        UserDetails returnUser = null;
        user.setFacebook(fb);
        user.setChatClientInterface(chatClient);
        try {
            this.clients.put(fb.getMe().getName(),user);
        } catch (FacebookException e) {
            e.printStackTrace();
        }
        ArrayList<String> friendsNames = new ArrayList<>();
        ArrayList<UserGroup> groupNames = new ArrayList<>();
        try {
            ResponseList<Friend> friends = fb.getFriends();
            ResponseList<facebook4j.Group> groups = fb.groups().getGroups();

            for (Friend friend : friends) {
                friendsNames.add(friend.getName());
            }
            for(facebook4j.Group group : groups){
                if(!dao.userHasLeft(group.getName(),fb.getMe().getName())) {
                    UserGroup g = new UserGroup();
                    g.setUserGroupName(group.getName());
                    ArrayList<String> userInGroup = new ArrayList<>();
                    for (User u : fb.groups().getGroupMembers(group.getId())) {
                        System.out.println("Users in group " + u.getName());
                        if (!dao.userHasLeft(g.getUserGroupName(), u.getName())) {
                            userInGroup.add(u.getName());
                        }
                    }
                    g.setUsersInGroup(userInGroup);
                    if (serverGroups.containsKey(g.getUserGroupName())) {
                        ArrayList<String> newUsers = g.getUsersInGroup();
                        UserGroup currentGroup = serverGroups.get(g.getUserGroupName());
                        for (String newUser : newUsers) {
                            if (!currentGroup.getUsersInGroup().contains(newUser)) {
                                currentGroup.getUsersInGroup().add(newUser);
                            }
                        }
                    } else {
                        serverGroups.put(g.getUserGroupName(), g);
                    }

                    groupNames.add(g);
                }
            }
            returnUser = new UserDetails(friendsNames,groupNames,fb.getMe().getName());
        } catch (FacebookException e) {
            e.printStackTrace();
        }
        for(UserGroup w : serverGroups.values()){
            for(String q : w.getUsersInGroup()){
                System.out.println("Users in group " + w.getUserGroupName() + " : " + q);
            }
        }

        return  returnUser;
    }

    public void removeChatClient(String clientName) throws RemoteException{
        System.out.println("Removing : " + clientName);
        this.clients.remove(clientName);
    }

    @Override
    public void shareFileWithUser(String fromUsername, String toUsername, File f) {
        System.out.println("Sending file from " + fromUsername + " to " + toUsername);
        dao.uploadUserFile(fromUsername,toUsername,f);
    }

    @Override
    public void sendUserMessage(String message, String fromUsername, String toUsername) throws RemoteException {
        Boolean isSistemMessage = false;
        System.out.println("Message : " + message + " fromUserName " + fromUsername + " toUsername " + toUsername);
        ChatClientInterface fromClient = clients.get(fromUsername).getChatClientInterface();


        if(message.startsWith("/groupRequests")){
            fromClient.retrieveUserMessage(dao.getGroupRequests(toUsername),"Server");
        }

        if(message.startsWith("/acceptRequest")){
            String requestedUser = message.substring("/acceptRequest ".length());
            if(dao.userHasLeft(toUsername,requestedUser)){
                dao.deleteUserLeaving(requestedUser,toUsername);
            }
            dao.deleteUserRequest(requestedUser,toUsername);
            serverGroups.get(toUsername).getUsersInGroup().add(requestedUser);
        }

        if(message.startsWith("/joinGroup")){
            isSistemMessage = true;
            String groupName = message.substring("/joinGroup ".length());


            if(!serverGroups.get(groupName).getUsersInGroup().contains(fromUsername)){
                dao.addUserJoinRequest(groupName,fromUsername);
            }
            for(String usr : serverGroups.get(groupName).getUsersInGroup()){
                if(clients.get(usr) != null){
                    String rqMessage = formatStringForChat("Server","User " + fromUsername + " requests to join group " + groupName);
                    dao.putGroupMessage(fromUsername,groupName,rqMessage);
                    clients.get(usr).getChatClientInterface().retrieveUserMessage(rqMessage,groupName);
                }
            }
        }
        if(message.startsWith("/leaveGroup")){
            isSistemMessage = true;
            if(serverGroups.get(toUsername).getUsersInGroup().contains(fromUsername)){
                serverGroups.get(toUsername).getUsersInGroup().remove(fromUsername);
                dao.addUserLeaveRequest(toUsername,fromUsername);
            }
        }

        if(message.startsWith("/files")){
            isSistemMessage = true;
            fromClient.retrieveUserMessage(dao.getSharedUserFiles(fromUsername,toUsername),"Server");

        }

        if(message.startsWith("/groupFiles")){
            fromClient.retrieveUserMessage(dao.getSharedGroupFiles(toUsername),"Server");
        }

        if(message.startsWith("/getFile")){
            isSistemMessage = true;
            System.out.println("File id : " + message.substring("/getFile ".length()));
            ChatClientInterface toClient = null;
            int fileId = Integer.decode(message.substring("/getFile ".length()));
            UserFile userFile = dao.getSimpleFile(fileId);

            if(clients.containsKey(userFile.getFromUsername())){
                System.out.println("User is found");
                toClient = clients.get(userFile.getFromUsername()).getChatClientInterface();
            }
            if(toClient == null){
                userFile = dao.getFullFile(fileId);
                System.out.println("user file " + userFile.getFileName());
                fromClient.retrieveFileFromServer(userFile.getFileContent(),userFile.getFileName());
            }else{
                try {
                    fromClient.retrieveFileFromUser(toClient,userFile.getFileName(),userFile.getFilePath());
                } catch (FileNotFoundException e) {
                    System.out.println("File not found sending file from server");
                    fromClient.retrieveFileFromServer(userFile.getFileContent(),userFile.getFileName());
                }
            }
        }

        if(message.startsWith("/postStatusMessage")){
            isSistemMessage = true;
            String statusMessage = message.substring("/postStatusMessage ".length());
            try {
                clients.get(fromUsername).getFacebook().postStatusMessage(statusMessage);
            } catch (FacebookException e) {
                e.printStackTrace();
            }
        }
        if(!isSistemMessage && !message.startsWith("/")){
            String formattedMessage = formatStringForChat(fromUsername,message);
            dao.putUserMessage(fromUsername,toUsername,formattedMessage);
            fromClient.retrieveUserMessage(formattedMessage,fromUsername);
            ChatClientInterface toClient = clients.get(toUsername).getChatClientInterface();
            if(toClient!= null)
                toClient.retrieveUserMessage(formattedMessage,fromUsername);
        }
    }

    @Override
    public void sendGroupMessage(String message, String fromUsername, String groupName) throws RemoteException {
        String formattedMessage = formatStringForChat(fromUsername,message);
        if(!dao.userHasLeft(groupName,fromUsername)) {
            UserGroup group = serverGroups.get(groupName);
            dao.putGroupMessage(fromUsername, groupName, formattedMessage);
            for (String user : group.getUsersInGroup()) {
                if (clients.containsKey(user)) {
                    System.out.println("Sending message " + message + " to " + user);
                    clients.get(user).getChatClientInterface().retrieveUserMessage(formattedMessage, groupName);
                }
            }
        }else{
            clients.get(fromUsername).getChatClientInterface().retrieveUserMessage("You have left this group, message is not posted \n","Server");
        }
    }

    @Override
    public String getUserChatHistory(String fromUsername, String toUsername) throws RemoteException {
        return dao.getUserChatHistory(fromUsername,toUsername);
    }

    @Override
    public String getGroupChatHistory(String groupName) throws RemoteException {
        return dao.getGroupChatHistory(groupName);
    }


    private Facebook getFacebook(String code){
        final OAuth20Service service = new ServiceBuilder()
                .apiKey(appId)
                .apiSecret(appSecret)
                .callback("https://www.facebook.com/connect/login_success.html")
                .build(FacebookApi.instance());
        OAuth2AccessToken token = service.getAccessToken(code);
        ConfigurationBuilder confBuild = new ConfigurationBuilder();
        confBuild.setDebugEnabled(true);
        confBuild.setOAuthAppId(appId);
        confBuild.setOAuthAppSecret(appSecret);
        confBuild.setJSONStoreEnabled(true);
        confBuild.setOAuthPermissions("user_birthday, user_religion_politics, user_relationships, user_relationship_details, user_hometown, user_location, user_likes, user_education_history, user_work_history, user_website, user_managed_groups, user_events, user_photos, user_videos, user_friends, user_about_me, user_status, user_games_activity, user_tagged_places, user_posts, read_page_mailboxes, email, read_insights, manage_pages, publish_pages, pages_show_list, pages_manage_cta, pages_messaging, pages_messaging_phone_number, publish_actions, read_audience_network_insights, user_actions.books, user_actions.music, user_actions.video, user_actions.news, user_actions.fitness, public_profile, basic_info");
        Facebook facebook = new FacebookFactory().getInstance(new OAuthAuthorization(confBuild.build()));
        AccessToken fbToken = new AccessToken(token.getAccessToken());
        System.out.println("Acces token : " + fbToken.getToken());
        facebook.setOAuthAccessToken(fbToken);
        return facebook;
    }

    private String formatStringForChat(String name,String s){
        return String.format("%-20s: %s\n" , name, s);
    }
}
