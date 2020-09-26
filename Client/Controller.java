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
                if(view.getFile() != null){
                    model.sendText(view.getChatArea(), "Sending file " + view.getFile().getName() + "...");
                    model.sendFile(view.getFile());
                }
                

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
                            //System.out.println("Took:" + msg.getText());

                            if(msg.getText().equals("FILEPART"))
                            {
                                view.saveFile();
                                model.setFile(view.getFile());

                                String path = model.getFile().getAbsolutePath();
                                FileOutputStream fos = new FileOutputStream(path);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);

                                //
                                System.out.println(path);

                                int filesize;
                                //byte[] byteArray = new byte[filesize];

                                while(msg.getText().equals("FILEPART"))
                                {
                                    filesize = msg.getFilesize();
                                    bos.write(msg.getBytes(), 0 , filesize);
                                    System.out.println("Wrote " + msg.getFilesize() + " in one loop of Controller receiver");

                                    msg = queue.take();
                                }

                                filesize = msg.getFilesize();
                                bos.write(msg.getBytes(), 0 , filesize);
                                System.out.println("Wrote " + msg.getFilesize() + " in fileend of Controller receiver");
                                
                                bos.flush();
                                bos.close();

                            } else if(msg.getText().equals("FILEEND")){
                                view.saveFile();
                                model.setFile(view.getFile());

                                String path = model.getFile().getAbsolutePath();
                                FileOutputStream fos = new FileOutputStream(path);
                                BufferedOutputStream bos = new BufferedOutputStream(fos);

                                int filesize = 1024*8;
                                //byte[] byteArray = new byte[filesize];
                                bos.write(msg.getBytes(), 0 , filesize);
                                System.out.println("Wrote " + msg.getFilesize() + " in fileend of Controller receiver");

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

            //readMessage.join(); 
        } catch(Exception e){
            //e.printStackTrace();
        }
    }

}