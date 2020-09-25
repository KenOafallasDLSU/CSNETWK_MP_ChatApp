import java.awt.event.*;

public class Controller {

    private View view;
    private Model model;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;

        view.addButtonActionListener((ActionEvent e) -> {
            if(e.getActionCommand().equals("Send")) {
                if(!(view.getMessage().equals(""))) {
                    model.setConversation(view.getChatArea(), view.getMessage());
                    view.setChatArea(model.getConversation());
                    view.setMessage("");
                }
                else {
                    view.message("Please enter a message!");
                }
            }
            if(e.getActionCommand().equals("Send file")) {
                view.sendFile();

            }
            if(e.getActionCommand().equals("Save file")) {
                view.saveFile();
                model.setFile(view.getFile());
                System.out.println(model.getFile());

//                view.setTemp(model.getFile().getName());
            }
            if(e.getActionCommand().equals("Login")) {
                if(!(view.getUsername().equals(""))) {
                    model.setUsername(view.getUsername());
                    view.login();
                }
                else {
                    view.message("Username cannot be blank!");
                }
            }
            if(e.getActionCommand().equals("Logout")) {
                view.logout();
                view.message("You have been logged out!");
            }
        });
    }

}
