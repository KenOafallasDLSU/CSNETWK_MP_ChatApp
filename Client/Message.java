/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

public class Message
{
    private String message;
    private byte[] byteArray;
    private int filesize;

    public Message()
    {
        this.filesize = 0;
    }

    public Message(int filesize)
    {
        this.filesize = filesize;
        this.byteArray = new byte[filesize];
    }

    public void setText(String m)
    {
        this.message = m;
    }

    public void setBytes(byte[] b){
        this.byteArray = b;
    }

    public int getFilesize(){
        return filesize;
    }

    public String getText(){
        return message;
    }

    public byte[] getBytes(){
        return byteArray;
    }
}