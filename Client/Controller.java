import java.awt.event.*;
import java.util.concurrent.BlockingQueue;

public class Controller {

    private View view;
    private Model model;
    private BlockingQueue<Message> queue;

    public Controller(Model model, View view, BlockingQueue<Message> q) {
        this.model = model;
        this.view = view;
        this.queue = q;

        view.addButtonActionListener((ActionEvent e) -> {
            if(e.getActionCommand().equals("Send")) {
                if(!(view.getMessage().equals(""))) {
                    //model.setConversation(view.getChatArea(), view.getMessage());
                    model.sendText(view.getChatArea(), view.getMessage());
                    view.setChatArea(model.getConversation());
                    view.setMessage("");
                }
                else {
                    view.message("Please enter a message!");
                }
            }
            if(e.getActionCommand().equals("Send file")) {
                //invoke model
                view.sendFile();

            }
            if(e.getActionCommand().equals("Save file")) {
                view.saveFile();
                model.setFile(view.getFile());
                System.out.println(model.getFile());

//                view.setFileLabel(model.getFile().getName());
            }
            if(e.getActionCommand().equals("Login")) {
                String user = view.getUsername();
                int port = view.getPort();

                if(port != -1) {
                    if(!(user.equals(""))) {
                        model.setUsername(user);
                        model.setPort(port);
                        model.activateClient();

//                    model.connect(user, port);
                        runAccepter();

                        view.login();
                    }
                    else {
                        view.message("Username cannot be blank!");
                    }
                }
                else {
                    view.message("Please input a valid port number.");
                }
            }
            if(e.getActionCommand().equals("Logout")) {
                view.logout();
                view.message("You have been logged out!");
                //
                model.logout();
            }
        });
    }

    void runAccepter()
    {
        try{
            Thread accepter = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
                String[] tokens;
                boolean flag = true;

                    while (flag) { 
                        try { 
                            // read the message sent to this client 
                            //System.out.println("Waiting for take");
                            Message msg = queue.take();
                            System.out.println("Took:" + msg.getText());

                            if(msg.getText().equals("FILEFILEFILE"))
                            {
                                view.saveFile();
                                model.setFile(view.getFile());
                                
                                // model.setPath(view.getPath());
                                String path = model.getFile().getAbsolutePath();

                            } else if(msg.getText().equals("ENDENDEND")){

                                flag = false;

                            } else{

                                model.setConversation(view.getChatArea(), msg.getText());
                                view.setChatArea(model.getConversation());
                                
                            }
                            
                        } catch (Exception e) { 
                            //e.printStackTrace(); 
                        } 
                    } 
                } 
            }); 

            accepter.start(); 

            //readMessage.join(); 
        } catch(Exception e){
            //e.printStackTrace();
        }
    }

}