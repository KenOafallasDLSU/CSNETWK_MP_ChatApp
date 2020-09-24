/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class CMDClient  
{ 
    static boolean isLoggedIn;

    public static void main(String args[]) 
    { 
        Scanner scn = new Scanner(System.in); 

        String name = args[0];
        int port = Integer.parseInt(args[1]);

        try{
            // obtaining input and out streams 
            Socket s = new Socket("localhost", port); 
            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
    
            CMDClient.isLoggedIn = true;
            // sendMessage thread 
            Thread sendMessage = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
                    while (CMDClient.isLoggedIn) { 
                        try{
                            // read the message to deliver. 
                            String msg = scn.nextLine(); 
                            
                            if(msg.equals("FILE"))
                            {
                                CMDClient.sendFile(scn, dos);
                            } else if(msg.equals("END")) {
                                CMDClient.logout(name, s, dis, dos);
                                CMDClient.isLoggedIn = false;
                            } else{
                                CMDClient.sendText(dos, name + ": " + msg);
                            }
                        } catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                        
                    } 
                } 
            }); 
            
            // readMessage thread 
            Thread readMessage = new Thread(new Runnable()  
            { 
                @Override
                public void run() { 
    
                    while (CMDClient.isLoggedIn) { 
                        try { 
                            // read the message sent to this client 
                            String msg = dis.readUTF(); 
                            if(msg.equals("FILE"))
                            {
                                CMDClient.receiveFile(scn, dis);
                            } else{
                                CMDClient.receiveText(msg);
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

    static void logout(String name, Socket s, DataInputStream dis, DataOutputStream dos)
    {
        System.out.println("You logged out");

        try { 
            dos.writeUTF(name + " HAS LOGGED OUT"); 

            dis.close();
            dos.close();
            s.close();
        } catch (Exception e) { 
            //e.printStackTrace(); 
        } 

        
    }

    static void sendFile(Scanner scn, DataOutputStream dos)
    {
        try { 
            dos.writeUTF("FILE"); 

            System.out.print("Enter filename: ");
            String fileName = scn.nextLine(); 

            dos.writeUTF(fileName); 

            File file = new File(fileName);
            byte[] byteArray = new byte [(int)file.length()];
			
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(byteArray,0,byteArray.length);
			DataInputStream disReader = new DataInputStream(bis);

            System.out.println("Sending file " + "\"" + fileName + "\" " + "(" + byteArray.length + " bytes)\n" );  
            dos.write(byteArray,0,byteArray.length);
            dos.flush();
        } catch(Exception e){
            e.printStackTrace();
        }
        
    }

    static void sendText(DataOutputStream dos, String msg)
    {
        try { 
            // write on the output stream 
            dos.writeUTF(msg); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    }

    static void receiveFile(Scanner scn, DataInputStream dis)
    {
        System.out.println("RECEIVE FILE");
        try{
            String fileName = dis.readUTF(); 

            System.out.println("Receiving file: " + fileName);

            System.out.print("Enter filename: ");
            fileName = scn.nextLine();
            //scn.nextLine();

            int filesize = 1048576;
            byte[] byteArray = new byte[filesize];

            FileOutputStream fos = new FileOutputStream(fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = dis.read(byteArray,0,byteArray.length);
    
            bos.write(byteArray, 0 , bytesRead);
            bos.flush();
            bos.close();
        } catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    static void receiveText(String msg)
    {
        if(!msg.equals(""))
            System.out.println(msg); 
    }
} 