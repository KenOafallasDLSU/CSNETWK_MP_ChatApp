import java.io.File;

public class Model {

    private String username;
    private String conversation;
    private File file;

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

    public void setConversation(String oldText, String newText) {
        conversation = oldText + "\n" + getUsername() + ": " + newText;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
