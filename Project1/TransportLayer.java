import java.util.Arrays;

public class TransportLayer
{
    private boolean connected;
    private NetworkLayer networkLayer;
    private int dtrans;
    private int dprop;

    //server is true if the application is a server (should listen) or false if it is a client (should try and connect)
    public TransportLayer(boolean server, int dprop, int dtrans)
    {
        this.connected = false;
        this.dtrans = dtrans;
        this.dprop = dprop;
        networkLayer = new NetworkLayer(server, dprop, dtrans);
    }

    public void send(byte[] payload)
    {
        if(connected == false){
            connect();
            System.out.println("Sending our original message!");
        }
        //printBytes(payload);
        networkLayer.send(payload);
    }

    public byte[] receive()
    {
        byte[] payload = networkLayer.receive();
        //printBytes(payload);
        if(Arrays.equals(payload,ByteArrayHelper.syn)){
            System.out.println("We received a syn! Sending the syn-ack!");
            networkLayer.send(ByteArrayHelper.synack);
            connected = true;
            payload = networkLayer.receive();
        }
        else{
            
        }
        return payload;
    }

    public void connect() {
        System.out.println("Attempting to form connection with server.");
        byte[] listen;
        do {
            System.out.println("Sending a syn.");
            networkLayer.send(ByteArrayHelper.syn);
            listen = networkLayer.receive();
        } while(Arrays.equals(ByteArrayHelper.synack,listen) == false);
        System.out.println("Received the synack! We're connected!");
        connected = true;
    }

    public void disconnect(){
        this.connected = false;
    }

    public void printBytes(byte[] payload){
        String s = "[";
        for(int i=0; i< payload.length; i++){
            s += payload[i] + ",";
        }
        s += "]";
        System.out.println(s);
    }

}
