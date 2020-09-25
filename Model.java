import java.io.File;

public class Model {

    private CMDClient client;

    private String username;
    private int port;
    private String conversation;
    private File file;

//    public void connect(String name, int port) {
//        client = new CMDClient(name, port);
//        setUsername(name);
//        setPort(port);
//    }

    public String getUsername() {
        return username;
    }

    public String getConversation() {
        return conversation;
    }

    public File getFile() {
        return file;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setConversation(String oldText, String newText) {
        conversation = oldText + "\n" + getUsername() + ": " + newText;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
