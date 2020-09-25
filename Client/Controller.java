import java.awt.event.*;
import java.util.concurrent.BlockingQueue;
import java.io.*;

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

                if(view.getFile() != null){
                    model.sendText(view.getChatArea(), "Sending file " + view.getFile().getName() + "...");
                    model.sendFile(view.getFile());
                }
                

            }
            if(e.getActionCommand().equals("Login")) {
                String user = view.getUsername();
                String ip = view.getIP();
                int port = view.getPort();

                if(!(user.equals(""))) {
                    if(ip.equals("")) {
                        view.message("IP cannot be blank.");
                    }
                    else if(ip.equals("localhost") || ip.equals("127.0.0.1")) {
                        if(port != 4000) {
                            view.message("Port is unavailable. Please connect to Port 4000");
                        }
                        else if(port == -1) {
                            view.message("Please input a valid port number.");
                        }
                        else {
                            model.setUsername(user);
                            model.setIP(ip);
                            model.setPort(port);
                            model.activateClient();
    
                            runAccepter();
    
                            view.login();
                        }
                    }
                    else {
                        view.message("Please connect to localhost or 127.0.0.1");
                    }
                }
                else {
                    view.message("Username cannot be blank!");
                }
            }
            if(e.getActionCommand().equals("Logout")) {
                view.logout();
                view.message("You have been logged out!");
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
                            Message msg = queue.take();

                            if(msg.getText().equals("FILEFILEFILE"))
                            {
                                view.saveFile();
                                model.setFile(view.getFile());
                                
                                String path = model.getFile().getAbsolutePath();

                                int filesize = msg.getFilesize();
                                byte[] byteArray = new byte[filesize];
                                FileOutputStream fos = new FileOutputStream(path);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);
                                bos.write(msg.getBytes(), 0 , filesize);
                                bos.flush();
                                bos.close();

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

        } catch(Exception e){
            //e.printStackTrace();
        }
    }

}