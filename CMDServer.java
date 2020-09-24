/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.lang.*;
import java.net.*;
import java.io.*;

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
            CMDServer.loggerServer = new Logger("log.txt");
            CMDServer.loggerA = new Logger("logA.txt");
            CMDServer.loggerB = new Logger("logB.txt");

            Thread listenerA = new Thread(() -> {
                try {
                    do{
                        //System.out.println("A is " + CMDServer.chA.isLoggedIn);
                        if(!CMDServer.chA.isLoggedIn)
                        { 
                            CMDServer.loggerServer.addLog("Server: Listening on port " + nPort + " for User A");
                            Socket userA = new Socket();
                            userA = serverSocket.accept();
                            CMDServer.loggerServer.addLog("Server: Client A at " + userA.getRemoteSocketAddress() + " has connected");

                            DataInputStream disA = new DataInputStream(userA.getInputStream()); 
                            DataOutputStream dosA = new DataOutputStream(userA.getOutputStream());

                            CMDServer.chA = new CMDClientHandler(userA, disA, dosA, true);

                            //reprint chat history if exists
                            CMDServer.chA.dos.writeUTF(CMDServer.loggerA.toString());

                            Thread tA = new Thread(CMDServer.chA);
                            tA.start();
                            tA.join();
                            System.out.println("listenerA does 1 listen loop");
                        }
                    }while(CMDServer.userExists);
                    System.out.println("A went out of loop");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread listenerB = new Thread(() -> {
                try {
                    do{
                        //System.out.println("B is " + CMDServer.chB.isLoggedIn);
                        if (!CMDServer.chB.isLoggedIn)
                        {

                            CMDServer.loggerServer.addLog("Server: Listening on port " + nPort + " for User B");
                            Socket userB = new Socket();
                            userB = serverSocket.accept();
                            CMDServer.loggerServer.addLog("Server: Client B at " + userB.getRemoteSocketAddress() + " has connected");

                            DataInputStream disB = new DataInputStream(userB.getInputStream()); 
                            DataOutputStream dosB = new DataOutputStream(userB.getOutputStream());

                            CMDServer.chB = new CMDClientHandler(userB, disB, dosB, false);

                            //reprint chat history if exists
                            CMDServer.chB.dos.writeUTF(CMDServer.loggerB.toString());

                            Thread tB = new Thread(CMDServer.chB);
                            tB.start();
                            tB.join();
                            System.out.println("listenerA does 1 listen loop");
                        }
                    }while(CMDServer.userExists);
                    System.out.println("B went out of loop");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            try {
                listenerA.start();
                listenerB.start();
    
                //listenerA.join();
                //listenerB.join();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //System.out.println("Before loop: " + CMDServer.userExists);
            synchronized(CMDServer.synchronizer)
            {
                CMDServer.synchronizer.wait();
            }
            //System.out.println("After loop: " + CMDServer.userExists);

            listenerA.interrupt();
            listenerA.join(1000);
            //System.out.println("A interrupted");

            listenerB.interrupt();
            listenerB.join(1000);
            //System.out.println("B interrupted");
            

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
                System.out.println("In CMDHandler run for A: " + this.isUserA );

                received = dis.readUTF();
                tokens = received.split(": ", 2);
                CMDServer.loggerServer.addLog(received);
                if(this.isUserA)
                {   
                    CMDServer.loggerB.addHistory(received);
                    CMDServer.loggerA.addHistory(tokens[1]);
                } else{
                    CMDServer.loggerA.addHistory(received);
                    CMDServer.loggerB.addHistory(tokens[1]);
                }
                    

                // if(received.equals("END"))
                // {
                //     this.logOut();
                // }
                //else
                //{
                    System.out.println(received);
                    if(this.isUserA)
                    {
                        if(!CMDServer.chB.isLoggedIn)
                            this.dos.writeUTF("Server: The other user is disconnected");
                        else
                        {
                            if(received.equals("FILE"))
                            {
                                this.relayFile();
                            }else if(received.equals("END")){
                                this.relayText(CMDServer.chB.dos, received);
                                this.logOut();
                            }else{
                                this.relayText(CMDServer.chB.dos, received);
                            }
                        }
                        
                            
                    }
                    else
                    {
                        if(!CMDServer.chA.isLoggedIn)
                            this.dos.writeUTF("Server: The other user is disconnected");
                        else
                        {
                            if(received.equals("FILE"))
                            {
                                this.relayFile();
                            }else if(received.equals("END")){
                                this.relayText(CMDServer.chA.dos, received);
                                this.logOut();
                            }else{
                                this.relayText(CMDServer.chA.dos, received);
                            }
                        }
                    }
                //}

            } catch(Exception e){

                //e.printStackTrace(); 
                this.isLoggedIn = false;
                if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                {
                    CMDServer.userExists = false;
                    synchronized(CMDServer.synchronizer)
                    {
                        CMDServer.synchronizer.notify();
                    }
                }
                System.out.println("Users Exist: " + CMDServer.userExists + " for User A: " + this.isUserA + " at catch");

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
                    System.out.println("Conditional break for User A: " + this.isUserA);
                    break;
                }
                    
                System.out.println("Users Exist: " + CMDServer.userExists + " for User A: " + this.isUserA);

            }
        }

        System.out.println("READ LOOP BROKEN for User A: " + this.isUserA);


        try{

            this.dis.close(); 
            this.dos.close(); 

        } catch(Exception e){

            e.printStackTrace(); 

        }

        System.out.println("THREAD DONE for User A: " + this.isUserA);
        
    }

    void relayText(DataOutputStream dos, String received)
    {
        try{
            dos.writeUTF(received);
        } catch(Exception e){
            
        }
        
    }

    void relayFile()
    {
        ;
    }

    void logOut()
    {
        try{
            this.isLoggedIn=false; 
            this.s.close(); 
            if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                    CMDServer.userExists = false;
        } catch(Exception e){
            
        }
        
    }
}
