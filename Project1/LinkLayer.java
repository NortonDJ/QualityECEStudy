/**
* This class simulates the link layer, which is conneccted with physical layer
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class LinkLayer
{
    private PhysicalLayer physicalLayer;
    /**
             * This is a constructor for LinkLayer
             * if the boolean is true, this is a server
             * if the boolean is false, this is a client, passed it to physical layer
             * @param boolean (server/client)
             * 
             */
    public LinkLayer(boolean server)
    {
        physicalLayer = new PhysicalLayer(server);
    }
    
    /**
             * send the byte message to physical layer to send out the bytes message to socket
             * 
             * @param byte message
             */
    public void send(byte[] payload)
    {
        physicalLayer.send( payload );
    }
    
    /**
             * call physical layer to read bytes from socket
             * 
             * @return the byte array received
             */
    public byte[] receive()
    {
        byte[] payload = physicalLayer.receive();
        return payload;
    }
}
