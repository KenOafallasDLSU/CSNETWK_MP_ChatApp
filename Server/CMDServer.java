/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.lang.*;
import java.net.*;
import java.io.*;
import java.nio.charset.*;

public class CMDServer
{
    static Logger loggerServer;
    static Logger loggerA;
    static Logger loggerB;
    static CMDClientHandler chA = new CMDClientHandler();
    static CMDClientHandler chB = new CMDClientHandler();
    static boolean userExists = true;
    static Object synchronizer = new Object();

    public static void main(String[] args) 
    {
        int nPort = 4000;
        
		try 
		{   
            ServerSocket serverSocket = new ServerSocket(nPort);
            String logName = "log-"+java.time.LocalDateTime.now()+".txt";
            CMDServer.loggerServer = new Logger(logName.replace(':','-'));
            CMDServer.loggerA = new Logger("logA.txt");
            CMDServer.loggerB = new Logger("logB.txt");

            Thread listenerA = new Thread(() -> {
                try {
                    do{
                        if(!CMDServer.chA.isLoggedIn)
                        { 
                            CMDServer.loggerServer.addLog("Server: Listening on port " + nPort + " at IPAddress localhost/127.0.0.1 for User A");
                            Socket userA = new Socket();
                            userA = serverSocket.accept();
                            CMDServer.loggerServer.addLog("Server: Client A at " + userA.getRemoteSocketAddress() + " has connected and logged in");

                            DataInputStream disA = new DataInputStream(userA.getInputStream()); 
                            DataOutputStream dosA = new DataOutputStream(userA.getOutputStream());

                            CMDServer.chA = new CMDClientHandler(userA, disA, dosA, true);

                            //reprint chat history if exists
                            CMDServer.chA.dos.writeUTF(CMDServer.loggerA.toString());

                            Thread tA = new Thread(CMDServer.chA);
                            tA.start();
                            tA.join();
                        }
                    }while(CMDServer.userExists);
                    
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            });

            Thread listenerB = new Thread(() -> {
                try {
                    do{
                        if (!CMDServer.chB.isLoggedIn)
                        {

                            CMDServer.loggerServer.addLog("Server: Listening on port " + nPort + " at IPAddress localhost/127.0.0.1 for User B");
                            Socket userB = new Socket();
                            userB = serverSocket.accept();
                            CMDServer.loggerServer.addLog("Server: Client B at " + userB.getRemoteSocketAddress() + " has connected and logged in");

                            DataInputStream disB = new DataInputStream(userB.getInputStream()); 
                            DataOutputStream dosB = new DataOutputStream(userB.getOutputStream());

                            CMDServer.chB = new CMDClientHandler(userB, disB, dosB, false);

                            //reprint chat history if exists
                            CMDServer.chB.dos.writeUTF(CMDServer.loggerB.toString());

                            Thread tB = new Thread(CMDServer.chB);
                            tB.start();
                            tB.join();
                        }
                    }while(CMDServer.userExists);

                } catch (Exception e) {
                    //e.printStackTrace();
                }
            });

            try {
                listenerA.start();
                listenerB.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            synchronized(CMDServer.synchronizer)
            {
                CMDServer.synchronizer.wait();
            }

            listenerA.interrupt();
            listenerA.join(1000);

            listenerB.interrupt();
            listenerB.join(1000);

		} catch (Exception e) {
            e.printStackTrace();
		} finally {
            CMDServer.loggerServer.addLog("Server: Both users have disconnected.");
            CMDServer.loggerServer.addLog("Server: Connection is terminated.");
            CMDServer.loggerServer.promptLogs();
            System.exit(0);
		}
  }
}

class CMDClientHandler implements Runnable
{
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isLoggedIn;
    boolean isUserA;

    public CMDClientHandler()
    {
        this.isLoggedIn = false;
        this.dis = null;
        this.dos = null;
        this.s = null;
    }

    public CMDClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, boolean b)
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.isLoggedIn = true;
        this.isUserA = b;
    }

