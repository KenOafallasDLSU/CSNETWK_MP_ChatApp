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
    static ServerButler jeeves;
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
            jeeves = new ServerButler();

            Thread listenerA = new Thread(() -> {
                try {
                    do{
                        //System.out.println("A is " + CMDServer.chA.isLoggedIn);
                        if(!CMDServer.chA.isLoggedIn)
                        { 
                            CMDServer.jeeves.addLog("Server: Listening on port " + nPort + " for User A\n");
                            Socket userA = new Socket();
                            userA = serverSocket.accept();
                            CMDServer.jeeves.addLog("Server: Client A at " + userA.getRemoteSocketAddress() + " has connected\r\n");

                            DataInputStream disA = new DataInputStream(userA.getInputStream()); 
                            DataOutputStream dosA = new DataOutputStream(userA.getOutputStream());

                            chA = new CMDClientHandler(userA, disA, dosA, true);

                            Thread tA = new Thread(chA);
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

                            CMDServer.jeeves.addLog("Server: Listening on port " + nPort + " for User B\n");
                            Socket userB = new Socket();
                            userB = serverSocket.accept();
                            CMDServer.jeeves.addLog("Server: Client B at " + userB.getRemoteSocketAddress() + " has connected\r\n");

                            DataInputStream disB = new DataInputStream(userB.getInputStream()); 
                            DataOutputStream dosB = new DataOutputStream(userB.getOutputStream());

                            chB = new CMDClientHandler(userB, disB, dosB, false);

                            Thread tB = new Thread(chB);
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

            System.out.println("Before loop: " + CMDServer.userExists);
            synchronized(CMDServer.synchronizer)
            {
                CMDServer.synchronizer.wait();
            }
            System.out.println("After loop: " + CMDServer.userExists);

            listenerA.interrupt();
            listenerA.join(1000);
            System.out.println("A interrupted");

            listenerB.interrupt();
            listenerB.join(1000);
            System.out.println("B interrupted");
            

		} catch (Exception e) {

            e.printStackTrace();
            
		} finally {

            CMDServer.jeeves.addLog("Server: Connection is terminated.\r\n");
            CMDServer.jeeves.promptLogs();
            System.exit(0);

		}
  }
}

class ServerButler
{
    StringBuilder log;

    ServerButler()
    {
        this.log = new StringBuilder("This is the log.\r\n");
    }

    void promptLogs()
    {
        System.out.println("Print the logz?? [Y]es [N]o");
        if(true)
        {
            try{
                System.out.println("What:" + this.log.toString());

                File logFile = new File("log.txt");
                logFile.createNewFile();

                FileWriter logWriter = new FileWriter("log.txt");
                logWriter.write(this.log.toString());
                logWriter.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }

    void addLog(String s)
    {
        System.out.println(s);
        this.log.append(s);
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
        while(this.isLoggedIn == true)
        {
            try{
                System.out.println("In CMDHandler run for A: " + this.isUserA );

                received = dis.readUTF();
                if(received.equals("END"))
                {
                    this.logOut();
                }
                else
                {
                    if(this.isUserA)
                    {
                        if(!CMDServer.chB.isLoggedIn)
                            this.dos.writeUTF("Server: The other user is disconnected");
                            
                        else
                        {
                            if(received.equals("FILE"))
                            {
                                this.relayFile();
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
                            }else{
                                this.relayText(CMDServer.chA.dos, received);
                            }
                        }
                        
                        
                            
                    }
                }

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

                CMDServer.jeeves.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " is connected: " + this.isLoggedIn + " :<\r\n");

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
