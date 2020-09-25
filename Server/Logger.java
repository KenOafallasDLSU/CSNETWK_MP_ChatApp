/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

import java.lang.*;
import java.net.*;
import java.io.*;
import javax.swing.*;

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
        System.out.println("Print the logs?? [Y]es [N]o");

        //hardcoded yes/no

        boolean print = false;
        int result = JOptionPane.showConfirmDialog(
            null, "Print the logs?", null,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

            if(result == JOptionPane.YES_OPTION){
                System.out.println("Yes");
                print = true;
            }else if (result == JOptionPane.NO_OPTION){
                System.out.println("No");
            }else {
                System.out.println("None selected");
        }

        if(print){
            String PATH = ".\\logs\\";
    
            File directory = new File(PATH);
            if (! directory.exists()){
                directory.mkdirs();
            }

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