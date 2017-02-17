
public class NetworkLayer
{

    private LinkLayer linkLayer;
    private int dtrans;
    private int dprop;

    public NetworkLayer(boolean server, int dprop, int dtrans)
    {
        this.dtrans = dtrans;
        this.dprop = dprop;
        linkLayer = new LinkLayer(server);

    }
    public void send(byte[] payload)
    {
        try {
            Thread.sleep(dtrans * payload.length * 8);
            Thread.sleep(dprop);
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        linkLayer.send(payload);
    }

    public byte[] receive()
    {
        byte[] payload = linkLayer.receive();
        return payload;
    }
}
