import java.io.File;
import java.util.concurrent.BlockingQueue;

public class Model {

    private String username;
    private String conversation;
    private File file;
    private Client client;
    private BlockingQueue<Message> queue;
    private int port;
    // private String path;
    private String IPaddress;

    public Model(BlockingQueue<Message> q)
    {
        this.queue = q;
        this.client = null;
        this.IPaddress = "localhost";
    }

    public String getUsername() {
        return username;
    }

    public String getConversation() {
        return conversation;
    }

    public File getFile() {
        return file;
    }

    // public String getPath() {
    //     return path;
    // }

    // public void setPath(String path) {
    //     this.path = path;
    // }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void activateClient(){
        this.client = new Client(this.IPaddress, this.username, this.port, this.queue);
    }

    public void setConversation(String oldText, String newText) {
        conversation = oldText + "\n"  + newText;

    }

    public void setFile(File file) {
        this.file = file;
    }

    /*New codes*/
    public void sendText(String oldText, String newText) {
        client.sendText(newText);
        conversation = oldText + "\n" + getUsername() + ": " + newText;
    }

    public void sendFile(File file) {
        client.sendFile(file);
    }

    public void logout(){
        client.logout();
    }
}
