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
    final String IPaddress;
    final String name;
    final int port;
    private Socket s;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BlockingQueue<Message> queue;

    public Client(String IPaddress, String name, int port, BlockingQueue<Message> q)
    {
        this.IPaddress = IPaddress;
        this.name = name;
        this.port = port;
        this.queue = q;
        this.isLoggedIn = true;
        this.s = null;
        this.dos = null;
        this.dis = null;

        try{
            this.s = new Socket(IPaddress, port); 
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
                                receiveText(str);
                            }
                            
                        } catch (Exception e) { 
                            //e.printStackTrace(); 
                        } 
                    } 
                } 
            }); 

            readMessage.start(); 
            this.dos.writeUTF(name + ": LOGGED IN (SYSTEM MESSAGE)"); 


            //readMessage.join(); 
        } catch(Exception e){
            //e.printStackTrace();
        }
    }

    public void logout()
    {
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

            byte[] byteArray = new byte [1024*8];;
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);            
            
            int count;
            while ((count = bis.read(byteArray,0,byteArray.length)) >= 1024*8)
            {
                dos.writeUTF("FILEPART");
                dos.flush();

                dos.write(byteArray, 0, count);
                dos.flush();
                
                //System.out.println("Read " + count + " bytes in one count of Client.sendFile");
            }
            
            dos.writeUTF("FILEEND");
            dos.flush();

            dos.write(byteArray, 0, count);
            dos.flush();

            //System.out.println("Read " + count + " bytes in fileend of Client.sendFile");

        } catch(Exception e){
            //e.printStackTrace();
        }
        
    }

    public void sendText(String msg)
    {
        try { 
            // write on the output stream 
            this.dos.writeUTF(name + ": " + msg); 
        } catch (IOException e) { 
            //e.printStackTrace(); 
        } 
    }

    public void receiveFile()
    {
        try{
            int filesize = 1024*8;
            byte[] byteArray;

            int bytesRead;
            
            while(dis.readUTF().equals("FILEPART"))
            {
                byteArray = new byte[filesize];
                bytesRead = dis.read(byteArray,0,byteArray.length);
                //System.out.println("Read " + bytesRead + " bytes in one loop of Client.receiveFile");

                Message msg = new Message(bytesRead);
                msg.setText("FILEPART");
                msg.setBytes(byteArray);
                this.queue.put(msg);
            }

            byteArray = new byte[filesize];
            bytesRead = dis.read(byteArray,0,byteArray.length);
            //System.out.println("Finished Client.receiveFile with " + bytesRead);

            Message msg = new Message(bytesRead);
            msg.setText("FILEEND");
            msg.setBytes(byteArray);
            this.queue.put(msg);
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

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