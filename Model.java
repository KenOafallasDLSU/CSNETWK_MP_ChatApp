public class Model {

    private String username;
    private String conversation;

    public String getUsername() {
        return username;
    }

    public String getConversation() {
        return conversation;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setConversation(String oldText, String newText) {
        conversation = oldText + "\n" + getUsername() + ": " + newText;
    }
}
