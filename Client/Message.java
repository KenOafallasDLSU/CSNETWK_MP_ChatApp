/**
* @S13
* @author Michaela Nicole P. Dizon
* @author Kenneth Neil B. Oafallas
*/

public class Message
{
    private String message;
    private byte[] byteArray;

    public Message()
    {
        int filesize = 1048576;
        this.byteArray = new byte[filesize];
    }

    public void setText(String m)
    {
        this.message = m;
    }

    public void setBytes(byte[] b){
        this.byteArray = b;
    }

    public String getText(){
        return message;
    }

    public byte[] getBytes(){
        return byteArray;
    }
}