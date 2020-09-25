/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Client  
{ 
    private boolean isLoggedIn;
    
    final String name;
    final int port;
    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BlockingQueue<Message> queue;

    public Client(String name, int port, BlockingQueue<Message> q)
    {
        this.name = name;
        this.port = port;
        this.queue = q;
        this.isLoggedIn = true;
        this.s = null;
        this.dos = null;
        this.dis = null;

        try{
            this.s = new Socket("localhost", port); 
            this.dis = new DataInputStream(s.getInputStream()); 
            this.dos = new DataOutputStream(s.getOutputStream()); 

            Thread readMessage = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
                String[] tokens;

                    while (isLoggedIn) { 
                        try { 
                            // read the message sent to this client 
                            String str = dis.readUTF(); 
                            tokens = str.split(": ", 2);
                            if(tokens[1].equals("FILE"))
                            {

                                receiveFile();

                            } else{

                                System.out.println("Received str: " + str);
                                receiveText(str);
                                
                            }
                            
                        } catch (Exception e) { 
                            //e.printStackTrace(); 
                        } 
                    } 
                } 
            }); 

            readMessage.start(); 

            //readMessage.join(); 
        } catch(Exception e){
            //e.printStackTrace();
        }
    }

    /*
    public void activateClient()
    { 
        try{
            // obtaining input and out streams 
            
    
            Client.isLoggedIn = true;
            // sendMessage thread 
            Thread sendMessage = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
                    while (Client.isLoggedIn) { 
                        try{
                            //read the message to deliver. 
                            //while(!scn.hasNextLine());
                            //System.out.println("has line: " + scn.hasNextLine());
                            
                            String msg = scn.nextLine(); 
                            
                            if(msg.equals("FILE"))
                            {
                                Client.sendFile(name, scn, dos);
                            } else if(msg.equals("END")) {
                                Client.logout(name, s, dis, dos);
                                Client.isLoggedIn = false;
                            } else{
                                Client.sendText(dos, name + ": " + msg);
                            }
                        } catch(Exception e)
                        {
                            //e.printStackTrace();
                        }
                        
                    } 
                } 
            }); 
            
            // readMessage thread 
            Thread readMessage = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
                String[] tokens;

                    while (Client.isLoggedIn) { 
                        try { 
                            // read the message sent to this client 
                            String msg = dis.readUTF(); 
                            tokens = msg.split(": ", 2);
                            if(tokens[1].equals("FILE"))
                            {
                                Client.receiveFile(scn, dis);
                            } else{
                                Client.receiveText(msg);
                            }
                            
                        } catch (Exception e) { 
                            //e.printStackTrace(); 
                        } 
                    } 
                } 
            }); 
    
            sendMessage.start(); 
            readMessage.start(); 

            sendMessage.join(); 
            readMessage.join(); 

        } catch(Exception e){
            //e.printStackTrace();
            System.out.println("Failed to connect to server. Please check if server is online.");
        } finally{
            //System.out.println(name + ": Connection is terminated.");
        }
    } 
    */

    public void logout()
    {
        //System.out.println("You logged out");

        try { 
            dos.writeUTF(name + ": LOGGED OUT (SYSTEM MESSAGE)"); 

            Message msg = new Message();
            msg.setText("ENDENDEND");

            this.queue.put(msg);
            this.isLoggedIn = false;

            dis.close();
            dos.close();
            s.close();
        } catch (Exception e) { 
            //e.printStackTrace(); 
        } 

        
    }

    public void sendFile(File file)
    {
        try { 
            dos.writeUTF(name +": FILE"); 
            dos.flush();

            //System.out.print("Enter filepath: ");
            //String fileName = scn.nextLine(); 
            String fileName = "sendThis.jpg";

            //read file
            //String fileName = f.replace("\\","\\\\");
            File file = new File(fileName);
            byte[] byteArray = new byte [(int)file.length()];
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(byteArray,0,byteArray.length);
			DataInputStream disReader = new DataInputStream(bis);

            //send file
            //System.out.println("Sending file " + "\"" + fileName + "\" " + "(" + byteArray.length + " bytes)\n" );  
            dos.write(byteArray,0,byteArray.length);
            dos.flush();

        } catch(Exception e){
            e.printStackTrace();
        }
        
    }

    public void sendText(String msg)
    {
        try { 
            // write on the output stream 
            this.dos.writeUTF(name + ": " + msg); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }

    public void receiveFile(byte[] byteArray)
    {
        //System.out.println("RECEIVE FILE");
        try{
            //String fileName = dis.readUTF(); 

            //System.out.println("Receiving file");

            //String fileName = "C:\\Users\\dell\\Desktop\\CCS Files\\Year 2 Term 3\\CSNETWK\\project\\CSNETWK_MP_ChatApp\\leFold\\gotcha.jpg";

            //int filesize = 1048576;
            //byte[] byteArray = new byte[filesize];

            //FileOutputStream fos = new FileOutputStream(fileName);
            //BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = dis.read(byteArray,0,byteArray.length);

            Message msg = new Message();
            msg.setText("FILEFILEFILE");
            msg.setBytes(byteArray);

            this.queue.put(msg);

            // System.out.println("received bytes: " + bytesRead);
    
            // bos.write(byteArray, 0 , bytesRead);
            // bos.flush();
            // bos.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    /*
    public void receiveText(String msg)
    {
        if(!msg.equals(""))
            System.out.println(msg); 
    }
    */
    public void receiveText(String str) {
        try{
            Message msg = new Message();
            msg.setText(str);
    
            this.queue.put(msg);
        } catch(Exception e)
        {
            //e.printStackTrace();
        }
        
    }

    public DataInputStream getDIS(){
        return this.dis;
    }
} 