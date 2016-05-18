import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Alex on 5/10/2016.
 */
public class UserDao {
    String url = "jdbc:mysql://localhost:3306/proiectIDP?useSSL=false";
    String username = "java";
    String password = "password";


    public UserDao(){
    }

    private Connection getConnection(){
        try {
            return DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void closeConnection(Connection conn){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getGroupChatHistory(String groupName){
        String sql = "SELECT user_name,message FROM GROUP_MESSAGES WHERE GROUP_NAME = ?";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,groupName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                response += rs.getString(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return  response;
    }

    public void addUserLeaveRequest(String groupName,String username){
        String sql = "INSERT INTO GROUP_LEAVE (USER_NAME,GROUP_NAME) VALUES (?,?)";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,groupName);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public void deleteUserLeaving(String username, String groupName){
        String sql = "DELETE FROM GROUP_LEAVE WHERE USER_NAME = ? AND GROUP_NAME = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,groupName);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public String getGroupRequests(String groupName){
        String sql = "SELECT * FROM GROUP_REQUESTS WHERE GROUP_NAME = ?";
        String response = "";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,groupName);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                response += "Nr. " + rs.getInt(1) + " | User  : " + rs.getString(2) + " | Request Date "  +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp(4))+ "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return response;
    }
    public void deleteUserRequest(String username, String groupName){
        String sql = "DELETE FROM GROUP_REQUESTS WHERE USER_NAME = ? AND GROUP_NAME = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,groupName);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public boolean userHasLeft(String groupName,String username){
        String sql = "SELECT COUNT(*) from GROUP_LEAVE where user_name = ? and group_name = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,groupName);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                if(rs.getInt(1) > 0){
                    return true;
                }else
                    return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return false;
    }

    public void addUserJoinRequest(String groupName,String username){
        String sql = "INSERT INTO GROUP_REQUESTS (USER_NAME,GROUP_NAME,REQUEST_DATE) VALUES (?,?,?)";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,groupName);
            stmt.setTimestamp(3,new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public String getUserChatHistory(String fromUsername, String toUsername){
        String sql = "SELECT * FROM USER_MESSAGES WHERE (FROM_USERNAME = ? AND TO_USERNAME = ?) OR (FROM_USERNAME = ? AND TO_USERNAME = ?)";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,fromUsername);
            stmt.setString(2,toUsername);
            stmt.setString(3,toUsername);
            stmt.setString(4,fromUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                response += rs.getString(4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return  response;
    }
    public void deleteOldFiles(){
        String sql = "DELETE FROM USER_FILES WHERE date_upload > SYSDATE - 15";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareCall(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }

    }

    public void deleteOldMessages(){
        String sql = "DELETE FROM USER_MESSAGES WHERE MESSAGE_DATE > SYSDATE - 15";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareCall(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public Timestamp lastPurge(){
        String sql = "SELECT LAST_PURGE FROM SERVER_DETAILS";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareCall(sql);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getTimestamp(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return null;
    }

    public void setLastPurge(Timestamp now){
        String sql = "UPDATE SERVER_DETAILS SET LAST_PURGE = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareCall(sql);
            stmt.setTimestamp(1,now);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public String getSharedGroupFiles(String groupName){
        String sql = "SELECT id,file_name,date_upload FROM USER_FILES WHERE TO_USERNAME = ?";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,groupName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                response += "Nr. " + rs.getInt(1) + " | File Name : " + rs.getString(2) + " | Upload Date "  +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp(3))+ "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return  response;
    }

    public String getSharedUserFiles(String fromUsername, String toUsername){
        String sql = "SELECT id,file_name,date_upload FROM USER_FILES WHERE (FROM_USERNAME = ? AND TO_USERNAME = ?) OR (FROM_USERNAME = ? AND TO_USERNAME = ?)";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,fromUsername);
            stmt.setString(2,toUsername);
            stmt.setString(3,toUsername);
            stmt.setString(4,fromUsername);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                response += "Nr. " + rs.getInt(1) + " | File Name : " + rs.getString(2) + " | Upload Date "  +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp(3))+ "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return  response;
    }
    public void putUserMessage(String fromUsername, String toUsername, String message){
        String sql = "INSERT INTO USER_MESSAGES (FROM_USERNAME, TO_USERNAME, MESSAGE,MESSAGE_DATE) VALUES (?,?,?,?);";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,fromUsername);
            stmt.setString(2,toUsername);
            stmt.setString(3,message);
            stmt.setTimestamp(4,new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public void putGroupMessage(String fromUsername, String groupName, String message){
        String sql = "INSERT INTO group_messages (user_name, group_name,MESSAGE_DATE,message) VALUES (?,?,?,?);";
        Connection conn = getConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,fromUsername);
            stmt.setString(2,groupName);
            stmt.setTimestamp(3,new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.setString(4,message);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
    }

    public void uploadUserFile(String fromUsername, String toUsername, File f){
        String sql = "INSERT INTO USER_FILES (FROM_USERNAME, TO_USERNAME,FILE_PATH,FILE_NAME, FILE,date_upload ) VALUES (?,?,?,?,?,?)";
        Connection conn = getConnection();

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            FileInputStream fis = new FileInputStream(f);
            stmt.setString(1,fromUsername);
            stmt.setString(2,toUsername);
            stmt.setString(3,f.getAbsolutePath());
            stmt.setString(4,f.getName());
            stmt.setBinaryStream(5,fis);
            stmt.setTimestamp(6,new Timestamp(Calendar.getInstance().getTimeInMillis()));
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
    }

    public UserFile getFullFile(int fileId){
        UserFile file = new UserFile();
        String sql = "select file_path,from_username, to_username,file_name,file from user_files where id = ?";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,fileId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                file.setFilePath(rs.getString(1));
                file.setFromUsername(rs.getString(2));
                file.setToUsername(rs.getString(3));
                file.setFileName(rs.getString(4));
                file.setFileContent(rs.getBytes(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return file;
    }
    public UserFile getSimpleFile(int fileId){
        UserFile file = new UserFile();
        String sql = "select file_path,from_username, to_username,file_name from user_files where id = ?";
        Connection conn = getConnection();
        String response = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,fileId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                file.setFilePath(rs.getString(1));
                file.setFromUsername(rs.getString(2));
                file.setToUsername(rs.getString(3));
                file.setFileName(rs.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeConnection(conn);
        }
        return file;
    }
}
