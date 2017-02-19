/**
 * HTTP class which deals with all headings, append messages to locations of files
 */
public class HTTP
{
    int dprop;
    int dtrans;
    /**
     * Constructor for objects of class HTTP
     */
    public HTTP()
    {
        TransportLayer transportLayer = new TransportLayer(false, dprop, dtrans);
    }
    
    //a method to receive request message
    public void receive(byte[] message){
        
    }
    
    //a respond method to send byte array according to request
    public void respond(){
        
    }
}