    public void run()
    {
        String received;
        String[] tokens;

        while(this.isLoggedIn == true)
        {
            try{
                received = dis.readUTF();
                tokens = received.split(": ", 2);

                CMDServer.loggerB.addHistory(received);
                CMDServer.loggerA.addHistory(received);
                
                if(this.isUserA)
                {
                    if(!CMDServer.chB.isLoggedIn)
                        this.dos.writeUTF("Server: The other user is disconnected");
                    else
                    {
                        if(tokens[1].equals("FILE"))
                        {
                            this.relayText(CMDServer.chB.dos, received);
                            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " sending file to Client at " + CMDServer.chB.s.getRemoteSocketAddress());
                            CMDServer.loggerServer.addLog("Server: Client at " + CMDServer.chB.s.getRemoteSocketAddress() + " receiving file from Client at " + this.s.getRemoteSocketAddress());
                            this.relayFile(this.dis, CMDServer.chB.dos);
                        }else if(tokens[1].equals("LOGGED OUT (SYSTEM MESSAGE)")){
                            this.relayText(CMDServer.chB.dos, received);
                            this.logOut();
                        }else{
                            this.relayText(CMDServer.chB.dos, received);
                            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " sending message \"" + tokens[1] + "\" to Client at " + CMDServer.chB.s.getRemoteSocketAddress());
                        }
                    }
                }
                else
                {
                    if(!CMDServer.chA.isLoggedIn)
                        this.dos.writeUTF("Server: The other user is disconnected");
                    else
                    {
                        if(tokens[1].equals("FILE"))
                        {
                            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " sending file to Client at " + CMDServer.chA.s.getRemoteSocketAddress());
                            CMDServer.loggerServer.addLog("Server: Client at " + CMDServer.chA.s.getRemoteSocketAddress() + " receiving file from Client at " + this.s.getRemoteSocketAddress());
                            this.relayText(CMDServer.chA.dos, received);
                            this.relayFile(this.dis, CMDServer.chA.dos);
                        }else if(tokens[1].equals("LOGGED OUT (SYSTEM MESSAGE)")){
                            this.relayText(CMDServer.chA.dos, received);
                            this.logOut();
                        }else{
                            this.relayText(CMDServer.chA.dos, received);
                            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " sending message \"" + tokens[1] + "\" to Client at " + CMDServer.chA.s.getRemoteSocketAddress());
                        }
                    }
                }

            } catch(Exception e){
                this.isLoggedIn = false;

                if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                {
                    CMDServer.userExists = false;
                    synchronized(CMDServer.synchronizer)
                    {
                        CMDServer.synchronizer.notify();
                    }
                }

                //for other exception
                CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " has disconnected.");

            } finally{
                if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                {
                    CMDServer.userExists = false;
                    synchronized(CMDServer.synchronizer)
                    {
                        CMDServer.synchronizer.notify();
                    }
                }
                if(CMDServer.userExists == false)
                {
                    break;
                }
            }
        }

        try{
            this.dis.close(); 
            this.dos.close(); 
        } catch(Exception e){
            //e.printStackTrace(); 
        }
    }

    void relayText(DataOutputStream dos, String received)
    {
        try{
            dos.writeUTF(received);
        } catch(Exception e){
            //e.printStackTrace(); 
        }
        
    }

    void relayFile(DataInputStream dis, DataOutputStream dos)
    {
        try{

            int filesize = 1024*8;
            byte[] byteArrayR = new byte[filesize];

            int bytesRead;// = dis.read(byteArrayR,0,byteArrayR.length);
            boolean inFile = dis.readUTF().equals("FILEPART");

            System.out.println(inFile + " init status");

            while (inFile) {
                bytesRead = dis.read(byteArrayR,0,byteArrayR.length);

                dos.writeUTF("FILEPART");
                dos.write(byteArrayR, 0, bytesRead);
                dos.flush();
                
                inFile = dis.readUTF().equals("FILEPART");

                //System.out.println("Next is not last: "+ inFile + ": Read " + bytesRead + " bytes in one loop of Server.relayFile");
            }
            
            bytesRead = dis.read(byteArrayR,0,byteArrayR.length);
            dos.writeUTF("FILEEND");
            dos.write(byteArrayR, 0, bytesRead);
            //System.out.println("Finished Server.relayFile with " + bytesRead);
            dos.flush();

        } catch(Exception e){
            e.printStackTrace();
            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " failed sending file.");
        }
    }

    void logOut()
    {
        try{
            this.isLoggedIn=false; 
            this.s.close(); 
            if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                    CMDServer.userExists = false;
            CMDServer.loggerServer.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " has disconnected.");
        } catch(Exception e){
            //e.printStackTrace(); 
        }
        
    }
}
