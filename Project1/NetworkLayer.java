/**
* This class simulates the network layer, which is conneccted with link layer
* In this layer, it simulates the transmission delay and propagation delay
* 
* This contains two methods: if the application is a server (should listen) or 
* false if it is a client (should try and connect) 
* 
* @author  Darren Norton, Yizhong Chen
* @since   Feb-19th-2017 
*/
public class NetworkLayer
{

    private LinkLayer linkLayer;
    private int dtrans;
    private int dprop;

    /**
             * This is a constructor for Network Layer
             * if the boolean is true, this is a server
             * if the boolean is false, this is a client, passed it to link layer
             * Get the delay time
             * 
             * @param boolean (server/client), transmission delay and propagation delay
             * 
             */
    public NetworkLayer(boolean server, int dprop, int dtrans)
    {
        this.dtrans = dtrans;
        this.dprop = dprop;
        linkLayer = new LinkLayer(server);

    }
    
    /**
             * simulate transmission delay and propagation delay
             * send the byte message to link layer to send out the bytes message to socket
             * 
             * @param byte message
             */
    public void send(byte[] payload)
    {
        try {
            Thread.sleep(dtrans * payload.length);
            Thread.sleep(dprop);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        linkLayer.send(payload);
    }
    
    /**
             * call link layer to read bytes from socket
             * 
             * @return the byte array received
             */
    public byte[] receive()
    {
        byte[] payload = linkLayer.receive();
        return payload;
    }
}
