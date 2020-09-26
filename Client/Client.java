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

                                //System.out.println("Received str: " + str);
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
            //String fileName = "sendThis.jpg";

            //read file
            //String fileName = f.replace("\\","\\\\");
            //File file = new File(fileName);
            byte[] byteArray = new byte [1024*8];;
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(byteArray,0,byteArray.length);
            //DataInputStream disReader = new DataInputStream(bis);
            
            
            int count;
            while ((count = bis.read(byteArray,0,byteArray.length)) >= 1024*8)
            {
                dos.writeUTF("FILEPART");
                dos.write(byteArray, 0, count);
                dos.flush();
                //
                System.out.println("Read " + count + " bytes in one count of Client.sendFile");
            }
            
            //dos.writeUTF("FILEPART");
            /*
            int count = bis.read(byteArray,0,byteArray.length);
            System.out.println("Read " + count + " bytes in Client.sendFile");
            */
            dos.writeUTF("FILEEND");
            dos.write(byteArray, 0, count);
            System.out.println("Read " + count + " bytes in fileend of Client.sendFile");

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

    public void receiveFile()
    {
        //System.out.println("RECEIVE FILE");
        try{
            //String fileName = dis.readUTF(); 

            //System.out.println("Receiving file");

            //String fileName = "C:\\Users\\dell\\Desktop\\CCS Files\\Year 2 Term 3\\CSNETWK\\project\\CSNETWK_MP_ChatApp\\leFold\\gotcha.jpg";

            int filesize = 1024*8;
            byte[] byteArray;

            //FileOutputStream fos = new FileOutputStream(fileName);
            //BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead;
            
            while(dis.readUTF().equals("FILEPART"))
            {
                byteArray = new byte[filesize];
                bytesRead = dis.read(byteArray,0,byteArray.length);
                System.out.println("Read " + bytesRead + " bytes in one loop of Client.receiveFile");

                Message msg = new Message(bytesRead);
                msg.setText("FILEPART");
                msg.setBytes(byteArray);
                this.queue.put(msg);
            }

            byteArray = new byte[filesize];
            bytesRead = dis.read(byteArray,0,byteArray.length);
            System.out.println("Finished Client.receiveFile with " + bytesRead);

            Message msg = new Message(bytesRead);
            msg.setText("FILEEND");
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