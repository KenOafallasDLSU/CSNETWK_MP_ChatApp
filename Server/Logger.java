/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.lang.*;
import java.net.*;
import java.io.*;

public class Logger
{
    private StringBuilder log;
    private String fileName;

    public Logger(String fileName)
    {
        this.log = new StringBuilder("");
        this.fileName = fileName;
    }

    public void promptLogs()
    {
        JOptionPane.showMessageDialog(null, "message");
        System.out.println("Print the logs?? [Y]es [N]o");
        if(true)
        {
            try{
                //System.out.println(this.log.toString());

                File logFile = new File(".\\logs\\"+fileName);
                logFile.createNewFile();

                FileWriter logWriter = new FileWriter(".\\logs\\"+fileName);
                logWriter.write(this.log.toString());
                logWriter.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }
    }

    public void addLog(String s)
    {
        System.out.println("(" + java.time.LocalDateTime.now() + ") " + s);
        this.log.append("(" + java.time.LocalDateTime.now() + ") " + s + "\r\n");
    }

    public void addHistory(String s)
    {
        this.log.append(s + "\r\n");
    }

    @Override
    public String toString()
    {
        return this.log.toString();
    }
}