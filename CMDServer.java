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
                        }
                    }while(CMDServer.chA.isLoggedIn || CMDServer.chB.isLoggedIn);
                    
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
                        }
                    }while(CMDServer.chA.isLoggedIn || CMDServer.chB.isLoggedIn);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            boolean inSession = false;
            do
            {
                if(!inSession){
                    inSession = true;

                    try {
                        listenerA.start();
                        listenerB.start();
            
                        // listenerA.join();
                        // listenerB.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                
                //System.out.println(CMDServer.userExists);
            } while(CMDServer.userExists);
            listenerA.interrupt();
            listenerB.interrupt();

		} catch (Exception e) {

            e.printStackTrace();
            
		} finally {

            CMDServer.jeeves.addLog("Server: Connection is terminated.\r\n");
            CMDServer.jeeves.promptLogs();

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
        while(isLoggedIn == true)
        {
            try{
                received = dis.readUTF();
                if(received == "END")
                {
                    this.isLoggedIn=false; 
                    this.s.close(); 
                }
                else
                {
                    if(this.isUserA)
                    {
                        if(CMDServer.chB.isLoggedIn)
                            CMDServer.chB.dos.writeUTF(received);
                        else
                            this.dos.writeUTF("Server: The other user has disconnected");
                    }
                    else
                    {
                        if(CMDServer.chA.isLoggedIn)
                            CMDServer.chA.dos.writeUTF(received);
                        else
                            this.dos.writeUTF("Server: The other user has disconnected");
                    }
                }

            } catch(Exception e){

                //e.printStackTrace(); 
                this.isLoggedIn = false;
                if(!CMDServer.chA.isLoggedIn && !CMDServer.chB.isLoggedIn)
                    CMDServer.userExists = false;
                CMDServer.jeeves.addLog("Server: Client at " + this.s.getRemoteSocketAddress() + " is connected: " + this.isLoggedIn + " :<\r\n");

            } finally{

            }
        }

        try{

            this.dis.close(); 
            this.dos.close(); 

        } catch(Exception e){

            e.printStackTrace(); 

        }
        
    }
}
